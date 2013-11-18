package pool

import java.util.concurrent.LinkedBlockingQueue
import scala.collection.parallel.mutable.ParHashMap
import tasks.Task
import scala.util.Random

/**
 * Created with IntelliJ IDEA.
 * User: mactep
 * Date: 18.11.13
 * Time: 9:21
 */
class ThreadPool(t : Int) {
  val _tasks_queue   = new LinkedBlockingQueue[Task]()
  val _task_notifer  = new Object()
  val _threads_state = ParHashMap.empty[PoolThread, Boolean]
  val _tasks_mapping = ParHashMap.empty[Int, PoolThread]
  val _timeout       = t

  def this(n : Int, t : Int) = {
    this(t)
    (0 to n).foreach(i => {
      val th = new PoolThread(true, this)
      th.start()
    })
  }

  def addTask(task : Runnable) : Int = {
    // Unstuck elements in queue if any
    if (!_tasks_queue.isEmpty) {
      _task_notifer.synchronized {
        _task_notifer.notify()
      }
    }

    val ihandle = Random.nextInt(65536)
    if (_threads_state.forall{case (_, v) => v}) {
      val th = new PoolThread(false, this)
      th.start()
    }
    _task_notifer.synchronized {
      _tasks_queue.put(new Task {
        def handle: Int = ihandle
        def run(): Unit = task.run()
      })
      _task_notifer.notify()
    }
    ihandle
  }

  def killTask(handle : Int) : Unit = {
    _tasks_mapping(handle).interrupt()
  }

  def timeout : Int = _timeout

  def size : Int = _threads_state.size

  def queue : LinkedBlockingQueue[Task] = _tasks_queue

  def killall() : Unit = _tasks_mapping.foreach{case (handle, _) => killTask(handle)}

  private[pool] def addThread(thread : PoolThread) : Unit = {
    _threads_state += (thread -> false)
  }

  private[pool] def delThread(thread : PoolThread) : Unit = {
    _threads_state.remove(thread)
  }

  private[pool] def mapTask(handle : Int, thread : PoolThread) : Unit = {
    _tasks_mapping += (handle -> thread)
    _threads_state(thread) = true
  }

  private[pool] def unmapTask(handle : Int) : Unit = {
    _threads_state(_tasks_mapping(handle)) = false
    _tasks_mapping.remove(handle)
  }

  private[pool] def notifier : Object = _task_notifer

  private[pool] def takeTask() : Task = _tasks_queue.take()
}
