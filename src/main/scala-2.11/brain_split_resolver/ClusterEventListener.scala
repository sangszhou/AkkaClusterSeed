package brain_split_resolver

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

/**
  * Created by xinszhou on 4/4/16.
  */

object ClusterEventListener {
  def props = Props(new ClusterEventListener)

  val actorName = "clusterEventListener"
}


class ClusterEventListener extends Actor with ActorLogging {
  log.info("cluster event listener actor created")

  val cluster = Cluster(context.system)

  override def receive: Receive = {
    case msg: UnreachableMember =>
      log.error("member unreachable " + msg)

    case msg: ReachableMember =>
      log.error("member reachable again " + msg)

    case msg: MemberUp =>
    case msg: MemberExited =>
      log.info("member is exited: " + msg.member)

    case msg: MemberRemoved =>
      log.info("member is removed " + msg.member)
  }

}
