package agent

import akka.actor.{ActorSystem,Props}
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import actors.AgentActor
import actors.ServerMessages.Start

/*
 TODO:
  1. move ActorSystem name to config
  2. move root to config
*/
object Agent {
  def main(args: Array[String]) {

    val system = ActorSystem("agent", ConfigFactory.load.getConfig("Agent"))

    val server = system.actorFor(s"akka://application@localhost:2552/user/scheduler")


    val agent = system.actorOf(Props{ new AgentActor(server, root) })

    agent ! Start

    import system.dispatcher
    system.scheduler.scheduleOnce( 5 seconds ){ system.shutdown() }
  }

}
