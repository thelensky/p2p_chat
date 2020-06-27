object Controller {

  def init() = {
    val user1 = User("Yuri", None)
    val user2 = User("Ivan", None)
    Model.usersList.addAll(user1, user2)

    val msg_1 = Msg("Hi", user1)
    val msg_2 = Msg("Hi Yuri", user2)
    Model.msgList.addAll(msg_1, msg_2)
  }

  def sendMsg(msg: String) = {
    val newMsg = Msg(msg, Model.hostUser)
    Model.msgList.add(newMsg)
  }

  def setHostUserName(name: String) =
    Model.hostUser = new User(name, null)
}
