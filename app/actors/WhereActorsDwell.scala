package actors

import akka.actor.{Props, Actor}
import play.api.libs.concurrent.Akka

object Admin {
  case object View
  case object RemoteSystem
  case object Response
}
class Admin extends Actor {
  import Admin._

  def receive = {
    case View => Response
    case RemoteSystem => Response
  }
}

object WhereActorsDwell {

  val strictAdmin = Akka.system.actorOf(Props[Admin], "admin")

  //val lazyAdmin = Akka.system.actorOf(Props[Admin], "admin")

}


