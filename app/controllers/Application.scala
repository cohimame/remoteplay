package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {
  
  def index = Action {
    import actors.WhereActorDwell._
    val adminActor = straightAdmin

    Ok(views.html.index("Your new application is ready."))
  }
  
}