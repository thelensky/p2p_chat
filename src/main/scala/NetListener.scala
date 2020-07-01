import NetListener.{PrivateMsg, PublicMsg}
import akka.actor.{Actor, ActorRef}
import akka.cluster.ClusterEvent.{MemberJoined, MemberUp}

object NetListener {

  trait Messages

  case class PublicMsg(msg: String) extends Messages

  case class PrivateMsg(msg: String, whom: ActorRef) extends Messages

  case class GetName() extends Messages

}

class NetListener extends Actor {
  override def receive: Receive = {
    case MemberUp =>
    case PublicMsg(msg) =>
      println(msg)
    case PrivateMsg(msg, whom) => println(msg, whom)
  }
}
