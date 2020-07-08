import akka.actor.{ActorSystem, Props}
import com.typesafe.config.{Config, ConfigFactory}
import javafx.application.{Application, Platform}
import javafx.application.Application.launch
import javafx.stage.Stage

import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters._

class MainApp extends Application {
  override def start(primaryStage: Stage): Unit = {
    val view: View = Controller.chatRoom(primaryStage, "p2p chat")
    Model.mainChatView = view
    primaryStage.setOnCloseRequest(_ => {
      Model.system.terminate()
      Platform.exit()
    })
  }
}

object MainApp {
  def main(args: Array[String]): Unit = {
    val conf: Config = ConfigFactory.load("application.conf")
    val listOfPorts: mutable.Buffer[Int] = conf.getIntList("seed-ports").asScala.map(_.toInt)
    val hostname: String = conf.getString("hostname")
    val system = ActorSystem()
    val props = PortChecker.props(hostname, listOfPorts)
    props.withDispatcher("javafx-dispatcher")
    system.actorOf(props, "check-port-actor")
    system.whenTerminated.map(_ => {
      val config = ConfigFactory.parseString(s"""akka.remote.artery.canonical.port=${Model.port}""")
        .withFallback(ConfigFactory.load("cluster-akka.conf"))
      val system = ActorSystem("ClusterSystem", config)
      val props = Props[ChatListener]

      Controller.setSystem(system)
      system.actorOf(props, "chatListener")
    })(ExecutionContext.global)
    launch(classOf[MainApp], args: _*)
  }
}