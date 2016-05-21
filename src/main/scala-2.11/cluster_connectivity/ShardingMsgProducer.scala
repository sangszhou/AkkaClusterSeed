package cluster_connectivity

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, Cancellable, Props}
import cluster_connectivity.ShardingMsgProducer.DoSharding
import cluster_connectivity.ShardingProbe.{Ping, Pong, PongTimeout}
import util.{ClusterTool, NetUtil, TimeUtil}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
/**
  * Created by xinszhou on 4/4/16.
  */
object ShardingMsgProducer {
  def props: Props = Props(classOf[ShardingMsgProducer])

  case object DoSharding

  val actorName = "PingBot"
  val shardName = "Shard"
  val modeSharding = "sharding"

}

class ShardingMsgProducer extends Actor with ActorLogging {

  val hostport = NetUtil.actorSystemAddress(context.system).hostPort
  var fromSeq: Long = 0
  val routineProbe = context.system.scheduler.schedule(15 second, 15 second, self, DoSharding)

  var waitingWindow = Map.empty[Long, (Option[Cancellable], Long)]

  log.info("sharding msg producer has been created")

  override def receive: Receive = {
    case DoSharding =>
      log.info("do sharding message received on producer, sending message to sharding probe")

      val currentTime = TimeUtil.gmtTime.toDateTime.getMillis

      ClusterTool.shardingProbeLocalRegin.foreach(_ ! Ping(fromSeq))

      waitingWindow += fromSeq -> (Some(context.system.scheduler.scheduleOnce(1 minutes,
        self, PongTimeout(fromSeq))), currentTime)

      fromSeq += 1

    case Pong(fromSeqNum, toSeq, toHostname) =>
      log.info("pong msg received in producer")
      waitingWindow.get(fromSeqNum) match {
        case None =>
        case Some(_) =>
          val currentTime = TimeUtil.gmtTime.toDateTime.getMillis
          val responseTime = currentTime - waitingWindow(fromSeqNum)._2

          waitingWindow(fromSeqNum)._1.get.cancel()
          waitingWindow -= fromSeqNum

      }

    case PongTimeout(num) =>
      waitingWindow -= num
      log.error(s"failed to receive message from shardingProbe, timeout msg seq is $num" )
  }
}
