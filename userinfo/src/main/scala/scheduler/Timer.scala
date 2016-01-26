package scheduler

import java.util.concurrent.TimeUnit
import java.util.{Calendar, Date}

import log.UILogger

import scala.util.control.Breaks._

/**
  * Created by yangshuai on 2016/1/26.
  */
object Timer {

  def waitToNextTask: Unit = {

    val calendar = Calendar.getInstance
    calendar.setTime(new Date)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)
    val remainSeconds = (60 - minute) * 60 + (60 - second)

    UILogger.warn("Next task will begin in " + remainSeconds + "seconds.")
    TimeUnit.SECONDS.sleep(remainSeconds)
  }
}
