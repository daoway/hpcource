package pool

/**
 * Created with IntelliJ IDEA.
 * User: mactep
 * Date: 18.11.13
 * Time: 9:26
 */
class PoolThread(hot_thread : Boolean, tpool : ThreadPool) extends Thread {
  val _hot_thread  = hot_thread
  val _thread_pool = tpool

  override def run() : Unit = {
    var run_flag = true
    _thread_pool.addThread(this)
    while (run_flag) {
      try {
        _thread_pool.notifier.synchronized {
          _thread_pool.notifier.wait(_thread_pool.timeout)
        }

        val qempty = _thread_pool.queue.isEmpty
        if (!_hot_thread && qempty) {
          run_flag = false
        }
        else if (!qempty) {
          val task = _thread_pool.takeTask()

          _thread_pool.mapTask(task.handle, this)
          task.run()
          _thread_pool.unmapTask(task.handle)
        }
      }
      catch {
        case e : InterruptedException =>
      }
    }
    _thread_pool.delThread(this)
  }
}
