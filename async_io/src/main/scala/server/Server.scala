package server

import akka.actor.{ActorRef, ActorSystem}
import java.net.InetSocketAddress
import scala.collection.mutable.ArrayBuffer

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 16.12.13
 * Time: 18:45
 */
class Server(val host : String, val port : Int) {
  val system = ActorSystem("async-io-server")
  val endpoint = new InetSocketAddress(host, port)

  val subscribers = ArrayBuffer.empty[ActorRef]

  system.actorOf(ServerService.props(endpoint, subscribers), "server-service")
  readLine("Hit any key to exit")
  system.shutdown()
  system.awaitTermination()
}
