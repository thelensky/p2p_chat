import akka.actor.ActorRef
import javafx.collections.{FXCollections, ObservableList}

case class User(name: String, actorRef: Option[ActorRef])

case class Msg(msg: String, user: User)

case class ChatRoom(host: User, guest: User)

object Model {
  val msgList: ObservableList[Msg] = FXCollections.observableArrayList()
  val usersList: ObservableList[User] = FXCollections.observableArrayList()
  var hostUser: User = null
}


