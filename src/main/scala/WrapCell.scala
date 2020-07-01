import javafx.scene.control.Label

trait WrapCell [S] {

  import javafx.application.Platform
  import javafx.scene.control.TableCell
  import javafx.scene.control.TableColumn
  import javafx.scene.layout.VBox
  import javafx.util.Callback

  val WRAPPING_CELL_FACTORY: Callback[TableColumn[S, String], TableCell[S, String]] = new Callback[TableColumn[S, String], TableCell[S, String]]() {
    override def call(param: TableColumn[S, String]): TableCell[S, String] = {
      val tableCell = new TableCell[S, String]() {
        override protected def updateItem(item: String, empty: Boolean): Unit = {
          if (item eq getItem) return
          super.updateItem(item, empty)
          if (item == null) {
            super.setText(null)
            super.setGraphic(null)
          }
          else {
            super.setText(null)
            val l: Label = new Label(item)
            l.setWrapText(true)
            val box = new VBox(l)
            l.heightProperty.addListener((_, _, newValue) => {
                box.setPrefHeight(newValue.doubleValue + 7)
                Platform.runLater(() => this.getTableRow.requestLayout())
            })
            super.setGraphic(box)
          }
        }
      }
      tableCell
    }
  }
}
