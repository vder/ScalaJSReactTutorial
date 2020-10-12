package tutorial.webapp

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom

object Square {

  case class Props(value: String, onClick: Callback)

  class Backend(bs: BackendScope[Props, Unit]) {
    def render(p: Props) =
      <.button(
        ^.cls := "square",
        ^.onClick --> p.onClick,
        p.value
      )

  }
  val component = ScalaComponent
    .builder[Props]("Square")
    .renderBackend[Backend]
    .build

  def apply(value: Option[String], onClick: Callback) =
    component(Props(value.getOrElse(""), onClick))
}

object Board {

  case class State(nextMove: String, vals: Vector[Option[String]])

  class Backend(bs: BackendScope[Unit, State]) {

    val switchNextMove: PartialFunction[String, String] = {
      case "X" => "O"
      case "O" => "X"
    }

    def handleClick(i: Int) =
      bs.modState(s =>
          s.vals(i) match {
            case None    => State(switchNextMove(s.nextMove), s.vals.updated(i, Some(s.nextMove)))
            case _ => s
          }
      )

    def render(s: State) = {
      val status = s"Next player: ${s.nextMove}"
      def renderSquare(i: Int) = Square(s.vals(i), handleClick(i))
      <.div(
        <.div(
          ^.cls := "status",
          status
        ),
        <.div(
          ^.cls := "board-row",
          renderSquare(0),
          renderSquare(1),
          renderSquare(2)
        ),
        <.div(
          ^.cls := "board-row",
          renderSquare(3),
          renderSquare(4),
          renderSquare(5)
        ),
        <.div(
          ^.cls := "board-row",
          renderSquare(6),
          renderSquare(7),
          renderSquare(8)
        )
      )
    }

  }

  val component = ScalaComponent
    .builder[Unit]("Board")
    .initialState(State("X", Vector.fill(9)(None)))
    .renderBackend[Backend]
    .build

  def apply() = component()
}

object Game {
  val component = ScalaComponent
    .builder[Unit]("Game")
    .renderStatic(
      <.div(
        ^.cls := "game",
        <.div(
          ^.cls := "game-board",
          Board()
        ),
        <.div(
          <.div( /*Status*/ ),
          <.ol( /* TODO */ )
        )
      )
    )
    .build

  def apply() = component()
}

object TutorialApp {
  def main(args: Array[String]): Unit = {
    Game().renderIntoDOM(dom.document.getElementById("root"))
  }

}
