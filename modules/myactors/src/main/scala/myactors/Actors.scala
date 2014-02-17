package myactors

import akka.actor.{ActorLogging, Actor}


object Pinger {
  case object Ask
  case object Response
}

class Pinger extends Actor with ActorLogging {
  import Pinger._

  //Logger info s"$self initialized by Application"

  def receive = {
    case Ask =>

      log info s"got Ask from $sender"
      sender ! Response

    case Response =>
      log info s"got Response from $sender"
  }
}



