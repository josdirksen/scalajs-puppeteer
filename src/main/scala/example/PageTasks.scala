package example

import cats.data.ReaderT

import scala.concurrent.ExecutionContext.Implicits.global
import example.puppeteer.PuppeteerFacade.{Page, Response, WaitForNavigationOptions}

import scala.concurrent.{Future, Promise => SPromise}
import scala.scalajs.js.Promise
import cats.implicits._
import io.scalajs.nodejs.fs.Fs
import io.scalajs.nodejs.http.{Http, ServerResponse}
import io.scalajs.nodejs.url.URL
import io.scalajs.nodejs.console
import io.scalajs.nodejs.https.Https
import io.scalajs.nodejs.path.Path

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

  def getMultipleBackgroundAndDownload(numberOfPages: Int, downloadTo: String = ""): PageTask[List[String]] = {
    val n = (0 to numberOfPages map { _ => gotoNextPageAndWait().flatMap { _ => getBackgroundImage.flatMap { url => download(url, downloadTo)}}})
    n.foldLeft(PageTask(_ => Future(List[String]()))) { (b, a) =>
      b.flatMap { bb => a.map { aa => aa.fold(bb)(aaa => bb:+aaa) }}
    }
  }

  def download(url: String, downloadTo: String):PageTask[Option[String]] = PageTask { _ =>
    val p = SPromise[Option[String]]()
    val direct = url.substring(5, url.length -2)

    def handler(fileName: String): ServerResponse => Unit = { response =>
      response.pipe(Fs.createWriteStream(downloadTo + Path.basename(fileName)))
      p.success(Option(fileName))
    }

    console.log(s"Trying to download $direct")
    URL.parse(direct).pathname.toOption match {
      case Some(fileName) if direct.startsWith("http:") =>  Http.get(direct, handler(fileName))
      case Some(fileName) if direct.startsWith("https:") => Https.get(direct, handler(fileName))
      case _ =>
        console.log(s"Can't parse $direct, ignoring")
        p.success(None) // we just igore these, so also complete the promise
    }

    p.future
  }
}
