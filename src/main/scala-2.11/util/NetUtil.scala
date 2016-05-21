package util

import java.net.{Inet4Address, InetAddress, NetworkInterface}
import java.util

import akka.actor.{ActorSystem, Address, ExtendedActorSystem}

import scala.util.Try

/**
  * Created by xinszhou on 4/4/16.
  */
object NetUtil {

  def getIpaddress: Try[String] = {

    var localAddress: String = ""

    val interfaces: util.Enumeration[NetworkInterface] = NetworkInterface.getNetworkInterfaces

    Try {

      while(interfaces.hasMoreElements) {
        val ele = interfaces.nextElement

        val addrs: util.Enumeration[InetAddress] = ele.getInetAddresses

        while(addrs.hasMoreElements) {
          val addr: InetAddress = addrs.nextElement()

          addr match {
            case some: Inet4Address if !addr.isLoopbackAddress =>
              //              println(some.getHostAddress)
              localAddress = some.getHostAddress
            case _ => // do nothing
          }
        }
      }
      localAddress
    }

  }

  def getIphost: Try[(String, String)] = {

    var localAddress: String = ""
    var localHostname: String = ""

    val interfaces: util.Enumeration[NetworkInterface] = NetworkInterface.getNetworkInterfaces

    Try {

      while (interfaces.hasMoreElements) {
        val ele = interfaces.nextElement

        val addrs: util.Enumeration[InetAddress] = ele.getInetAddresses

        while (addrs.hasMoreElements) {
          val addr: InetAddress = addrs.nextElement()

          addr match {
            case some: Inet4Address if !addr.isLoopbackAddress =>
              localAddress = some.getHostAddress
              localHostname = some.getHostName

            case _ => // do nothing
          }
        }
      }

      (localAddress, localHostname)
    }

  }


  def actorSystemAddress(system: ActorSystem): Address = {
    system.asInstanceOf[ExtendedActorSystem].provider.getDefaultAddress
  }



  lazy val localIpAddress = getIpaddress.get
  lazy val (ip, host) = getIphost.get

}
