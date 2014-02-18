package agent

import akka.actor.{ActorSystem,Props}

import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory

import myactors.Pinger
import myactors.Pinger._

object ActiveAgent {
  def main(args: Array[String]) {

    val system = ActorSystem("agent",
      ConfigFactory.load.getConfig("ActiveAgent")
    )

    val activeAgent = system.actorOf(Props[Pinger])

    val passiveAgent = system.actorSelection(
      "akka.tcp://agent@0.0.0.0:8000/user/passive")


    //activeAgent ! Ask

    passiveAgent tell (Ask , activeAgent)

    import system.dispatcher
    system.scheduler.scheduleOnce( 10 seconds ){ system.shutdown() }
  }

}
