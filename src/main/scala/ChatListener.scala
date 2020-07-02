import akka.actor.Actor
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

object ChatListener {

  trait Message


  case class IAm(user: User) extends Message

  case class Chatting(msg: Msg, isPrivate: Boolean) extends Message

}

case class ChatListener() extends Actor {

  import ChatListener._

  val cluster: Cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberUp(_) =>
      context.sender() ! IAm(Model.hostUser.get)
    case UnreachableMember(_) =>
      Controller.deleteUser(sender())
    case MemberRemoved(_, _) =>
      Controller.deleteUser(sender())
    case IAm(user: User) =>
      Model.mainChatView.getModel.usersList.filtered(_ == user).add(user)
    case Chatting(msg, isPrivate) => Controller.publishMsg(msg, isPrivate)
    case _ =>
  }
}
