package other

import java.util.regex.Pattern

import scala.collection.mutable

/**
  * Created by yangshuai on 2016/1/20.
  */
object RegExpTest extends App {

  val source = "休市安排为：（一）元旦：1月1日（星期五）至1月3日（星期日）休市，1月4日（星期一）起照常开市；（二）春节：2月7日（星期日）至2月13日（星期六）休市，2月15日（星期一）起照常开市。另外，2月6日（星期六）、2月14日（星期日）为周末休市；（三）清明节：4月2日（星期六）至4月4日（星期一）休市，4月5日（星期二）起照常开市；（四）劳动节：4月30日（星期六）至5月2日（星期一）休市，5月3日（星期二）起照常开市；（五）端午节：6月9日（星期四）至6月11日（星期六）休市，6月13日（星期一）起照常开市。另外，6月12日（星期日）为周末休市；（六）中秋节：9月15日（星期四）至9月17日（星期六）休市，9月19日（星期一）起照常开市。另外，9月18日（星期日）为周末休市；（七）国庆节：10月1日（星期六）至10月7日（星期五）休市，10月10日（星期一）起照常开市。另外，10月8日（星期六）、10月9日（星期日）为周末休市。"


  val pattern = "\\d+月\\d+日".r
  val iterator = pattern.findAllMatchIn(source)

  while(iterator.hasNext) {
    val item = iterator.next.toString()
    val arr = item.split("月")
    var month = arr(0)
    var day = arr(1)
    day = day.substring(0, day.length - 1)
    if (month.length == 1)
      month = "0" + month

    if (day.length == 1)
      day = "0" + day

    print("\"2016-" + month + "-" + day + "\", ")
  }
}
