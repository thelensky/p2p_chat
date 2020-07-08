import akka.actor.ActorRef
import javafx.collections.{FXCollections, ObservableList}

final case class User(name: String, actorRef: ActorRef) extends JsonSerializable

final case class Msg(msg: String, user: User) extends JsonSerializable

trait ModelTrait {
  val msgList: ObservableList[Msg] = FXCollections.observableArrayList()
  val usersList: ObservableList[User] = FXCollections.observableArrayList()
}

