package example

import cats.data.ReaderT
import scala.concurrent.ExecutionContext.Implicits.global
import example.puppeteer.PuppeteerFacade.{Page, Response, WaitForNavigationOptions}

import scala.concurrent.Future
import scala.scalajs.js.Promise
import cats.implicits._


object PageTasks {

  type PageTask[T] = ReaderT[Future, Page, T]

  object PageTask {
    def apply[T](block: Page => Future[T]): ReaderT[Future, Page, T] = {
      ReaderT.apply[Future, Page, T](block)
    }
  }

  implicit def promiseToFuture[T](promise: Promise[T]): Future[T] = promise.toFuture

  def waitUntilNavigationDone(): PageTask[Response] = PageTask { page =>
    page.waitForNavigation(new WaitForNavigationOptions(){val waitUntil = "networkidle"})
  }

  def gotoInitialPage(initialPage: String): PageTask[Response] = PageTask { page =>
    page.goto("https://earthview.withgoogle.com/beegden-the-netherlands-2007")
  }

  def getBackgroundImage(): PageTask[String] = PageTask { page =>
    page.evaluate("document.querySelector('.background').style['background-image']")
  }

  def gotoNextPageAndWait(): PageTask[Unit] = {
    for {
      _ <- gotoNextPage
      _ <- waitUntilNavigationDone
    } yield ()
  }

  def gotoNextPage(): PageTask[Unit] = PageTask { page =>
    for {
      handle <- page.$("a.pagination__link--next")
      _ <- handle.click()
    } yield ()
  }

  def getMultipleBackground(numberOfPages: Int): PageTask[List[String]] = {
    val n = (0 to numberOfPages map { _ => gotoNextPageAndWait().flatMap { _ => getBackgroundImage.map { url => url}}})
    n.foldLeft(PageTask(_ => Future(List[String]()))) { (b, a) =>
      b.flatMap { bb => a.map { aa => bb:+aa }}
    }
  }
}
