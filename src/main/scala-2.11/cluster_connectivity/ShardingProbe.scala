package cluster_connectivity

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.sharding.ShardRegion
import cluster_connectivity.ShardingProbe.{Ping, Pong}
import util.NetUtil

/**
  * Created by xinszhou on 4/4/16.
  */
object ShardingProbe {

  case object Do
  case class Ping(fromSeq: Long)
  case class Pong(fromSeq: Long, toSeq: Long, hostport: String)
  case class PongTimeout(fromSeq: Long)

  val actorName = "shardingActorName"
  val shardName = "shardingShardName"

  def props: Props = Props(classOf[ShardingProbe])

  def idExtractor: ShardRegion.ExtractEntityId = {
    case msg @Ping(seq) => ((seq % 30).toString, msg)
  }

  def shardResolver: ShardRegion.ExtractShardId =  {
    case msg @ Ping(seq) => (seq % 30).toString
  }
}

class ShardingProbe extends Actor with ActorLogging {

  log.info("sharding probe created, ready to receive msg")
  var toSeq = 0

  val hostport = NetUtil.actorSystemAddress(context.system).hostPort

  override def receive: Receive = {
    case Ping(fromSeq) =>
      log.info("ping msg received from sharding probe")

      val receiver = sender()
      receiver ! Pong(fromSeq, toSeq, hostport)
      toSeq += 1
  }

}
