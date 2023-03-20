package com.android.resource.vm.user.data

/**
 * created by jiangshide on 2020/5/5.
 * email:18311271399@163.com
 */
class Pay {

}

class Order {
  val id: Long = 0
  val uid: Long = 0
  var source: Int = 0
  val appid: String? = ""
  val mchid: String? = ""
  val packageValue:String?=""
  val nonceStr: String? = ""
  val sign: String? = ""
  val prepayId: String? = ""
  val date: Long = 0
  val tradeType: String? = ""
  val status: Int = 0
  val reason: String? = ""

  override fun toString(): String {
    return "Order(id=$id, uid=$uid, source=$source, appid=$appid, mchid=$mchid, packageValue=$packageValue, nonceStr=$nonceStr, sign=$sign, prepayId=$prepayId, date=$date, tradeType=$tradeType, status=$status, reason=$reason)"
  }

}