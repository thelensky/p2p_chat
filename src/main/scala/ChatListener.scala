
import akka.actor.{Actor, ActorLogging, ActorSelection}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import javafx.application.Platform

object ChatListener {

  trait ChatCommands extends JsonSerializable

  final case class Init() extends ChatCommands

  final case class IAm(user: User) extends ChatCommands

  final case class Chatting(msg: Msg, isPrivate: Boolean) extends ChatCommands

}

class ChatListener() extends Actor with ActorLogging {
  Controller.setFrameActor(self)

  import ChatListener._

  var bufferActorRef: Set[ActorSelection] = Set()
  var bufferUsers: Set[User] = Set()

  val cluster: Cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberUp(member) =>
      val actorSel = context.actorSelection(member.address.toString + "/user/chatListener")
      val selfSel = context.actorSelection(self.path)
      val model = Option(Model.mainChatView)
      if (!selfSel.equals(actorSel)) {
        model match {
          case Some(_) => Model.hostUser.map(u => actorSel ! IAm(u))
          case None => bufferActorRef += actorSel
        }
      }
    case UnreachableMember(member) =>
      Controller.deleteUser(member, context)
    case MemberRemoved(member, _) =>
      Controller.deleteUser(member, context)

    case u: IAm =>
      val model = Option(Model.mainChatView)
      model match {
        case Some(_) =>
          Platform.runLater(() => {
            Model.mainChatView.getModel.usersList.add(u.user)
          })
          cluster.joinSeedNodes(Seq(sender().path.address))
        case None =>
          bufferUsers += u.user
      }
    case _: Init =>
      Platform.runLater(() => {
        bufferUsers.foreach(user =>
          Model.mainChatView.getModel.usersList.add(user)
        )
      })
      Model.hostUser.map(u => bufferActorRef.foreach(_ ! IAm(u)))
    case chat: Chatting => Controller.publishMsg(chat, context)
    case _: MemberEvent =>

  }
}
