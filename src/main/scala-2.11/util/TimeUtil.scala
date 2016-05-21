package util

import java.util.Locale

import org.joda.time.{DateTimeZone, LocalDate, LocalDateTime}
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

/**
  * Created by xinszhou on 4/4/16.
  */
object TimeUtil {
  val fmt: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withLocale(new Locale("en_US"))

  def gmtTime = LocalDateTime.now(DateTimeZone.UTC)
  def gmtDate = LocalDate.now(DateTimeZone.UTC)
  def utcTime = LocalDateTime.now(DateTimeZone.UTC)

}
