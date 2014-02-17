package controllers

import play.api._
import play.api.mvc._
import actordwelling.WhereActorsDwell._

import akka.util.Timeout
import scala.concurrent.duration._
import akka.pattern.ask

object Application extends Controller {
  
  def index = Action {

   /*
    val adminActor = strictActor
    adminActor !  myactors.Pinger.Ask
   */

    implicit val timeout = Timeout(10 seconds)

    strictActor ?  myactors.Pinger.Ask

    strictActor ?  myactors.Pinger.Response

    //strictActor ?  myactors.Pinger.Ask


    Ok(views.html.index("Your new application is ready."))
  }
  
}