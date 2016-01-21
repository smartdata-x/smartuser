package scheduler

import java.util.concurrent.TimeUnit
import java.util.{Date, Calendar}
import log.SULogger

import scala.util.control.Breaks._
/**
  * Created by yangshuai on 2016/1/21.
  */
object Timer {

  val taskHour = Array[Int](9, 11, 13, 15)
  val taskMinute = Array[Int](30, 30, 0, 0)

  def waitToNextTask(): Unit = {

    val calendar = Calendar.getInstance
    calendar.setTime(new Date)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)
    var remainSeconds = 0

    var index = 0

    if (hour >= taskHour.last) {
      remainSeconds = ((24 - hour + taskHour(0)) * 60 + taskMinute(0) - minute) * 60 - second
    } else {
      breakable {
        for (i <- taskHour.indices) {
          if (hour < taskHour(i) || (hour == taskHour(i) && minute <= taskMinute(i))) {
            remainSeconds = ((taskHour(i) - hour) * 60 + taskMinute(i) - minute) * 60 - second
            index = i
            break
          }
        }
      }
    }

    SULogger.warn("Wait to " + taskHour(index) + ":" + taskMinute(index))
    SULogger.warn("Seconds: " + remainSeconds)
    println("Wait to " + taskHour(index) + ":" + taskMinute(index))
    println("Seconds: " + remainSeconds)
    TimeUnit.SECONDS.sleep(remainSeconds)
  }
}
