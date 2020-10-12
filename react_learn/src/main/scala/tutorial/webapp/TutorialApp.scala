package tutorial.webapp

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom

object Square {

  case class State(value: String = "")

  class Backend(bs: BackendScope[Int, State]) {
    def render(s: Int, state: State) =
      <.button(
        ^.cls := "square",
        ^.id := s"square$s",
        ^.onClick --> bs.setState(State("X")),
        state.value
      )

  }
  val component = ScalaComponent
    .builder[Int]("Square")
    .initialState(State())
    .renderBackend[Backend]
    .build

  def apply(i: Int) = component(i)
}

object Board {
  def renderSquare(i: Int) = Square(i)

  val component = ScalaComponent
    .builder[Unit]("Board")
    .renderStatic(
      {
        val status = "Next player: X"
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
    )
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
