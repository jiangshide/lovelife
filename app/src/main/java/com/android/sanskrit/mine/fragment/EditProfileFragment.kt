package com.android.sanskrit.mine.fragment

import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import com.android.base.BaseBean
import com.android.event.ZdEvent
import com.android.files.Files
import com.android.files.Files.OnFileListener
import com.android.img.Img
import com.android.resource.MyFragment
import com.android.resource.REFRESH_MINE
import com.android.resource.REFRESH_PROFILE
import com.android.resource.Resource
import com.android.http.oss.OssClient
import com.android.http.oss.OssClient.HttpFileListener
import com.android.resource.view.CityDialog
import com.android.resource.vm.user.data.User
import com.android.sanskrit.R
import com.android.sanskrit.mine.fragment.profile.EditHobbyFragment
import com.android.sanskrit.mine.fragment.profile.EditIntroFragment
import com.android.sanskrit.mine.fragment.profile.EditNameFragment
import com.android.sanskrit.mine.fragment.profile.EditPhoneFragment
import com.android.sanskrit.user.fragment.BindPhoneFragment
import com.android.utils.LogUtil
import com.android.utils.data.FileData
import com.android.utils.data.IMG
import com.android.widget.ZdButton
import com.android.widget.ZdDialog
import com.android.widget.ZdToast
import com.android.widget.adapter.ZdListAdapter
import kotlinx.android.synthetic.main.mine_profile_edit_fragment.bindMobile
import kotlinx.android.synthetic.main.mine_profile_edit_fragment.userProfileAge
import kotlinx.android.synthetic.main.mine_profile_edit_fragment.userProfileAgeL
import kotlinx.android.synthetic.main.mine_profile_edit_fragment.userProfileBirthday
import kotlinx.android.synthetic.main.mine_profile_edit_fragment.userProfileBirthdayL
import kotlinx.android.synthetic.main.mine_profile_edit_fragment.userProfileCity
import kotlinx.android.synthetic.main.mine_profile_edit_fragment.userProfileCityL
import kotlinx.android.synthetic.main.mine_profile_edit_fragment.userProfileConstellation
import kotlinx.android.synthetic.main.mine_profile_edit_fragment.userProfileConstellationL
import kotlinx.android.synthetic.main.mine_profile_edit_fragment.userProfileHobby
import kotlinx.android.synthetic.main.mine_profile_edit_fragment.userProfileHobbyL
import kotlinx.android.synthetic.main.mine_profile_edit_fragment.userProfileHobbyTag
import kotlinx.android.synthetic.main.mine_profile_edit_fragment.userProfileIcon
import kotlinx.android.synthetic.main.mine_profile_edit_fragment.userProfileId
import kotlinx.android.synthetic.main.mine_profile_edit_fragment.userProfileIntro
import kotlinx.android.synthetic.main.mine_profile_edit_fragment.userProfileIntroL
import kotlinx.android.synthetic.main.mine_profile_edit_fragment.userProfileNick
import kotlinx.android.synthetic.main.mine_profile_edit_fragment.userProfileNickL
import kotlinx.android.synthetic.main.mine_profile_edit_fragment.userProfilePhone
import kotlinx.android.synthetic.main.mine_profile_edit_fragment.userProfilePhoneL
import kotlinx.android.synthetic.main.mine_profile_edit_fragment.userProfileSex
import kotlinx.android.synthetic.main.mine_profile_edit_fragment.userProfileSexL
import kotlinx.android.synthetic.main.mine_profile_edit_fragment.userProfileZodiac
import kotlinx.android.synthetic.main.mine_profile_edit_fragment.userProfileZodiacL

/**
 * created by jiangshide on 2019-08-17.
 * email:18311271399@163.com
 */

