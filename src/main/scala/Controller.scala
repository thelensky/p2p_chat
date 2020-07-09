import ChatListener.Init
import akka.actor.{ActorContext, ActorRef, ActorSystem}
import akka.cluster.Member
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

object Controller {

  def deleteUser(member: Member, context: ActorContext): Unit = {
    val deadActor = context.actorSelection(member.address.toString + "/user/chatListener")
    Platform.runLater(() => {
      val model = Option(Model.mainChatView)
      model match {
        case Some(_) =>
          Model.mainChatView.getModel.usersList.removeIf(u => context.actorSelection(u.actorRef.path).equals(deadActor))
          Model.chatRooms
            .find(u => context.actorSelection(u.withUser.actorRef.path) == deadActor)
            .map(_.ctx.getModel.asInstanceOf[Model].getCtxStage.close())
        case None =>
      }
    })
  }

  def pushMsg(ctx: View, msg: String): Unit = {
    val message = Msg(msg, Model.hostUser.get)
    ctx.getModel.msgList.add(message)
    ctx.chatTable.scrollTo(Int.MaxValue)
    ctx.getModel.usersList.forEach(_.actorRef ! ChatListener.Chatting(message, ctx.getIsPrivateChat))
  }

  def publishMsg(chat: ChatListener.Chatting, context: ActorContext): Unit =
    Platform.runLater(() =>
      if (chat.isPrivate) {
        Model.chatRooms.find(_.withUser.actorRef eq context.sender()) match {
          case Some(chR) => chR.ctx.getModel.msgList.add(chat.msg)
          case None =>
            val ctx: View = openPrivateChat(chat.msg.user)
            ctx.getModel.msgList.add(chat.msg)
        }
      } else {
        Model.mainChatView.getModel.msgList.add(chat.msg)
      }
    )

  def openPrivateChat(withUser: User): View = {
    Model.chatRooms.find(_.withUser == withUser) match {
      case Some(chatRoom) =>
        chatRoom.ctx.getModel.asInstanceOf[Model].getCtxStage.requestFocus()
        chatRoom.ctx
      case None =>
        val stage = new Stage()
        val ctx: View = chatRoom(stage, s"p2p private chat with ${withUser.name}")
        ctx.initPrivateChat(true)
        ctx.getModel.usersList.add(withUser)
        val room: ChatRoom = ChatRoom(withUser, ctx)
        Model.chatRooms += room
        stage.setOnCloseRequest(_ => {
          Model.chatRooms -= room
        })
        ctx
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
    Model.system = system
  }
}
