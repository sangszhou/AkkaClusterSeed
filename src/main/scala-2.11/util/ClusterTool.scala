package util

import akka.actor.{ActorRef, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import cluster_connectivity.{RemoteMsgProducer, ShardingMsgProducer, ShardingProbe}
import com.typesafe.config.ConfigFactory
import main.Boot
import org.slf4j.LoggerFactory

/**
  * Created by xinszhou on 4/4/16.
  */
object ClusterTool {

  val log = LoggerFactory.getLogger(getClass)

  val globalConf = ConfigFactory.load

  val clusterToolConf = globalConf.getConfig("clusterTool")

  var shardingProbeLocalRegin: Option[ActorRef] = None

  def init() = {

    val enableClusterTool = clusterToolConf.getBoolean("enable")

    if (enableClusterTool) {
      log.info("enable cluster tool")

      val shardingConf = clusterToolConf.getConfig("shardingConf")


      //start akka cluster sharding
      lazy val enableShardingMonitor = shardingConf.getBoolean("enable")
      if (enableShardingMonitor) {

        log.info("enabled sharding monitor")

        ClusterSharding(Boot.system).start(
          typeName = ShardingProbe.shardName,
          entityProps = ShardingProbe.props,
          settings = ClusterShardingSettings(Boot.system),
          extractEntityId = ShardingProbe.idExtractor,
          extractShardId = ShardingProbe.shardResolver
        )

        shardingProbeLocalRegin = Some(ClusterSharding(Boot.system).shardRegion(ShardingProbe.shardName))

        //starting sharding msg producer
        Boot.system.actorOf(ShardingMsgProducer.props, "shardingMessageProducer")
      }


      //start remote actor calling
      lazy val remoteConf = clusterToolConf.getConfig("remoteConf")
      lazy val enableRemoteMonitor = remoteConf.getBoolean("enable")

      if (enableRemoteMonitor) {
        Boot.system.actorOf(Props(classOf[RemoteMsgProducer]), RemoteMsgProducer.actorName)
      }

    }


  }


}
