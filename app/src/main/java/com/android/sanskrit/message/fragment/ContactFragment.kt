package com.android.sanskrit.message.fragment

import android.content.AsyncQueryHandler
import android.content.ContentResolver
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.img.Img
import com.android.resource.MyFragment
import com.android.resource.vm.user.data.ContactsInfo
import com.android.sanskrit.R
import com.android.utils.AppUtil
import com.android.utils.LogUtil
import com.android.utils.SystemUtil
import com.android.widget.adapter.KAdapter
import com.android.widget.adapter.create
import kotlinx.android.synthetic.main.message_contact_fragment.contactEdit
import kotlinx.android.synthetic.main.message_contact_fragment.contactsRecycleView
import kotlinx.android.synthetic.main.message_contact_fragment_item.view.contactItemDes
import kotlinx.android.synthetic.main.message_contact_fragment_item.view.contactItemIcon
import kotlinx.android.synthetic.main.message_contact_fragment_item.view.contactItemIconBtn
import kotlinx.android.synthetic.main.message_contact_fragment_item.view.contactItemName

/**
 * created by jiangshide on 2020/3/20.
 * email:18311271399@163.com
 */
class ContactFragment : MyFragment(), OnContactListener {

  private var adapter: KAdapter<ContactsInfo>? = null
  private var mData = ArrayList<ContactsInfo>()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return setTitleView(R.layout.message_contact_fragment)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    setTitle("联系人")
    contactEdit.setListener { s, input ->
      if (TextUtils.isEmpty(input)) {
        adapter?.add(mData, true)
        setSmallTitle("(共${mData.size}人)")
        return@setListener
      }
      LogUtil.e("mData:",mData)
      val data = ArrayList<ContactsInfo>()
      mData.forEach {
            if (it.name.trim().toLowerCase().contains(input) || it.number.trim().toLowerCase().contains(input)) {
              data.add(it)
            }
          }
      adapter?.add(data, true)
      setSmallTitle("(共${data.size}人)")
    }
    loadData()
  }

  private fun showView(data: ArrayList<ContactsInfo>) {
    mData.clear()
    mData.addAll(data)
    setSmallTitle("(共${data.size}人)")
    hiddle()
    adapter = contactsRecycleView.create(data, R.layout.message_contact_fragment_item, {
      val contactsInfo = it
      if (TextUtils.isEmpty(it.photo)) {
        if (!TextUtils.isEmpty(it.name)) {
          val length = it.name.length
          contactItemIconBtn.text = it.name.substring(length - 1, length)
          contactItemIconBtn.setRandomColor()
          contactItemIcon.visibility = View.GONE
        }
      } else {
        contactItemIcon.visibility = View.VISIBLE
        Img.loadImageCircle(it.photo, contactItemIcon, R.mipmap.default_user)
      }
      contactItemName.text = it.name
      contactItemDes.text = it.number
      contactItemDes.setOnClickListener {
        SystemUtil.call(mActivity,contactsInfo.number)
      }
      LogUtil.e("this:", it)
    }, {
      SystemUtil.sendSMS(mActivity,number,"http://www.baidu.com")
    })
  }

  private fun loadData() {
    showLoading()
    val asyncQueryHandler = ContactAsyncQueryHandler(
        mActivity.contentResolver, this
    )
    val uri = Phone.CONTENT_URI
    val projection = arrayOf<String?>(
        Phone.CONTACT_ID,
        Phone._ID,
        Phone.DISPLAY_NAME,
        Phone.DATA1,
        Phone.STARRED,
        Phone.PHOTO_THUMBNAIL_URI,
        "sort_key"
    )
    asyncQueryHandler!!.startQuery(
        0, null, uri, projection, null, null, "sort_key COLLATE LOCALIZED asc"
    )
  }

  private class ContactAsyncQueryHandler(
    cr: ContentResolver?,
    callback: OnContactListener?
  ) :
      AsyncQueryHandler(cr) {
    private val mCallback: OnContactListener? = callback
    override fun onQueryComplete(
      token: Int,
      cookie: Any?,
      cursor: Cursor?
    ) {
      val contactIdMap: MutableMap<Int?, ContactsInfo?>?
      val contactsInfos: ArrayList<ContactsInfo> = ArrayList<ContactsInfo>()
      if (cursor != null && cursor.count > 0) {
        contactIdMap = HashMap()
        cursor.moveToFirst()
        var oldNumber = ""
        for (i in 0 until cursor.count) {
          cursor.moveToPosition(i)
          LogUtil.e("cursor:", cursor)
          val contactId: Int = cursor.getInt(0)
          val id: Int = cursor.getInt(1)
          val name: String = cursor.getString(2)
          val number: String = cursor.getString(3)
          if (TextUtils.isEmpty(number) || number == oldNumber) {
            continue
          }
          oldNumber = number
          val started: Int = cursor.getInt(4)
          var photo: String = ""
          try {
            photo = cursor.getString(5)
          } catch (e: Exception) {
            LogUtil.e(e)
          }
          if (!contactIdMap!!.containsKey(contactId) && name != null && number != null) {
            val contactsInfo = ContactsInfo()
            contactsInfo.id = contactId
            contactsInfo.name = name
            contactsInfo.number = number
            contactsInfo.photo = photo
            contactsInfo.started = started
            contactsInfos.add(contactsInfo)
            contactIdMap[contactId] = contactsInfo
          }
        }
        cursor.close()
      }
      mCallback?.data(contactsInfos)
    }

  }

  override fun data(data: ArrayList<ContactsInfo>) {
    showView(data)
  }
}

interface OnContactListener {
  fun data(data: ArrayList<ContactsInfo>)
}