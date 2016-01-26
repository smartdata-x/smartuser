package scheduler

/**
  * Created by yangshuai on 2016/1/26.
  */
object Scheduler {

  def main(args: Array[String]): Unit = {

    while (true) {


      Timer.waitToNextTask()
    }
  }

}
