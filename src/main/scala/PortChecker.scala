import java.net.InetSocketAddress

import akka.actor.{Actor, Props}
import akka.io.{IO, Tcp}

import scala.collection.mutable

object PortChecker {
  def props(hostname: String, ports: mutable.Buffer[Int]): Props =
    Props(new PortChecker(hostname, ports: mutable.Buffer[Int]))
}

class PortChecker(hostname: String, ports: mutable.Buffer[Int]) extends Actor {

  import context.system

  val connection: Iterator[Int] = ports.iterator
  var port: Int = connection.next()

  IO(Tcp) ! Tcp.Connect(new InetSocketAddress(hostname, port))

  override def receive: Receive = {
    case Tcp.Connected(_, _) =>
      port = connection.next()
      IO(Tcp) ! Tcp.Connect(new InetSocketAddress(hostname, port))
    case Tcp.CommandFailed(_: Tcp.Connect) =>
      Controller.setPort(port)
      context.stop(self)
      context.system.terminate()
  }
}