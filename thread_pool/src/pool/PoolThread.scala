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
    var start = System.currentTimeMillis()
    _thread_pool.addThread(this)
    while (_hot_thread || (System.currentTimeMillis() - start) < _thread_pool.timeout) {
      try {
        _thread_pool.notifier.synchronized {
          _thread_pool.notifier.wait()
          val task = _thread_pool.takeTask()

          _thread_pool.mapTask(task.handle, this)
          task.run()
          _thread_pool.unmapTask(task.handle)
          start = System.currentTimeMillis()
        }
      }
      catch {
        case e : InterruptedException =>
      }
    }
    _thread_pool.delThread(this)
  }
}