class EditProfileFragment : MyFragment(), HttpFileListener, OnFileListener {
  lateinit var adapter: ZdListAdapter<BaseBean>
  private var birthday: String? = null
  private lateinit var cityDialog: CityDialog

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.mine_profile_edit_fragment)
  }

  @RequiresApi(VERSION_CODES.JELLY_BEAN)
  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    cityDialog = CityDialog(activity)
    userProfileIcon.setOnClickListener {
      Files().setType(IMG)
          .setMax(1)
          .setSingleListener(this)
          .setSingleListener(this)
          .open(this)
    }

    bindMobile.setOnClickListener {
      push(BindPhoneFragment(true).setTitle("绑定手机号"))
    }
    userProfileIntroL.setOnClickListener {
      push(EditIntroFragment())
    }

    userProfileSexL.setOnClickListener {
      ZdDialog.createList(activity, listOf("男", "女"))
          .setOnItemListener { parent, view, position, id ->
            val user = Resource.user
            user?.sex = position + 1
            upload(user)
          }
          .show()
    }

    userProfileBirthdayL.setOnClickListener {
      ZdDialog.createWheelBirthday(activity)
          .setBirthday(birthday)
          .setOnWheelBirthdayListener { isCancel, year, month, day ->
            if (isCancel) return@setOnWheelBirthdayListener
            birthday = "$year-$month-$day"
            val user = Resource.user
            user?.birthday = birthday!!
            upload(user)
          }
          .show()
    }

    userProfileCityL.setOnClickListener {
      cityDialog.create()
          .show()
      cityDialog.setCityListener { province, city, cityCode ->
        val user = Resource.user
        user?.city = city
        upload(user)
      }
    }

    userProfileNickL.setOnClickListener {
      push(EditNameFragment())
    }

    userProfilePhoneL.setOnClickListener {
      push(EditPhoneFragment())
    }

    userProfileHobbyL.setOnClickListener {
      push(EditHobbyFragment())
    }

    userVM!!.profile.observe(this, Observer {
      hiddle()
      if (it.error != null) {
        ZdToast.txt(it.error.msg)
        return@Observer
      }
      initUserInfo()
      ZdEvent.get()
          .with(REFRESH_MINE)
          .post(REFRESH_MINE)
    })
    initUserInfo()
    ZdEvent.get()
        .with(REFRESH_PROFILE)
        .observes(this, Observer {
          initUserInfo()
        })
  }

  private fun upload(user: User?) {
    userVM!!.profile(user)
    showLoading()
  }

  private fun initUserInfo() {
    val user = Resource.user
    LogUtil.e("user:", user)
    if (user == null) return

    Img.loadImageCircle(
        user.icon, userProfileIcon, R.mipmap.default_user
    )

    userProfileId.text = "ID: ${user.id}"

    if (!TextUtils.isEmpty(user.nick)) {
      userProfileNick.text = user.nick
      userProfileNick.setTextColor(getColor(R.color.black))
    }

    if (!TextUtils.isEmpty(user.intro)) {
      userProfileIntro.text = user.intro
      userProfileIntro.setTextColor(getColor(R.color.black))
    }

    userProfileSex.text = if (user.sex == 1) "男" else "女"

    if (!TextUtils.isEmpty(user.birthday)) {
      userProfileBirthday.text = user.birthday
      userProfileBirthday.setTextColor(getColor(R.color.black))
    }

    if(user.age > 3){
      userProfileAgeL.visibility = View.VISIBLE
      userProfileAge.text = "${user.age}后"
      userProfileAge.setTextColor(getColor(R.color.black))
    }else{
      userProfileAgeL.visibility = View.GONE
    }

    if (!TextUtils.isEmpty(user.zodiac)) {
      userProfileZodiacL.visibility = View.VISIBLE
      userProfileZodiac.text = user.zodiac
      userProfileZodiac.setTextColor(getColor(R.color.black))
    } else {
      userProfileZodiacL.visibility = View.GONE
    }

    if (!TextUtils.isEmpty(user.constellation)) {
      userProfileConstellationL.visibility = View.VISIBLE
      userProfileConstellation.text = user.constellation
      userProfileConstellation.setTextColor(getColor(R.color.black))
    } else {
      userProfileConstellationL.visibility = View.GONE
    }

    if (!TextUtils.isEmpty(user.city)) {
      userProfileCity.text = user.city
      userProfileCity.setTextColor(getColor(R.color.black))
    }

    if (!TextUtils.isEmpty(user.phone)) {
      userProfilePhone.text = user.phone
      userProfilePhone.setTextColor(getColor(R.color.black))
    }


    if (TextUtils.isEmpty(user.name)) {
      bindMobile.visibility = View.VISIBLE
    } else {
      bindMobile.visibility = View.GONE
    }
    user.hobby
    if (!TextUtils.isEmpty(user.hobby)) {
      userProfileHobby.text = "去编辑"
      showView(user.hobby!!.split(","))
    }else{
      userProfileHobby.text = "去设置"
    }
  }

  private fun showView(data: List<String>) {
    val views = ArrayList<ZdButton>()
    userProfileHobbyTag.removeAllViews()
    data.forEachIndexed { index, type ->
      val view = getView(R.layout.report_fragment_item)
      val zdButton = view.findViewById<ZdButton>(R.id.reportItemName)
      zdButton.text = type
      zdButton.setRandomColor()
      zdButton.setTextColor(getColor(R.color.white))
      view.id = index
      userProfileHobbyTag.addView(view)
      views.add(zdButton)
    }
  }

  override fun onSuccess(
    position: Int,
    url: String
  ) {
    LogUtil.e("url:", url)
    val user = Resource.user
    user?.icon = url
    upload(user)
  }

  override fun onFailure(
    clientExcepion: Exception,
    serviceException: Exception
  ) {
    ZdToast.txt(serviceException?.message)
  }

  override fun onProgress(
    currentSize: Long,
    totalSize: Long,
    progress: Int
  ) {
  }

  override fun onFile(fileData: FileData) {
    LogUtil.e("fileData:", fileData)
    Img.loadImageCircle(fileData?.path, userProfileIcon)
    OssClient.instance.setListener(this)
        .setFileData(fileData!!)
        .start()
  }
}