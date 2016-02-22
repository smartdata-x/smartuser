package config

/**
  * Created by C.J.YOU on 2016/1/18.
  */
object FileConfig {

  val ROOT_DIR = "/home/smartuser"

  //股票信息
  val STOCK_INFO = ROOT_DIR + "/Stock"
  //股票回报率
  val RATE_OF_RETURN_STOCK = ROOT_DIR + "/Return_Stock"
  //用户所关注的股票
  val USER_INFO = ROOT_DIR + "/User"
  //用户回报率排名
  val RANK_USER = ROOT_DIR + "/Rank_User"
  // 用户回报率检验
  val USER_CHOICE_CHECKER = ROOT_DIR + "/Choice_Checker"
  // 用户回报率检验百分比
  val USER_CHOICE_CHECKER_PERCENT = ROOT_DIR + "/Checker_Percent"
}

