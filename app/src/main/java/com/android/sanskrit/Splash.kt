package com.android.sanskrit

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.android.base.BaseActivity
import com.android.location.ZdLocation
import com.android.resource.Resource
import com.android.sanskrit.user.UserFragment
import com.android.sanskrit.user.fragment.BindPhoneFragment
import com.android.utils.LogUtil
import com.android.widget.ZdDialog
import com.android.widget.ZdToast
import com.permissionx.guolindev.PermissionX

class Splash : BaseActivity(), ZdDialog.DialogViewListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Resource.first) {
            ZdDialog.createView(this, R.layout.protocol_summary_of_agreement, this)
                .show()
        } else {
            requestPermission()
        }
    }

    private fun request() {
        ZdLocation.getInstance().nativeLocation
        val user = Resource.user
        if (user != null) {
            if (TextUtils.isEmpty(user.name) && user.ignoreBind == 0) {
                goFragment("绑定手机号", BindPhoneFragment::class.java)
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
        } else {
            goFragment(UserFragment::class.java)
        }
        finish()
    }

    override fun onView(view: View) {
        val summaryOfAgreementSubmit = view.findViewById<TextView>(R.id.summaryOfAgreementSubmit)
        userProtocol(view.findViewById<TextView>(R.id.summaryOfAgreement))
        summaryOfAgreementSubmit.setOnClickListener {
            ZdDialog.cancelDialog()
            requestPermission()
        }
    }

    private fun requestPermission() {
        PermissionX.init(this)
            .permissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_CONTACTS
//            Manifest.permission.READ_PHONE_NUMBERS,
//                Manifest.permission.READ_CALENDAR
            )
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
//                    Toast.makeText(this, "All permissions are granted", Toast.LENGTH_LONG).show()
                    request();
                } else {
                    ZdToast.makeText(
                        this,
                        "These permissions are denied: $deniedList",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
    }

    private fun userProtocol(textView: TextView) {
        val des = getString(R.string.summary_of_agreement)
        val protocol1 = des + getString(R.string.user_protocol)
        val and = "$protocol1 ${getString(R.string.add)} "
        val protocol2 = and + getString(R.string.protect_protocol)
        val sufixDes = (protocol2
                + getString(R.string.summary_protocol_end))
        textView.highlightColor = ContextCompat.getColor(this, R.color.alpha)
        val spannableStringBuilder =
            SpannableStringBuilder()
        spannableStringBuilder.append(
            sufixDes
        )
        val userClick: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openUrl(BuildConfig.USE_AGREEMENT, "隐私保护指引")
            }
        }
        val policyClick: ClickableSpan =
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    openUrl(BuildConfig.PRIVACY_AGREEMENT, "软件许可及服务协议")
                }
            }
        spannableStringBuilder.setSpan(
            userClick, des.length, protocol1.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableStringBuilder.setSpan(
            policyClick, and.length, protocol2.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val userColor =
            ForegroundColorSpan(Color.parseColor("#9599F9"))
        val policyColor =
            ForegroundColorSpan(Color.parseColor("#9599F9"))

        spannableStringBuilder.setSpan(
            StyleSpan(Typeface.BOLD_ITALIC), des.length,
            protocol1.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableStringBuilder.setSpan(
            StyleSpan(Typeface.BOLD_ITALIC), and.length,
            protocol2.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableStringBuilder.setSpan(
            userColor, des.length, protocol1.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableStringBuilder.setSpan(
            policyColor, and.length, protocol2.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.text = spannableStringBuilder
    }
}
