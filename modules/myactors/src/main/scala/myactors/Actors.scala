package myactors

import akka.actor.{ActorRef, Actor}

object Protocol {
  case object Ask
  case object Response
  case class Target(ref: ActorRef)
}

class Admin extends Actor {
  import Protocol._

  def receive = {
    case Ask => Response
  }
}

class Agent extends Actor {
  import Protocol._

  def receive = {
    case Target(ref) => ref ! Ask
  }

}

