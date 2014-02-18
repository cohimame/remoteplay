package controllers

import play.api._
import play.api.mvc._
import akka.actor.{Props, ActorSystem}
import play.api.libs.concurrent.Akka
import myactors.Pinger
import play.api.Play.current

object Application extends Controller {


  def index = Action {

    val strictActor = Akka.system.actorOf(Props[Pinger], "passive")

    /*
    *
    *
    * val agentRef = Akka.system.actorFor(agent)
        Admin.scheduler ! AdminsFix(agentRef)
    * */




    Ok(views.html.index("Your new application is ready."))
  }

}

