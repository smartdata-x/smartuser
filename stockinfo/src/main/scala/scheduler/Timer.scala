package scheduler

import java.util.concurrent.TimeUnit
import java.util.{Calendar, Date}

import log.SILogger

import scala.util.control.Breaks._

/**
  * Created by yangshuai on 2016/1/26.
  */
object Timer {

  val taskHour = Array[Int](9, 11, 13, 15)
  val timeMap = Map[Int, Int](9 -> 30, 11 -> 30, 13 -> 0, 15 -> 0)

  def waitToNextTask(): Unit = {

    val calendar = Calendar.getInstance
    calendar.setTime(new Date)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)
    var remainSeconds = 0
    var currentHour = 0

    var index = 0

    if (hour >= taskHour.last) {
      currentHour = taskHour(0)
      remainSeconds = ((24 - hour + currentHour) * 60 + timeMap.get(currentHour).get - minute) * 60 - second
    } else {
      breakable {
        for (i <- taskHour.indices) {
          currentHour = taskHour(i)
          if (hour < taskHour(i) || (hour == currentHour && minute < timeMap.get(currentHour).get)) {
            remainSeconds = ((currentHour - hour) * 60 + timeMap.get(currentHour).get - minute) * 60 - second
            index = i
            break
          }
        }
      }
    }

    currentHour = taskHour(index)
    SILogger.warn("Wait to " + currentHour + ":" + timeMap.get(currentHour).get)
    SILogger.warn("Seconds: " + remainSeconds)
    TimeUnit.SECONDS.sleep(remainSeconds)
  }
}
