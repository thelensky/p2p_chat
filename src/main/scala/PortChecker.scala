import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.io.{IO, Tcp}
import com.typesafe.config.ConfigFactory

object PortChecker {
  def props(hostname: String, ports: Iterator[Int]): Props =
    if (ports.hasNext) {
      Props(new PortChecker(hostname, ports))
    }
    else throw new RuntimeException("No ports exists!")

  def startup(port: Int): ActorRef = {
    val config = ConfigFactory.parseString(
      s"""
      akka.remote.artery.canonical.port=$port
      """).withFallback(ConfigFactory.load())

    val system = ActorSystem("ClusterSystem", config)
    system.actorOf(Props[ChatListener], "chatListener")
  }
}

class PortChecker(hostname: String, ports: Iterator[Int]) extends Actor {

  import context.system

  val port: Int = ports.next()

  val remote: InetSocketAddress = new InetSocketAddress(hostname, port)

  IO(Tcp) ! Tcp.Connect(remote)

  override def receive: Receive = {
    case Tcp.Connected(_, _) =>
      system.actorOf(PortChecker.props(hostname, ports))
      context.stop(self)
    case Tcp.CommandFailed(_: Tcp.Connect) =>
      try {
        if (Model.hostUser.get.name != null)
          Controller.setHostUser(Model.hostUser.get.name, PortChecker.startup(port))
      } catch {
        case _: Throwable => throw new RuntimeException("User undefined!")
      }
      context.stop(self)
  }
}


