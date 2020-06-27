import java.net.URL
import java.util.{Optional, ResourceBundle}

import javafx.beans.property.ReadOnlyStringWrapper
import javafx.event.ActionEvent
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.TableColumn.CellDataFeatures
import javafx.scene.control._
import javafx.scene.input.MouseEvent

class View extends Initializable {

  @FXML
  var msgField: TextArea = _
  @FXML
  var enterMsg: Button = _
  @FXML
  var chatTable: TableView[Msg] = _
  @FXML
  var userId: TableColumn[Msg, String] = _
  @FXML
  var msgId: TableColumn[Msg, String] = _
  @FXML
  var usersTable: TableView[User] = _
  @FXML
  var usersId: TableColumn[User, String] = _

  @FXML
  def sendMsg(actionEvent: ActionEvent): Unit = {
    Controller.sendMsg(msgField.getText())
    msgField.setText("")
  }

  @FXML
  def openPrivateChat(e: MouseEvent) = {
    println(usersTable.getSelectionModel.getSelectedItem.name)
    usersTable.getSelectionModel.clearSelection()
  }

  def userNameImpute(): Unit = {
    val dialog = new TextInputDialog("User name")

    dialog.setTitle(null)
    dialog.setHeaderText("Enter your name:")
    dialog.setContentText("Name:")

    val result: Optional[String] = dialog.showAndWait

    result.ifPresent((name: String) => Controller.setHostUserName(name))
  }


  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    userId.setCellValueFactory((msg: CellDataFeatures[Msg, String]) => {
      new ReadOnlyStringWrapper(msg.getValue.user.name)
    })
    msgId.setCellValueFactory((msg: CellDataFeatures[Msg, String]) => {
      new ReadOnlyStringWrapper(msg.getValue.msg)
    })
    msgId.setCellFactory(MsgWrapCell.WRAPPING_CELL_FACTORY)
    usersId.setCellValueFactory((user: CellDataFeatures[User, String]) => {
      new ReadOnlyStringWrapper(user.getValue.name)
    })

    chatTable.setItems(Model.msgList)
    usersTable.setItems(Model.usersList)
    chatTable.setSelectionModel(null)
    userNameImpute()
  }

}

object MsgWrapCell extends WrapCell[Msg] {}
