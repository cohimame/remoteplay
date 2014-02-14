package agent.actors

import akka.pattern.pipe
import akka.actor._
import akka.actor.SupervisorStrategy.Restart

import scala.concurrent.Future
import scala.concurrent.duration._

import agent.utils.Utils

object ServerMessages {
  case object Start

  case class  AgentFilesystem(filelist: List[String])
  case class  Check(filelist: List[String])
  case object InitialCheckDone
  case class  PeriodicCheckFailed(fail: List[String])

  case object DoTheFix
  case object IMadeFix
}

object AgentActor {
  case object FileSystemRequest
  case class  FileSystem(filelist: List[String])

  case class  InitialCheckRequest(filelist: List[String])
  case class  InitialCheckSuccess(filelist: List[String])
  case object InitialCheckFail

  case class  PeriodicCheck(filelist: List[String], period: FiniteDuration)
  case object PeriodicCheckSuccess
  case class  PeriodicCheckFailure(result: List[String])

  case object PeriodicCheckStop

  val ONE_MINUTE: FiniteDuration = 1 minute

}

/**
    When AgentActor restarts, his children don't.
*/
class AgentActor(admin: ActorRef, root:String) extends Actor with ActorLogging {
  import ServerMessages._
  import AgentActor._

  log info s"admin's actor is $admin"

  override val supervisorStrategy = OneForOneStrategy() { case _ => Restart }

  override def preStart() {
    // TODO maybe move actorNames to config?
    context.actorOf(Props{new FileSystemActor(root)},"fsScanner")
    context.actorOf(Props[InitialCheckActor],"initChecker")
    context.actorOf(Props[PeriodicalCheckActor],"periodicalChecker")

    context.children foreach { context.watch }
  }

  override def preRestart(reason: Throwable, message: Option[Any]) = ()
  override def postRestart(reason: Throwable) = ()

  def receive = {

    case Start =>
      log info "got Start message"
      context.child("fsScanner") foreach { _ ! FileSystemRequest }

    case FileSystem(files) =>
      log info "got FileSystem message"
      admin ! AgentFilesystem(files)

    case Check(filelist) =>
      context.child("initChecker") foreach { _ ! InitialCheckRequest(filelist) }

    case InitialCheckSuccess(filelist) => {
      admin ! InitialCheckDone
      context.child("periodicalChecker") foreach {
        _ ! PeriodicCheck(filelist, ONE_MINUTE)
      }
    }

    case PeriodicCheckFailure(result) =>
      admin ! PeriodicCheckFailed(result)

    case DoTheFix =>
      admin ! IMadeFix

    case Terminated(deadActor) =>
      log debug s"${deadActor.path.name} has died, but restarting isn't implemented"

  }

}

class FileSystemActor(root: String) extends Actor with ActorLogging {
  import AgentActor._

  import context.dispatcher

  case class ResultFor(result: List[String], receiver: ActorRef)
  case class FailureFor(result: Throwable, receiver: ActorRef)

  def receive = {

    case  FileSystemRequest =>
      val master = sender
      Future { Utils.getFileSystem(root) } map {
        ResultFor(_ ,master)
      } recover {
        case t: Throwable => FailureFor(t, master)
      } pipeTo self

    case ResultFor(result, master) =>
      log debug ("sucess:" :: result).mkString("\n")
      master ! FileSystem(result)

    case FailureFor(t,master) =>
      log debug s"acquire filesystem inside future failed: $t "

  }

}

class InitialCheckActor extends Actor with ActorLogging {
  import AgentActor._
  import agent.model.DataStorage._

  import context.dispatcher

  case class ResultFor(files: List[String],
                       fileCRCmap: Map[String, Long],
                       receiver: ActorRef)

  case class FailureFor(result: Throwable, receiver: ActorRef)

  def receive = {

    case InitialCheckRequest(files) =>
      log info files.mkString("\n")

      val master = sender
      Future {
        Utils.generateMap(files)
      } map {
        ResultFor(files,_,master)
      } recover {
        case t: Throwable => FailureFor(t, master)
      } pipeTo self

    case ResultFor(files,result,master) =>
      filesCRCs = result
      master ! InitialCheckSuccess(files)

    case FailureFor(throwable,master) =>
      log debug s"initial check inside future failed: $throwable"

  }

}

class PeriodicalCheckActor extends Actor with ActorLogging {
  import AgentActor._
  import agent.model.DataStorage._
  import context.{dispatcher,system}

  var task: Option[Cancellable] = None
  var master: ActorRef = context.system.deadLetters

  case class OneMoreCheck(files: List[String], period: FiniteDuration)
  case class SuccessfulCheck(files:List[String], period: FiniteDuration, right: Boolean)
  case class FailedCheck(files:List[String], period: FiniteDuration, left: List[String])

  def receive = {

    case PeriodicCheck(files,period) =>
      task foreach ( _.cancel )
      master = sender
      self ! OneMoreCheck(files, period)

    case OneMoreCheck(files, period) => {
      val oldMap = Future( filesCRCs )
      val currentMap = Future( Utils.generateMap(files) )

      val result = for {
        oM <- oldMap
        cM <- currentMap
      } yield Utils.compareCRCMaps(oM,cM)

      result map {
        _ match {
          case Right(result) => SuccessfulCheck(files,period,result)
          case Left(error) => FailedCheck(files,period,error)
        }
      } pipeTo self

    }

    case SuccessfulCheck(files, period, right) =>
      log debug s"successful check ${files.mkString("\n")}"
      task = Some(system.scheduler.scheduleOnce(period, self, OneMoreCheck(files,period)))

    case FailedCheck(files, period, left) =>
      master ! PeriodicCheckFailure(left)

    case PeriodicCheckStop =>
      task foreach( t => t.cancel())

  }

}
