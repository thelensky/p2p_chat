import java.net.URL
import java.util.ResourceBundle

import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.{Button, TextField}
import javafx.stage.Stage

class EnterForm extends Initializable {
  @FXML
  var userNameID: TextField = _
  @FXML
  var ipID: TextField = _
  @FXML
  var portID: TextField = _
  @FXML
  var btnConfirm: Button = _
  var stage: Stage = _

  @FXML
  def onConfirm(): Unit = {
    def getInt(str: String) = {
      try {
        Some(str.toInt)
      } catch {
        case _: NumberFormatException => None
      }
    }
    Controller.setHostUser(userNameID.getText, Controller.startCluster(Option(ipID.getText()), getInt(portID.getText())))
    this.stage.close()
  }

  override def initialize(location: URL, resources: ResourceBundle): Unit = {}

}
