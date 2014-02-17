package actordwelling

import akka.actor.{ActorSystem, Props, Actor}



//import play.api.libs.concurrent.Akka

import myactors.Pinger
import com.typesafe.config.ConfigFactory

object WhereActorsDwell {

  val system = ActorSystem("agent",
    ConfigFactory.load.getConfig("PassiveAgent")
  )

  lazy val strictActor = system.actorOf(Props[Pinger], "admin")

  //val lazy lazyActor = Akka.system.actorOf(Props[Admin], "admin")
}


