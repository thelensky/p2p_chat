import akka.actor.ActorRef
import javafx.collections.{FXCollections, ObservableList, ObservableSet}

case class User(name: String, actorRef: ActorRef)

case class Msg(msg: String, user: User)

trait ModelTrait {
  val msgList: ObservableList[Msg] = FXCollections.observableArrayList()
  val usersList: ObservableList[User] = FXCollections.observableArrayList()
}

