package com.yc.reid.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.text.TextUtils
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.ContextCompat
import androidx.core.os.ConfigurationCompat
import com.blankj.utilcode.util.LanguageUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.luck.picture.lib.language.PictureLanguageUtils.setAppLanguage
import com.yc.reid.R
import com.yc.reid.api.CloudApi
import com.yc.reid.base.BaseFragment
import com.yc.reid.bean.sql.ConfigDataSql
import com.yc.reid.mar.MyApplication
import com.yc.reid.ui.act.LoginAct
import com.yc.reid.utils.PopupWindowTool
import kotlinx.android.synthetic.main.f_set.bt_sure
import kotlinx.android.synthetic.main.f_set.et_company
import kotlinx.android.synthetic.main.f_set.et_host
import kotlinx.android.synthetic.main.f_set.iv_close
import kotlinx.android.synthetic.main.f_set.tv_language
import org.litepal.LitePal.findFirst
import java.util.Locale


/**
 * @Author nike
 * @Date 2023/5/30 16:46
 * @Description
 */
class SetFrg : BaseFragment(), OnClickListener{

    var languagePosition: Int = 0

    var languageChoosePosition: Int = 0

    override fun getLayoutId(): Int = R.layout.f_set

    override fun initParms(bundle: Bundle) {
    }

    override fun initView(rootView: View) {
//        setTitle(getString(R.string.back))
        iv_close.setOnClickListener { pop() }

        val left = ContextCompat.getDrawable(requireContext(), R.mipmap.language_setting)
        left?.setBounds(0, 0, 70, 70) //必须设置图片的大小否则没有作用
//        tv_language.setCompoundDrawables(null, null, left, null) //设置图片left这里如果是右边就放到第二个参数里面依次对应

        tv_language.setOnClickListener(this)
        bt_sure.setOnClickListener(this)

        val bean = findFirst(ConfigDataSql::class.java)
        if (bean != null){
            et_company.setText(bean.companyid)
            CloudApi.SERVLET_URL = bean.url.toString()
        }

        when(MyApplication.getCurrentLanguage()){
            "zh" -> {
                tv_language.text = getText(R.string.s_chinese)
                languagePosition = 0
            }
            "zh-rHK" -> {
                tv_language.text = getText(R.string.t_chinese)
                languagePosition = 1
            }
            "en" -> {
                tv_language.text = getText(R.string.e_english)
                languagePosition = 2
            }
        }
        languageChoosePosition = languagePosition
        et_host.setText(bean.url)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.tv_language ->{//设置语言
                PopupWindowTool.showListDialog(activity)
                    .asCenterList(getString(R.string.please_language),
                        arrayOf(
                            getString(R.string.s_chinese),
                            getString(R.string.t_chinese),
                            getString(R.string.e_english),
                        ),{ position, text ->
                            when(position){
                                0 -> tv_language.text = getText(R.string.s_chinese)
                                1 -> tv_language.text = getText(R.string.t_chinese)
                                2 -> tv_language.text = getText(R.string.e_english)
                            }
                            languagePosition = position
                        }).show()
            }
            R.id.bt_sure ->{
                if (languageChoosePosition != languagePosition){
                    when(languagePosition){
                        0 -> {
                            MyApplication.setCurrentLanguage("zh")
                        }
                        1 ->{
                            MyApplication.setCurrentLanguage("zh-rHK")
                        }
                        2 -> {
                            MyApplication.setCurrentLanguage("en")
                        }
                    }
                }

                val url = et_host.text.toString()
                val company = et_company.text.toString()
                if (StringUtils.isEmpty(url) || StringUtils.isEmpty(company) || languageChoosePosition == languagePosition){
                    showToast(getString(R.string.error_))
                    return
                }
                val bean = findFirst(ConfigDataSql::class.java)
                if (!StringUtils.isEmpty(url)){
                    bean.url = url
                }
                if (!StringUtils.isEmpty(company)){
                    bean.companyid = company
                }
                bean.save()
                refreshUI()
            }
        }
    }

    private fun refreshUI() {
        recreate(requireActivity())
    }


}