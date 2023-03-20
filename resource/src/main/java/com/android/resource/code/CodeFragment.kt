package com.android.resource.code

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.resource.MyFragment
import com.android.resource.R.layout
import com.android.resource.code.SideBar.OnLetterChangeListener
import kotlinx.android.synthetic.main.activity_pick.codeSearchEdit
import kotlinx.android.synthetic.main.activity_pick.rv_pick
import kotlinx.android.synthetic.main.activity_pick.side
import kotlinx.android.synthetic.main.activity_pick.tv_letter
import java.util.ArrayList

/**
 * created by jiangshide on 2020/3/22.
 * email:18311271399@163.com
 */
class CodeFragment : MyFragment() {
  private val selectedCountries = ArrayList<Country>()
  private val allCountries = ArrayList<Country>()
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return  setTopView(false).setTitleView(layout.activity_pick)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    allCountries.clear()
    allCountries.addAll(Country.getAll(mActivity, null))
    selectedCountries.clear()
    selectedCountries.addAll(allCountries)
    val adapter =
      CAdapter(selectedCountries, mActivity)
    rv_pick.adapter = adapter
    val manager = LinearLayoutManager(mActivity)
    rv_pick.layoutManager = manager
    rv_pick.adapter = adapter
    rv_pick.addItemDecoration(DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL))
    codeSearchEdit.setListener { s, input ->
      selectedCountries.clear()
      for (country in allCountries) {
        if (country.name.toLowerCase()
                .contains(input.toLowerCase())
        ) {
          selectedCountries.add(country)
        }
      }
      adapter.update(selectedCountries)
    }
    side.addIndex("#", side.indexes.size)
    side.onLetterChangeListener = object : OnLetterChangeListener {
      override fun onLetterChange(letter: String) {
        tv_letter.visibility = View.VISIBLE
        tv_letter.text = letter
        val position = adapter.getLetterPosition(letter)
        if (position != -1) {
          manager.scrollToPositionWithOffset(position, 0)
        }
      }

      override fun onReset() {
        tv_letter.visibility = View.GONE
      }
    }
  }
}