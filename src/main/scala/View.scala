import java.net.URL
import java.util.ResourceBundle

import javafx.beans.property.ReadOnlyStringWrapper
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.TableColumn.CellDataFeatures
import javafx.scene.control._
import javafx.scene.input.{KeyCode, KeyEvent, MouseEvent}

class View extends Initializable {

  private var isPrivateChat: Boolean = false
  private val model: ModelTrait = Model()

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
  var scroll: ScrollPane = _
  @FXML
  var labelId: Label = _

  @FXML
  def handle(ke: KeyEvent): Unit = {
    if (ke.getCode.getCode equals KeyCode.ENTER.getCode) {
      sendMsg()
    }
  }

  @FXML
  def sendMsg(): Unit = {
    Controller.pushMsg(this, msgField.getText())
    msgField.setText("")
  }

  @FXML
  def openPrivateChat(): Unit = {
    if (usersTable.getSelectionModel != null && !isPrivateChat) {
      val user: Option[User] = Option(usersTable.getSelectionModel.getSelectedItem)
      user match {
        case user: Some[User] =>
          Controller.openPrivateChat(user.get)
          usersTable.getSelectionModel.clearSelection()
        case _ =>
      }
    }
  }

  def getIsPrivateChat: Boolean = isPrivateChat

  def initPrivateChat(flag: Boolean): Unit = {
    isPrivateChat = flag
    usersTable.setSelectionModel(null)
    usersId.setSortable(false)
  }

  def getModel: ModelTrait = model

  def initImpute(): Unit = {
    //    val dialog = new TextInputDialog("User name")
    //
    //    dialog.setTitle(null)
    //    dialog.setHeaderText("Enter your name:")
    //    dialog.setContentText("Name:")
    //
    //    val result: Optional[String] = dialog.showAndWait
    //    result.ifPresentOrElse((name: String) => {
    //      Controller.setHostUser(name)
    //    }, () => {
    //      Platform.exit()
    //      System.exit(0)
    //    })

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

    chatTable.setItems(model.msgList)
    usersTable.setItems(model.usersList)
    chatTable.setSelectionModel(null)
    //    Model.hostUser.getOrElse(initImpute)
  }

  def setLabelId(string: String) = this.labelId.setText(string)

}

object MsgWrapCell extends WrapCell[Msg] {}
