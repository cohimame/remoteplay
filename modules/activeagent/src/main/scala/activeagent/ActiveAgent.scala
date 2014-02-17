package agent

import akka.actor.{ActorSystem,Props}

import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory

import myactors.Pinger


object ActiveAgent {
  def main(args: Array[String]) {

    val system = ActorSystem(
      "agent",
      ConfigFactory.load.getConfig("ActiveAgent")
    )

    val server = system.actorFor(
      s"akka://application@localhost:2552/user/scheduler")


    val agent = system.actorOf(Props[Pinger])



    import system.dispatcher
    system.scheduler.scheduleOnce( 5 seconds ){ system.shutdown() }
  }

}
