package example

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport.Namespace
import scala.scalajs.js.annotation.{JSImport, ScalaJSDefined}
import scala.scalajs.js.Promise
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js.timers._
import org.scalajs.dom


object RunMe extends scala.scalajs.js.JSApp {

  implicit def promiseToFuture[T](promise: Promise[T]): Future[T] = promise.toFuture

  @ScalaJSDefined
  trait Options extends js.Object {
    val executablePath: String
    val headless: Boolean
  }

  val options = new Options() {
    val executablePath = "/Users/jos/dev/git/workbench-example-app/target/scala-2.12/scalajs-bundler/main/node_modules/puppeteer/.local-chromium/mac-494755/chrome-mac/Chromium.app/Contents/MacOS/Chromium"
    val headless = false
  }


  val p = () => dom.window.alert("hello")
  val jsfun: js.Function0[Unit] = p

  def main(): Unit = {

    def testFunction() : Unit = {
     // donothing
    }

    val result: Future[String] = for {
      browser <- Puppeteer.launch(options)
      page <- browser.newPage()
      response <- page.goto("https://earthview.withgoogle.com/beegden-the-netherlands-2007")
      background <- page.$("div.background")
      handle <- page.$("a.pagination__link--next")
//      _ <- page.evaluate("alert('hello world')")
      _ <- page.evaluate(testFunction _)
      content <- { println(s"${response.ok}|${response.url}|${response.status}|${handle}"); response.text() }
      _ <- handle.click()
    } yield (content)


    result.recover {
      case exc => println(s"Boom: $exc")
    }

    // document.querySelector("a.pagination__link--next")

    result.map(content => println(s"Retrieved content: $content"))
  }

  @JSImport("puppeteer", Namespace)
  @js.native
  object Puppeteer extends Puppeteer

  @js.native
  trait Puppeteer extends js.Object {
    def launch(option:Options): Promise[Browser] = js.native
  }

  @js.native
  trait Browser extends js.Object {
    def newPage(): Promise[Page] = js.native
  }

  @js.native
  trait Page extends js.Object {
    def goto(url: String): Promise[Response] = js.native
    def $(selector: String): Promise[ElementHandle] = js.native
    def evaluate(function: String): Promise[Unit] = js.native
    def evaluate(function: js.Function0[Unit]): Promise[Unit] = js.native
  }

  trait ElementHandle extends js.Object {
    def click(): Promise[Unit] = js.native
  }

  @js.native
  trait Response extends js.Object {
    val ok: Boolean = js.native
    val url: String = js.native
    val status: Int = js.native
    def text(): Promise[String] = js.native
  }


}