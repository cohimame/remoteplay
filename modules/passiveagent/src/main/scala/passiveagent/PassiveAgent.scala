package agent

import akka.actor.{ActorSystem,Props}

import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory

import myactors.Pinger


object PassiveAgent {
  def main(args: Array[String]) {

    val system = ActorSystem(
      "agent",
      ConfigFactory.load.getConfig("PassiveAgent")
    )

    val passiveAgent = system.actorOf(Props[Pinger],"passive")


    import system.dispatcher
    system.scheduler.scheduleOnce( 30 seconds ){ system.shutdown() }
  }

}
