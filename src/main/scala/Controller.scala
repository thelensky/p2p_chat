import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

object Controller {

  def init(ctx: View): Unit = {
    val user1 = User("Yuri", None)
    val user2 = User("Ivan", None)
    ctx.getModel.usersList.addAll(user1, user2)

    val msg_1 = Msg("Hi", user1)
    val msg_2 = Msg("Hi Yuri", user2)
    ctx.getModel.msgList.addAll(msg_1, msg_2)
    ()
  }

  def pushMsg(ctx: View, msg: String): Unit = {
    val message = Msg(msg, Model.hostUser.get)
    ctx.getModel.msgList.add(message)
    ctx.chatTable.scrollTo(Int.MaxValue)
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

  def setHostUserName(name: String): Unit =
    Model.hostUser = Option(User(name, None))

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
}
