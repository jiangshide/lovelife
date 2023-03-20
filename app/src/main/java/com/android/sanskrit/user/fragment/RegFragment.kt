package com.android.sanskrit.user.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.event.ZdEvent
import com.android.files.Files
import com.android.files.Files.OnFileListener
import com.android.files.fragment.File_SHOW_TOP
import com.android.http.oss.OssClient
import com.android.http.oss.OssClient.HttpFileListener
import com.android.img.Img
import com.android.resource.FINISH_VALIDATE
import com.android.resource.MyFragment
import com.android.resource.REFRESH_PROFILE
import com.android.resource.Resource
import com.android.resource.data.DeviceData
import com.android.resource.data.PositionData
import com.android.sanskrit.MainActivity
import com.android.sanskrit.R
import com.android.utils.DateUtil.OnDateListener
import com.android.utils.LogUtil
import com.android.utils.SPUtil
import com.android.utils.data.FileData
import com.android.utils.data.IMG
import com.android.widget.ZdToast
import kotlinx.android.synthetic.main.user_reg_fragment.*

/**
 * created by jiangshide on 2020/3/18.
 * email:18311271399@163.com
 */
class RegFragment : MyFragment(), OnDateListener, HttpFileListener, OnFileListener {

    private var name = ""
    private var validateCode = ""
    private var psw = ""
    private var rePsw = ""
    private var sex = -1
    private var icon = ""
    private var country = ""
    private var isChecked = true

    private var isReg = false

    private val COUNTDOWN_TIME: Long = 60

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setTopView(false).setTitleView(R.layout.user_reg_fragment)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        this.name = arguments?.getString("name")!!
        this.isReg = arguments?.getBoolean("isReg")!!
        this.country = arguments?.getString("country")!!
        val positionData = PositionData()
        val deviceData = DeviceData(mActivity)
        userName.text = "${this.name}(+${this.country})"
        if (!this.isReg) {
            userIconTips.visibility = View.GONE
            userSexL.visibility = View.GONE
            Img.loadImageCircle(icon, userIcon, R.mipmap.ic_launcher)
            userIconL.isEnabled = false
        }
        userIconL.setOnClickListener {
            clearFocus()
            SPUtil.putInt(File_SHOW_TOP, 1)
            Files().setType(IMG)
                .setMax(1)
                .setFloat(true)
                .setSingleListener(this)
                .open(mActivity)
        }

        userSexGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.userSexGroupMan -> {
                    sex = 1
                }
                R.id.userSexGroupWoman -> {
                    sex = 2
                }
            }
            validate()
            clearFocus()
        }

        userValidateCode.setListener { s, input ->
            this.validateCode = input
            validate()
        }
        userValidateCodeSend.setOnClickListener {
            countDown()
            clearFocus()
        }

        userPswEdit.setListener { s, input ->
            this.psw = input
            validate()
        }

        userConfirmEdit.setListener { s, input ->
            this.rePsw = input
            validate()
        }

        userSubmitAudit.setOnClickListener {
            if (this.isReg) {
                userVM?.reg(
                    this.name, this.psw, this.validateCode, this.country, this.icon, this.sex,
                    positionData.gson, deviceData.gson
                )
                return@setOnClickListener
            }
            userVM?.forgetPsw(this.name, this.psw, this.validateCode)
            clearFocus()
        }

        userProtocol.setOnCheckedChangeListener { buttonView, isChecked ->
            this.isChecked = isChecked
            if (this.isChecked) {
                userProtocol.setTextColor(getColor(R.color.font))
            } else {
                userProtocol.setTextColor(getColor(R.color.fontLight))
            }
            validate()
        }

        userVM!!.validate.observe(this, Observer {
            if (it.error != null) {
                ZdToast.txt(it.error.message)
                return@Observer
            }
            userValidateCode.setText(it.data)
        })

        userVM!!.forgetPsw.observe(this, Observer {
            if (it.error != null) {
                ZdToast.txt(it.error.message)
                return@Observer
            }
            Resource.name = this.name
//      if(it.data){
            ZdEvent.get()
                .with(REFRESH_PROFILE)
                .post(REFRESH_PROFILE)
            ZdEvent.get()
                .with(FINISH_VALIDATE)
                .post(FINISH_VALIDATE)
            finish()
//      }
        })

        userVM!!.reg.observe(this, Observer {
            LogUtil.e("err:", it.error, " | data:", it.data)
            if (it.error != null) {
                ZdToast.txt(it.error.message)
                return@Observer
            }
            Resource.user = it.data
            Resource.name = this.name
            go(MainActivity::class.java)
        })
        countDown()
    }

    private fun clearFocus() {
        userValidateCode.clearFocus()
        userPswEdit.clearFocus()
        userConfirmEdit.clearFocus()
    }

    private fun countDown() {
        userVM?.validate(this.name)
        countDown(COUNTDOWN_TIME).setListener(this)
            .start()
        userValidateCodeSend.isEnabled = false
        userValidateCodeSend.setTextColor(getColor(R.color.fontLight))
    }

    private fun validate() {
        var disable =
            TextUtils.isEmpty(this.validateCode) || TextUtils.isEmpty(this.psw) || TextUtils.isEmpty(
                this.rePsw
            ) || this.psw != this.rePsw || !this.isChecked

        if (this.isReg) {
            disable =
                TextUtils.isEmpty(this.validateCode) || TextUtils.isEmpty(this.psw) || TextUtils.isEmpty(
                    this.rePsw
                ) || this.psw != this.rePsw || !this.isChecked || TextUtils.isEmpty(icon) || sex == -1
        }

        userSubmitAudit.isEnabled = !disable

        userSubmitAudit.normalColor = if (disable) R.color.disable else R.color.blackLightMiddle
//    if (disable) {
//      userConfirmTipsSize.compoundDrawableTintList =
//        ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.disable))
//      userConfirmTipsType.compoundDrawableTintList =
//        ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.disable))
//    } else {
//      userConfirmTipsSize.compoundDrawableTintList =
//        ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.gray))
//      userConfirmTipsType.compoundDrawableTintList =
//        ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.gray))
//    }
    }

    override fun onFinish() {
        if (userValidateCodeSend == null) return
        userValidateCodeSend.isEnabled = true
        userValidateCodeSend.setTextColor(getColor(R.color.blueLight))
        userValidateCodeSend.text = getString(R.string.resend)
    }

    override fun onTick(millisUntilFinished: Long) {
        if (userValidateCodeSend == null) return
        userValidateCodeSend.text = "${millisUntilFinished / 1000}s ${getString(R.string.resend)}"
    }

    override fun onSuccess(
        position: Int,
        url: String
    ) {
        icon = url!!
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
        if (userIcon == null) return
        icon = fileData!!.path!!
        Img.loadImageCircle(icon, userIcon, R.mipmap.default_user)
        OssClient.instance.setListener(this)
            .setFileData(fileData!!)
            .start()
        userIconTips.visibility = View.GONE
        validate()
    }
}