package config

/**
  * Created by yangshuai on 2016/1/26.
  */
object HbaseConfig {

  private var _dir = ""

  private var _quorum = ""

  private var _port = ""

  def init(dir: String, quorum: String, port: String): Unit = {
    _dir = dir
    _quorum = quorum
    _port = port
  }

  def dir: String = _dir

  def quorum: String = _quorum

  def port: String = _port

}
