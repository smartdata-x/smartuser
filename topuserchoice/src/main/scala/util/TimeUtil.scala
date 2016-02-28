package util

import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

/**
  * Created by C.J.YOU on 2016/1/13.
  * 格式化时间的工具类
  */
 object TimeUtil {

  val holiday = List[String](
    "2016-01-01", "2016-01-02", "2016-01-03",
    "2016-02-07", "2016-02-08", "2016-02-09", "2016-02-10", "2016-02-11", "2016-02-12", "2016-02-13",
    "2016-04-04",
    "2016-05-02",
    "2016-06-09", "2016-06-10",
    "2016-09-15", "2016-09-16",
    "2016-10-03", "2016-10-04", "2016-10-05", "2016-10-06", "2016-10-07")

  /**
    * 今天是否工作日
    * @author yangshuai
    */
  def ifWorkDayToday: Boolean = {

    val calendar = Calendar.getInstance
    calendar.setTime(new Date)

    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY && !holiday.contains(getDay)) {
      true
    } else {
      false
    }
  }

  def getTime(timeStamp: String): String = {
    val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
    val bigInt: BigInteger = new BigInteger(timeStamp)
    val date: String = sdf.format(bigInt)
    date
  }

  def getDay: String = {
    val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    val date: String = sdf.format(new Date)
    date
  }

  def getCurrentHour: Int = {
    val calendar = Calendar.getInstance
    calendar.setTime(new Date)
    calendar.get(Calendar.HOUR_OF_DAY)
  }

  /**
    * offset必须为负数
    * @author yangshuai
    */
  def getPreWorkDay(offset: Int): String = {

    if (offset > 0) {
      throw new IllegalArgumentException("offset must be negative")
    }

    val kv = getPreWorkDay(offset, new Date)
    val date = kv._2
    val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    sdf.format(date)
  }

  /**
    * recursive
    * @author yangshuai
    */
 def getPreWorkDay(offset: Int, date: Date): (Int, Date) = {

   val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")

   if (offset == 0) {
     (offset, date)
   } else {
     val calendar = Calendar.getInstance
     calendar.setTime(date)
     calendar.add(Calendar.DATE, -1)
     val preDate = calendar.getTime
     if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && !holiday.contains(sdf.format(date))) {
       getPreWorkDay(offset + 1, preDate)
     } else {
       getPreWorkDay(offset, preDate)
     }
   }
 }

}
