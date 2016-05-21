package cluster_connectivity

import akka.actor.{Actor, ActorLogging, Address, Cancellable, RootActorPath}
import akka.cluster.Cluster
import cluster_connectivity.ShardingProbe.{Ping, Pong, PongTimeout}
import util.{NetUtil, TimeUtil}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by xinszhou on 4/4/16.
  */

object RemoteMsgProducer {
  case object DoRemote

  val actorName = "RemoteMsgProducer"
  val actorPath = s"/user/${actorName}"
  val modeRemote = "remote"
}

class RemoteMsgProducer extends Actor with ActorLogging {
  import RemoteMsgProducer._
  val hostport = NetUtil.actorSystemAddress(context.system).hostPort
  var fromSeq: Long = 0
  var toSeq: Long = 0

  val routineProbe = context.system.scheduler.schedule(30 second, 5 second, self, DoRemote)
  var waitingWindow: Map[Long, (Option[Cancellable], Long)] = Map.empty[Long, (Option[Cancellable], Long)]

  val cluster = Cluster(context.system)
  val seedsAddress: List[Address] = cluster.settings.SeedNodes.toList

  override def receive: Receive = {
    case DoRemote =>
      val toAddr = seedsAddress(fromSeq % seedsAddress.size toInt)

      context.actorSelection(RootActorPath(toAddr) / actorPath.split("/")) ! Ping(fromSeq)

      val currentTime = TimeUtil.gmtTime.toDateTime.getMillis
      waitingWindow += fromSeq ->( Some(context.system.scheduler.scheduleOnce(1 minutes, self,
        PongTimeout(fromSeq))), currentTime)

      fromSeq += 1

    case Ping(fromSeqNum) =>
      sender() ! Pong(fromSeqNum, toSeq, hostport)
      toSeq += 1


    case Pong(fromSeqNum, toSeqNum, toHostname) =>
      waitingWindow.get(fromSeqNum) match {
        case None =>
        case Some(_) =>
          val currentTime = TimeUtil.gmtTime.toDateTime.getMillis
          val responseTime = currentTime - waitingWindow(fromSeqNum)._2

          waitingWindow(fromSeqNum)._1.get.cancel()
          waitingWindow -= fromSeqNum

          log.info("successfully receive pong message")

      }

    case PongTimeout(fromSeqNum) =>
      waitingWindow = waitingWindow - fromSeqNum
      log.error("failed to receive pong message")

  }



}
