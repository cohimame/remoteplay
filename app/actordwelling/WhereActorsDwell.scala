package actordwelling

import akka.actor.{Props, Actor}
import play.api.libs.concurrent.Akka

import myactors.Pinger

object WhereActorsDwell {
  val strictActor = Akka.system.actorOf(Props[Pinger], "admin")

  //val lazy lazyActor = Akka.system.actorOf(Props[Admin], "admin")
}


