package server

import java.net.InetSocketAddress
import akka.actor._
import akka.io.Tcp
import akka.util.ByteString
import scala.collection.mutable.ArrayBuffer

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 16.12.13
 * Time: 19:20
 */
object ServerConnectionHandler {
  def props(remote: InetSocketAddress, connection: ActorRef, subscribers : ArrayBuffer[ActorRef]): Props =
    Props(new ServerConnectionHandler(remote, connection, subscribers))
}

class ServerConnectionHandler(remote: InetSocketAddress, connection: ActorRef,
                              subscribers : ArrayBuffer[ActorRef]) extends Actor with ActorLogging {
  context.watch(connection)

  def receive: Receive = {
    case Tcp.Received(data) =>
      val text = data.utf8String.trim
      log.info("Received '{}' from remote address {}", text, remote)
      text match {
        case "close"     =>
          context.stop(self)
          subscribers -= sender
        case "subscribe" =>
          subscribers += sender
          sender ! Tcp.Write(ByteString("Subscribed!\n"))
        case _           =>
          subscribers.foreach(_ ! Tcp.Write(ByteString("Message from %s: %s\n".format(remote, text))))
      }
    case _: Tcp.ConnectionClosed =>
      log.info("Stopping, because connection for remote address {} closed", remote)
      context.stop(self)
      subscribers -= sender
    case Terminated(`connection`) =>
      log.info("Stopping, because connection for remote address {} died", remote)
      context.stop(self)
      subscribers -= sender
  }
}