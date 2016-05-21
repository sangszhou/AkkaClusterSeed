package main

import akka.actor.ActorSystem
import org.slf4j.LoggerFactory
import util.ClusterTool

/**
  * Created by xinszhou on 4/4/16.
  */
object Boot {

  val log = LoggerFactory.getLogger(getClass)

  val system = ActorSystem("ClusterSystem")

  def main(args: Array[String]) = {

    ClusterTool.init()

    log.info("application started")
  }

}
