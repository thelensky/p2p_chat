import akka.actor.{ActorRef, ActorSystem}
import javafx.stage.Stage

class Model extends ModelTrait {
  private var ctxStage: Option[Stage] = None

  def setCtxStage(stage: Stage): Unit = ctxStage = Option(stage)

  def getCtxStage: Option[Stage] = ctxStage
}

object Model {
  var system: ActorSystem = _
  var hostUser: Option[User] = None
  var chatRooms: Set[ChatRoom] = Set()
  var mainChatView: View = _

  def apply(): Model = new Model()

  def setHostUser(user: User): Unit = hostUser = Option(user)
}

case class ChatRoom(withUser: User, ctx: View)