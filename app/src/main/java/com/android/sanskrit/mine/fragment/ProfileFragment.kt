package com.android.sanskrit.mine.fragment

import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.android.files.view.transferee.loader.GlideImageLoader
import com.android.files.view.transferee.transfer.TransferConfig
import com.android.img.Img
import com.android.resource.MyFragment
import com.android.resource.vm.user.data.User
import com.android.sanskrit.R
import com.android.utils.AppUtil
import com.android.widget.ZdButton
import kotlinx.android.synthetic.main.mine_profile_fragment.userProfileAge
import kotlinx.android.synthetic.main.mine_profile_fragment.userProfileAgeL
import kotlinx.android.synthetic.main.mine_profile_fragment.userProfileBirthday
import kotlinx.android.synthetic.main.mine_profile_fragment.userProfileCity
import kotlinx.android.synthetic.main.mine_profile_fragment.userProfileConstellation
import kotlinx.android.synthetic.main.mine_profile_fragment.userProfileConstellationL
import kotlinx.android.synthetic.main.mine_profile_fragment.userProfileHobby
import kotlinx.android.synthetic.main.mine_profile_fragment.userProfileIcon
import kotlinx.android.synthetic.main.mine_profile_fragment.userProfileId
import kotlinx.android.synthetic.main.mine_profile_fragment.userProfileInfo
import kotlinx.android.synthetic.main.mine_profile_fragment.userProfileNick
import kotlinx.android.synthetic.main.mine_profile_fragment.userProfilePhone
import kotlinx.android.synthetic.main.mine_profile_fragment.userProfileSex
import kotlinx.android.synthetic.main.mine_profile_fragment.userProfileV
import kotlinx.android.synthetic.main.mine_profile_fragment.userProfileZodiac
import kotlinx.android.synthetic.main.mine_profile_fragment.userProfileZodiacL

/**
 * created by jiangshide on 2019-08-17.
 * email:18311271399@163.com
 */

class ProfileFragment : MyFragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.mine_profile_fragment)
  }

  @RequiresApi(VERSION_CODES.JELLY_BEAN)
  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    val user: User = arguments?.getParcelable("data")!!

    Img.loadImageCircle(user.icon, userProfileIcon, R.mipmap.default_user)
    userProfileIcon.setOnClickListener {
      transferee?.apply(
          TransferConfig.build()
              .setImageLoader(GlideImageLoader.with(AppUtil.getApplicationContext()))
              .setSourceImage(user.icon)
              .create()
      )
          ?.show()
    }
    if (user.certification >0) {
      userProfileV.visibility = View.VISIBLE
    } else {
      userProfileV.visibility = View.GONE
    }

    userProfileId.text = "记号:${user.id}"
    userProfileInfo.setTxt(user.intro)
    userProfileNick.text = user.nick
    userProfileSex.text = if (user.sex == 1) "男" else "女"
    if (!TextUtils.isEmpty(user.birthday)) {
      userProfileBirthday.text = user.birthday
      userProfileZodiac.text = user.zodiac
      userProfileZodiacL.visibility = View.VISIBLE
      userProfileConstellation.text = user.constellation
      userProfileConstellationL.visibility = View.VISIBLE
      userProfileAgeL.visibility = View.VISIBLE
      userProfileAge.text = "${user.age}后"
    } else {
      userProfileZodiacL.visibility = View.GONE
      userProfileConstellationL.visibility = View.GONE
      userProfileAgeL.visibility = View.GONE
    }
    userProfileCity.text = user.city
    userProfilePhone.text = user.phone
    if (!TextUtils.isEmpty(user.hobby)) {
      showView(user.hobby!!.split(","))
    }
  }

  private fun showView(data: List<String>) {
    val views = ArrayList<ZdButton>()
    userProfileHobby.removeAllViews()
    data.forEachIndexed { index, type ->
      val view = getView(R.layout.report_fragment_item)
      val zdButton = view.findViewById<ZdButton>(R.id.reportItemName)
      zdButton.text = type
      zdButton.setRandomColor()
      zdButton.setTextColor(getColor(R.color.white))
      view.id = index
      userProfileHobby.addView(view)
      views.add(zdButton)
    }
  }
}