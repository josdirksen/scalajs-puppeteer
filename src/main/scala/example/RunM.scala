package example

import scala.concurrent.ExecutionContext.Implicits.global
import cats.implicits._

import scala.util.{Failure, Success}

object RunMe extends scala.scalajs.js.JSApp {

  import puppeteer.PuppeteerFacade._
  import PageTasks._

  def main(): Unit = {

    val initialPage = "https://earthview.withgoogle.com/beegden-the-netherlands-2007"

    // define the task to run
    val task = for {
      _          <- gotoInitialPage(initialPage)
      initialUrl <- getBackgroundImage
      otherUrls  <- getMultipleBackground(100)
    } yield (otherUrls :+ initialUrl)

    // run the task in the context of a page
    val result = for {
      browser <- Puppeteer.launch(defaultOptions)
      page <- browser.newPage()
      urls <- task.run(page)
    } yield (urls)


    result.onComplete {
      case Success(urls) => urls.foreach { println(_)}
      case Failure(exc) => println(s"Something went wrong executing the tasks: $exc")
    }
  }
}