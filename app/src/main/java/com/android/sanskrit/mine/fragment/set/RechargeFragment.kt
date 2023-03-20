package com.android.sanskrit.mine.fragment.set

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.android.resource.MyFragment
import com.android.resource.vm.user.data.Account
import com.android.resource.vm.user.data.RechargeQuota
import com.android.resource.vm.user.data.RechargeType
import com.android.sanskrit.R
import com.android.sanskrit.wxapi.WXApiManager
import com.android.utils.LogUtil
import com.android.widget.ZdToast
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import com.android.widget.extension.setDrawableLeft
import kotlinx.android.synthetic.main.mine_set_account_recharge_fragment.rechargeQuotaRecycleView
import kotlinx.android.synthetic.main.mine_set_account_recharge_fragment.rechargeSubmit
import kotlinx.android.synthetic.main.mine_set_account_recharge_fragment.rechargeTypeRecycleView
import kotlinx.android.synthetic.main.mine_set_account_recharge_quota_fragment_item.view.quotaItemCoin
import kotlinx.android.synthetic.main.mine_set_account_recharge_quota_fragment_item.view.quotaItemIncrease
import kotlinx.android.synthetic.main.mine_set_account_recharge_quota_fragment_item.view.quotaItemMoney
import kotlinx.android.synthetic.main.mine_set_account_recharge_quota_fragment_item.view.quotaItemR
import kotlinx.android.synthetic.main.mine_set_account_recharge_type_fragment_item.view.typeItemCheck
import kotlinx.android.synthetic.main.mine_set_account_recharge_type_fragment_item.view.typeItemName

/**
 * created by jiangshide on 2020/5/2.
 * email:18311271399@163.com
 */
class RechargeFragment : MyFragment() {

  private var quotaAdapter: KAdapter<RechargeQuota>? = null
  private var typeAdapter: KAdapter<Account>? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.mine_set_account_recharge_fragment)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    showQuota()
    showType()


    userVM!!.pay.observe(this, Observer {
      hiddle()
      LogUtil.e("---err:",it.error," | data:",it.data)
      if (it.error != null) {
        ZdToast.txt(it.error.message)
        return@Observer
      }
      WXApiManager.pay(it.data)
    })

    rechargeSubmit.setOnClickListener {
      userVM?.pay()
      showLoading()
    }
  }

  private fun showQuota() {
    val quota = RechargeQuota()
    val layoutManager = GridLayoutManager(mActivity, 2)
    quotaAdapter = rechargeQuotaRecycleView.create(
        quota.getArr(mActivity), R.layout.mine_set_account_recharge_quota_fragment_item, {
      quotaItemCoin.text = "${it.coin}梵币"
      quotaItemMoney.text = "${it.money}元"
      quotaItemIncrease.text = "赠送${it.increase}代缴券"
      LogUtil.e("selected:", it.selected)
      if (it.selected) {
        quotaItemR.setBackgroundResource(R.drawable.bg_radius_blue)
        quotaItemCoin.setTextColor(getColor(R.color.white))
      } else {
        quotaItemR.setBackgroundResource(R.drawable.bg_radius_white)
        quotaItemCoin.setTextColor(getColor(R.color.black))
      }
    }, {
      LogUtil.e("this:", this)
      quotaAdapter!!.datas()
          .forEachIndexed { index, rechargeQuota ->
            quotaAdapter!!.datas()[index].selected = coin == rechargeQuota.coin
          }
      quotaAdapter?.notifyDataSetChanged()
    }, layoutManager
    )
  }

  private fun showType() {
    val type = RechargeType()
    typeAdapter = rechargeTypeRecycleView.create(
        type.getArr(mActivity), R.layout.mine_set_account_recharge_type_fragment_item, {
      LogUtil.e("selected:", it.selected)
      typeItemCheck.isChecked = it.selected
      typeItemName.text = it.name
      typeItemName.setDrawableLeft(it.resId)
    }, {
      LogUtil.e("this:", this)
      typeAdapter!!.datas()
          .forEachIndexed { index, account ->
            typeAdapter!!.datas()[index].selected = account.name == name
          }
      typeAdapter?.notifyDataSetChanged()
    })
  }
}