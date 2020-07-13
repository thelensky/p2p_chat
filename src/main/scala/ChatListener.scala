
import akka.actor.{Actor, ActorLogging, Address, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

object ChatListener {

  trait ChatCommands extends JsonSerializable

  final case class Init() extends ChatCommands

  final case class AddMe() extends ChatCommands

  final case class IAm(user: User) extends ChatCommands

  final case class Chatting(msg: Msg, isPrivate: Boolean) extends ChatCommands

  def props(host: Option[String], port: Option[Int]): Props = Props(new ChatListener(host, port))
}

class ChatListener(host: Option[String], port: Option[Int]) extends Actor with ActorLogging {

  import ChatListener._

  val cluster: Cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])

    host match {
      case Some(h: String) =>
        port match {
          case Some(p: Int) =>
            val address = Address(
              cluster.system.provider.getDefaultAddress.protocol,
              cluster.system.provider.getDefaultAddress.system, h, p)
            cluster.joinSeedNodes(Seq(address))
          case None => cluster.joinSeedNodes(Seq(cluster.system.provider.getDefaultAddress))
        }
      case None => cluster.joinSeedNodes(Seq(cluster.system.provider.getDefaultAddress))
    }
  }

  val defaultPort: Option[Int] = cluster.system.provider.getDefaultAddress.port
  val defaultHost: Option[String] = cluster.system.provider.getDefaultAddress.host

  defaultHost.map(h => defaultPort.map(p => Controller.printHostAndPort(Model.mainChatView, h, p)))

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberUp(member) =>
      val actorSel = context.actorSelection(member.address.toString + "/user/chatListener")
      val selfSel = context.actorSelection(self.path)
      if (!selfSel.equals(actorSel)) {
        Model.hostUser.map(u => actorSel ! IAm(u))
      }
    case UnreachableMember(member) =>
      Controller.deleteUser(member, context)
    case MemberRemoved(member, _) =>
      Controller.deleteUser(member, context)

    case u: IAm =>
      Model.hostUser.map(user => {
        if (!user.equals(u.user)) {
          Controller.addUser(u.user)
        }
      })
    case chat: Chatting => Controller.publishMsg(chat, context)
  }
}
