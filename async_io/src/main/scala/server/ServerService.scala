package server

import java.net.InetSocketAddress
import akka.actor.{ActorRef, Actor, ActorLogging, Props}
import akka.io.{Tcp, IO}
import scala.collection.mutable.ArrayBuffer

/**
 * Created with IntelliJ IDEA.
 * User: pavel
 * Date: 16.12.13
 * Time: 19:18
 */
object ServerService {
  def props(endpoint: InetSocketAddress, subscribers : ArrayBuffer[ActorRef]): Props =
    Props(new ServerService(endpoint, subscribers))
}

class ServerService(endpoint : InetSocketAddress, subscribers : ArrayBuffer[ActorRef]) extends Actor with ActorLogging {
  import context.system

  IO(Tcp) ! Tcp.Bind(self, endpoint)

  override def receive: Receive = {
    case Tcp.Connected(remote, _) =>
      log.info("Remote address {} connected", remote)
      sender ! Tcp.Register(context.actorOf(ServerConnectionHandler.props(remote, sender, subscribers)))
  }
}