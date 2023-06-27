package com.yc.reid.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.StringUtils
import com.yc.reid.R
import com.yc.reid.api.CloudApi
import com.yc.reid.base.BaseFragment
import com.yc.reid.bean.sql.ConfigDataSql
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
            when(bean.languagePosition){
                0 -> tv_language.text = getText(R.string.s_chinese)
                1 -> tv_language.text = getText(R.string.t_chinese)
                2 -> tv_language.text = getText(R.string.e_english)
            }
            languagePosition = bean.languagePosition!!
            et_host.setText(bean.url)
            et_company.setText(bean.companyid)
            CloudApi.SERVLET_URL = bean.url.toString()
        }
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
                            languagePosition = position
                            val resources = requireContext().resources
                            val dm = resources.displayMetrics
                            val config: Configuration = resources.configuration
                            // 应用用户选择语言
                            when(position){
                                0 -> config.locale = Locale.SIMPLIFIED_CHINESE
                                1 -> config.locale = Locale.TRADITIONAL_CHINESE
                                2 -> config.locale = Locale.ENGLISH;
                            }
                            resources.updateConfiguration(config, dm)

                            val intent = Intent(activity, LoginAct::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            requireActivity().startActivity(intent)
                            // 杀掉进程
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(0);
                        }).show()
            }
            R.id.bt_sure ->{
                val url = et_host.text.toString()
                val company = et_company.text.toString()
                if (StringUtils.isEmpty(url) || StringUtils.isEmpty(company)){
                    showToast(getString(R.string.error_))
                    return
                }
                val bean = findFirst(ConfigDataSql::class.java)
                bean.languagePosition = languagePosition
                bean.url = url
                bean.companyid = company
                bean.save()
                pop()
            }
        }
    }

}