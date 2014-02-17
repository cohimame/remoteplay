package myactors

import akka.actor.{ActorLogging, Actor}

object Protocol {
  case object Ask
  case object Response
}

class Pinger extends Actor with ActorLogging {
  import Protocol._

  def receive = {
    case Ask =>
      log info s"got Response from $sender"
      sender ! Response

    case Response =>
      log info s"got Response from $sender"
  }
}



