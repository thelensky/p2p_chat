import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.{Button, TextArea}

class View {

  @FXML
  var msgField: TextArea = _
  var enterMsg: Button = _

  def sendMsg(actionEvent: ActionEvent): Unit = {
    println("Hi " + msgField.getText())
  }

}

object View {

}
