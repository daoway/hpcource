import java.util.concurrent.LinkedBlockingQueue
import pool.ThreadPool
import scala.util.Random
import test.TaskFactory

/**
 * Created with IntelliJ IDEA.
 * User: mactep
 * Date: 18.11.13
 * Time: 11:35
 */
object Main {
  val results = new LinkedBlockingQueue[(String, Any)]()
  val factory = new TaskFactory(results)

  class SystemExitCalled extends Exception {

  }

  def process(s : String, tp : ThreadPool) = {
    if (s.isEmpty) {

    }
    else if (s(0) == 'a') {
      val i = s.drop(2).toInt
      val name = Random.nextInt(100000)

      val handle = tp.addTask(factory.createTaskFor(name.toString, i, Random.nextInt(42)))

      printf("Task %s added to pool as %d\n", name, handle)
    }
    else if (s(0) == 'k') {
      val i = s.drop(2).toInt
      tp.killTask(i)
      printf("Task %d killed]n", i)
    }
    else {
      print("Results: ")
      while (!results.isEmpty) {
        printf("%s ", results.take())
      }
      println()
    }
  }

  def main(args : Array[String]) = {
    val tp = new ThreadPool(2, 10000)

    System.setSecurityManager(new SecurityManager() {
      override def checkExit(status : Int) : Unit = {
        throw new SystemExitCalled()
      }
    })

    while (true) {
      try {
        printf("Number of threads: %d\n", tp.size)
        printf("Queue: %s\n", tp.queue)
        val s = readLine()
        process(s, tp)
      }
      catch {
        case e : SystemExitCalled => tp.killall()
      }
    }
  }
}