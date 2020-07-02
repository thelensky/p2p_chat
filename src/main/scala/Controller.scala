import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.config.ConfigFactory
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

import scala.jdk.CollectionConverters._

object Controller {
  def publishMsg(msg: Msg, isPrivate: Boolean): Unit = {
    if (isPrivate) {
      Model.chatRooms.find(_.withUser.actorRef == msg.user.actorRef).map(_.ctx.getModel.msgList.add(msg))
    }
    else {
      Model.mainChatView.getModel.msgList.add(msg)
    }
  }

  def deleteUser(sender: ActorRef): Option[Unit] = {
    Model.mainChatView.getModel.usersList.removeIf(_.actorRef == sender)
    Model.chatRooms.find(_.withUser.actorRef == sender).map(_.ctx.getModel.asInstanceOf[Model].getCtxStage.close())
  }

  def init(ctx: View): Unit = {
    val user1 = User("Yuri", null)
    val user2 = User("Ivan", null)
    val model = ctx.getModel
    model.usersList.add(user1)
    model.usersList.add(user2)

    val msg_1 = Msg("Hi", user1)
    val msg_2 = Msg("Hi Yuri", user2)
    ctx.getModel.msgList.addAll(msg_1, msg_2)
    ()
  }

  def pushMsg(ctx: View, msg: String): Unit = {
    val message = Msg(msg, Model.hostUser.get)
    ctx.getModel.msgList.add(message)
    ctx.chatTable.scrollTo(Int.MaxValue)
    Model.hostUser.foreach(_.actorRef ! ChatListener.Chatting(message, ctx.getIsPrivateChat))
  }

  def openPrivateChat(withUser: User): Unit = {

    if (Model.chatRooms exists (chatRoom => chatRoom.withUser == withUser)) {
      Model.chatRooms.filter(chatRoom => chatRoom.withUser == withUser).foreach(chatRoom => {
        chatRoom.ctx.getModel.asInstanceOf[Model].getCtxStage.requestFocus()
      })
    } else {
      val stage = new Stage()
      val ctx: View = chatRoom(stage, s"p2p private chat with ${withUser.name}")
      ctx.initPrivateChat(true)
      ctx.getModel.usersList.add(withUser)
      val room: ChatRoom = ChatRoom(withUser, ctx)
      Model.chatRooms += room
      stage.setOnCloseRequest(_ => {
        Model.chatRooms -= room
      })
      ()
    }
  }

  def setHostUser(name: String, ref: ActorRef = null): Unit =
    Model.hostUser = Option(User(name, ref))


  def chatRoom(stage: Stage, title: String): View = {
    val fxmlLoader = new FXMLLoader(getClass.getResource("chat_room.fxml"))
    val root: Parent = fxmlLoader.load()
    val view = fxmlLoader.getController.asInstanceOf[View]
    view.getModel.asInstanceOf[Model].setCtxStage(stage)
    stage.setTitle(title)
    stage.setScene(new Scene(root))
    stage.show()
    view
  }

  def startConnectionToNet(): Unit = {
    val conf = ConfigFactory.load()
    val listOfPorts = conf.getIntList("seed-ports").asScala.map(i => i.toInt)

    val hostname: String = conf.getString("akka.remote.artery.canonical.hostname")
    val system = ActorSystem("Tcp")

    system.actorOf(PortChecker.props(hostname, listOfPorts.iterator))
  }
}
