import server.Server

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 16.12.13
 * Time: 18:09
 */
object Main {
  case class Config(host : String = "127.0.0.1", port : Int = 33030)

  def getParser : scopt.OptionParser[Config] = new scopt.OptionParser[Config]("ig-regions") {
    head("async_io", "1.0-SNAPSHOT")
    note("Use telnet to connect to the server.")
    note("Send 'subscribe' message to the server to receive messages from other clients")
    opt[String]("host") action {(s, c) => c.copy(host = s)} text "set host address (default: 127.0.0.1)"
    opt[Int]("port") action {(s, c) => c.copy(port = s)} text "set port (default: 33030)"
    note("Help:")
    help("help") text "this message"
  }

  def main(args : Array[String]) : Unit = {
    val parser = getParser

    parser.parse(args, Config()) map {config => {
      new Server(config.host, config.port)
    }} getOrElse {
      parser.showUsage
    }
  }
}
