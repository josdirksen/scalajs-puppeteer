package example.puppeteer

import scala.scalajs.js
import scala.scalajs.js.Promise
import scala.scalajs.js.annotation.JSImport.Namespace
import scala.scalajs.js.annotation.{JSImport, ScalaJSDefined}

object PuppeteerFacade {

  val defaultOptions = new Options() {
    val executablePath = "/Users/jos/dev/git/workbench-example-app/target/scala-2.12/scalajs-bundler/main/node_modules/puppeteer/.local-chromium/mac-494755/chrome-mac/Chromium.app/Contents/MacOS/Chromium"
    val headless = false
  }

  @JSImport("puppeteer", Namespace)
  @js.native
  object Puppeteer extends Puppeteer

  @js.native
  trait Puppeteer extends js.Object {
    def launch(option: Options): Promise[Browser] = js.native
  }

  @js.native
  trait Browser extends js.Object {
    def newPage(): Promise[Page] = js.native
  }

  @js.native
  trait Page extends js.Object {
    def goto(url: String): Promise[Response] = js.native
    def $(selector: String): Promise[ElementHandle] = js.native
    def evaluate(function: String): Promise[String] = js.native
    def waitForNavigation(options: WaitForNavigationOptions) : Promise[Response] = js.native
  }

  @js.native
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

  @ScalaJSDefined
  trait Options extends js.Object {
    val executablePath: String
    val headless: Boolean
  }

  @ScalaJSDefined
  trait WaitForNavigationOptions extends js.Object {
    val waitUntil: String
  }

}
