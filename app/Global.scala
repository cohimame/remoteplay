import akka.actor.{ActorRef, Props, ActorSystem}
import com.typesafe.config.ConfigFactory
import myactors.Pinger
import myactors.Pinger.Ask
import play.api._

import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.concurrent.Akka
import play.api.Play.current


object Global extends GlobalSettings {
  override def onStart(app: Application) {
    Logger.info("Application has started")
    //val strictActor = Akka.system.actorOf(Props[Pinger], "passive")
  }

  override def onStop(app: Application) {
    Logger.info("Application has stopped")
  }

}

/*
  object Global extends GlobalSettings {

    var adminSystem: ActorSystem = _

    override def onStart(app: Application) {
      Logger.info("Application has started")

      adminSystem = ActorSystem("agent",
        ConfigFactory.load.getConfig("PassiveAgent"))
        //ConfigFactory.load.getConfig("ActiveAgent"))

      val strictActor = adminSystem.actorOf(Props[Pinger], "passive")

      //val passiveAgent = system.actorSelection(
      //  "akka.tcp://agent@0.0.0.0:8000/user/passive")

      //passiveAgent tell (Ask, strictActor)
    }

    override def onStop(app: Application) {
      Logger.info("Application has stopped")
      system.shutdown()
    }

  }

*/