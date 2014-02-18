import akka.actor.{ActorRef, Props, ActorSystem}
import com.typesafe.config.ConfigFactory
import myactors.Pinger
import myactors.Pinger.Ask
import play.api._

object Global extends GlobalSettings {

  var system: ActorSystem = _



  override def onStart(app: Application) {
    Logger.info("Application has started")

    system = ActorSystem("agent",
      ConfigFactory.load.getConfig("PassiveAgent"))
      //ConfigFactory.load.getConfig("ActiveAgent"))

    val strictActor = system.actorOf(Props[Pinger], "passive")

    //val passiveAgent = system.actorSelection(
    //  "akka.tcp://agent@0.0.0.0:8000/user/passive")

    //passiveAgent tell (Ask, strictActor)
  }

  override def onStop(app: Application) {
    Logger.info("Application has stopped")
    system.shutdown()
  }




}