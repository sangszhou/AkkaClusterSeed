package persistence

/**
  * Created by xinszhou on 4/4/16.
  */
case class Connection(from: String, to: Option[String], mode: String,
                      time: String, responseTime: Option[Long] = None,
                      toSeq: Option[Long], status: String)
