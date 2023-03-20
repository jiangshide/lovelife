package com.android.web.fragment

import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import com.android.base.BaseFragment
import com.android.network.NetWork
import com.android.refresh.api.RefreshLayout
import com.android.utils.FileUtil
import com.android.utils.LogUtil
import com.android.web.R
import com.android.web.Web
import kotlinx.android.synthetic.main.webview.zdWebView
import kotlinx.android.synthetic.main.webview.zdWebViewProgress

/**
 * created by jiangshide on 2020-01-23.
 * email:18311271399@163.com
 */
class WebFragment : BaseFragment() {

  private var web: Web? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.webview)
  }

  override fun onTips(view: View?) {
    super.onTips(view)
    load()
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    web = arguments?.getParcelable("web")!!
    val title = FileUtil.getFileNameNoExtension(web?.mUrl)
    if (!TextUtils.isEmpty(title)) {
      web?.mTitle = title
    }
    setLeftListener {
      if (zdWebView.bridgeWebView.canGoBack()) {
        zdWebView.bridgeWebView.goBack()
        return@setLeftListener
      }
      if (web!!.fromFragment) pop() else finish()
    }.setTitle(web?.mTitle)
        .setTopBgIcon(web!!.mTopColor)
        .setTitleColor(web!!.mTitleColor)
        .setSmallTitleColor(web!!.mSmallTitleColor)
        .setRight(web!!.mRightTxt)
        .setRightColor(web!!.mRightTxtColor)
        .setRightListener {
          if (web!!.fromFragment) pop() else finish()
        }

    zdWebView.bridgeWebView.webViewClient = object : WebViewClient() {
      @RequiresApi(VERSION_CODES.LOLLIPOP)
      override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest,
        error: WebResourceError?
      ) {
        super.onReceivedError(view, request, error)
        if (request.isForMainFrame) {
          if (zdWebView == null || zdWebView.bridgeWebView == null) {
            return
          }
          zdWebView.bridgeWebView.loadUrl("about:blank")
          if (NetWork.instance.isNetworkAvailable()) {
            noData()
          } else {
            noNet()
          }
        }
      }
    }

    zdWebView.bridgeWebView.webChromeClient = object : WebChromeClient() {//todo:pickFile();

      override fun onProgressChanged(
        view: WebView?,
        newProgress: Int
      ) {
        super.onProgressChanged(view, newProgress)
        cancelRefresh()
        try {
          zdWebViewProgress.visibility = View.VISIBLE
          zdWebViewProgress.progress = newProgress
          if (newProgress == 100) {
            zdWebViewProgress.visibility = View.GONE
          }
        } catch (e: Exception) {
          LogUtil.e(e)
        }
      }

      override fun onJsAlert(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
      ): Boolean {
        return super.onJsAlert(view, url, message, result)
      }

      override fun onJsPrompt(
        view: WebView?,
        url: String?,
        message: String?,
        defaultValue: String?,
        result: JsPromptResult?
      ): Boolean {
        return super.onJsPrompt(view, url, message, defaultValue, result)
      }

      override fun onReceivedTitle(
        view: WebView?,
        title: String?
      ) {
        super.onReceivedTitle(view, title)
        LogUtil.e("title:", title)
        if (!TextUtils.isEmpty(title)) {
          setTitle(title)
        }
      }
    }
    load()
  }

  private fun load() {
    if (FileUtil.isDoc(web?.mUrl)) {
      if (web!!.mIsRemote) {
        zdWebView.loadOnlinePDF(web?.mUrl)
      } else {
        zdWebView.loadLocalPDF(web?.mUrl)
      }
    } else {
      zdWebView.loadUrl(web?.mUrl)
    }
  }

  override fun onRefresh(refreshLayout: RefreshLayout) {
    super.onRefresh(refreshLayout)
    load()
  }
}