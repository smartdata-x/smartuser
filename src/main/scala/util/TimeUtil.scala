package util

import java.math.BigInteger
import java.text.SimpleDateFormat

/**
  * Created by C.J.YOU on 2016/1/13.
  */
 object TimeUtil {

  def GetTime(timeStamp: String): String = {
    val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
    val bigInt: BigInteger = new BigInteger(timeStamp)
    val date: String = sdf.format(bigInt)
    date
  }

  def GetDay(timeStamp: String): String = {
    val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    val bigInt: BigInteger = new BigInteger(timeStamp)
    val date: String = sdf.format(bigInt)
    date
  }

  def GetDayAndHour(timeStamp: String): String = {
    val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH")
    val bigInt: BigInteger = new BigInteger(timeStamp)
    val date: String = sdf.format(bigInt)
    date
  }
}
