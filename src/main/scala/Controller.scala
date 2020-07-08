import ChatListener.Init
import akka.actor.{ActorRef, ActorSystem}
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

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
    ctx.getModel.usersList.forEach(_.actorRef ! ChatListener.Chatting(message, ctx.getIsPrivateChat))
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

  def setHostUser(name: String): Unit = {
    val user: User = User(name, Model.frameActor)
    Model.hostUser = Option(user)
    user.actorRef ! Init()
  }


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

  // AKKA
  def setPort(port: Int): Unit = Model.port = port

  def setFrameActor(actorRef: ActorRef): Unit = Model.frameActor = actorRef

  def setSystem(system: ActorSystem): Unit = {
    println("system added")
    Model.system = system
  }
}
