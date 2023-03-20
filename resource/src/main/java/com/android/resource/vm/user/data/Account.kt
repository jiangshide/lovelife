package com.android.resource.vm.user.data

import android.content.Context
import com.android.resource.R

/**
 * created by jiangshide on 2020/5/2.
 * email:18311271399@163.com
 */
class Account {
  var id: Long = 0
  var name: String = ""

  var resId: Int = 0
  var selected: Boolean = false

  fun getArr(context: Context): MutableList<Account> {
    val arr = ArrayList<Account>()
    val res = listOf(R.mipmap.recharge, R.mipmap.consume, R.mipmap.growl, R.mipmap.integral)
    context.resources.getStringArray(R.array.money)
        .forEachIndexed { index, s ->
          val account = Account()
          account.resId = res[index]
          account.name = s
          arr.add(account)
        }
    return arr
  }

}

class RechargeQuota {
  var id: Int = 0
  var name: String = ""
  var coin: Int = 0
  var money: Int = 0
  var increase: Int = 0
  var selected:Boolean=false

  fun getArr(context: Context): MutableList<RechargeQuota> {
    val arr = ArrayList<RechargeQuota>()
    val coins = listOf(50, 100, 300, 500, 1000, 2000, 5000)
    listOf(5, 10, 30, 50, 100, 200, 500).forEachIndexed { index, s ->
      val rechargeQuota = RechargeQuota()
      rechargeQuota.selected = index == 0
      rechargeQuota.coin = coins[index]
      rechargeQuota.money = s
      arr.add(rechargeQuota)
    }
    return arr
  }
}

class RechargeType {
  fun getArr(context: Context): MutableList<Account> {
    val arr = ArrayList<Account>()
    val res = listOf(R.mipmap.weixin_pay, R.mipmap.alipay, R.mipmap.qq_wallet, R.mipmap.ant_pay)
    context.resources.getStringArray(R.array.pay)
        .forEachIndexed { index, s ->
          val account = Account()
          account.selected = index == 0
          account.resId = res[index]
          account.name = s
          arr.add(account)
        }
    return arr
  }
}

