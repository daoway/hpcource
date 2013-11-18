package test

import java.util.concurrent.LinkedBlockingQueue
import scala.util.Random

/**
 * Created with IntelliJ IDEA.
 * User: mactep
 * Date: 18.11.13
 * Time: 10:10
 */
class TaskFactory(results : LinkedBlockingQueue[(String, Any)]) {
  def createTaskFor(name : String, millisecs : Int, result : Any) : Runnable = {
    def foo() = {
      val start = System.currentTimeMillis()
      while (System.currentTimeMillis() - start < millisecs) {
        Random.nextDouble()
        Thread.sleep(50)
      }
      results.put((name, result))
    }

    new Runnable {
      def run() = foo()
    }
  }
}
