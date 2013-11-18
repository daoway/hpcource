package tasks

/**
 * Created with IntelliJ IDEA.
 * User: mactep
 * Date: 18.11.13
 * Time: 9:33
 */
trait Task {
  def handle : Int
  def run() : Unit
}
