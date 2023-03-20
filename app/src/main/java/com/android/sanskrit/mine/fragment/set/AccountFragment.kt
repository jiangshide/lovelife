package com.android.sanskrit.mine.fragment.set

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.resource.MyFragment
import com.android.resource.vm.user.data.Account
import com.android.sanskrit.R
import com.android.widget.adapter.create
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm.HS256
import io.jsonwebtoken.security.Keys
import kotlinx.android.synthetic.main.mine_set_account_fragment.accountRecycleView
import kotlinx.android.synthetic.main.mine_set_account_fragment.recharge
import kotlinx.android.synthetic.main.mine_set_account_fragment_item.view.moneyItemIcon
import kotlinx.android.synthetic.main.mine_set_account_fragment_item.view.moneyItemName

/**
 * created by jiangshide on 2020/5/2.
 * email:18311271399@163.com
 */
class AccountFragment : MyFragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.mine_set_account_fragment)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    recharge.setOnClickListener {
      push(RechargeFragment().setTitle("充值中心"))
    }
    showView()
//    val jwt = Jwts.builder().setSubject("jsd")
//        .signWith(Keys.secretKeyFor(HS256))
//        .compact()
//    Jwts.builder().addClaims()
  }

  private fun showView() {
    val account = Account()
    accountRecycleView.create(account.getArr(mActivity), R.layout.mine_set_account_fragment_item, {
      moneyItemIcon.setImageResource(it.resId)
      moneyItemName.text = it.name
    }, {})
  }
}