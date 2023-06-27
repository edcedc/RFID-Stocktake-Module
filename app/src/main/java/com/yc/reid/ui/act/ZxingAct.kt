package com.yc.reid.ui.act

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.LogUtils
import com.uuzuche.lib_zxing.activity.CaptureFragment
import com.uuzuche.lib_zxing.activity.CodeUtils
import com.uuzuche.lib_zxing.activity.CodeUtils.AnalyzeCallback
import com.yc.reid.R
import com.yc.reid.SCAN_STATUS_QRCODE
import com.yc.reid.SCAN_STATUS_SCAN
import com.yc.reid.base.BaseActivity
import com.yc.reid.event.StockTakeListEvent
import com.yc.reid.utils.ImageUtils
import kotlinx.android.synthetic.main.a_zxing.iv_open
import kotlinx.android.synthetic.main.a_zxing.iv_photo
import org.greenrobot.eventbus.EventBus


/**
 * @Author nike
 * @Date 2023/6/19 17:35
 * @Description 二维码
 */
class ZxingAct  : BaseActivity(), OnClickListener {

    /**
     * 扫描跳转Activity RequestCode
     */
    val REQUEST_CODE = 111

    /**
     * 选择系统图片Request Code
     */
    val REQUEST_IMAGE = 112

    var isOpen = false

    var stocktakeno: String? = null

    override fun getLayoutId(): Int = R.layout.a_zxing

    override fun initView() {
        setTitle("Scan code")
        iv_open.setOnClickListener(this)
        iv_photo.setOnClickListener(this)

        val captureFragment = CaptureFragment()
        // 为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.f_zxing);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();
    }

    override fun initParms(bundle: Bundle) {
        stocktakeno = bundle.getString("stocktakeno")
    }

    /**
     * 二维码解析回调函数
     */
    var analyzeCallback: AnalyzeCallback = object : AnalyzeCallback {
        override fun onAnalyzeSuccess(mBitmap: Bitmap, result: String) {
            val resultIntent = Intent()
            val bundle = Bundle()
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS)
            bundle.putString(CodeUtils.RESULT_STRING, result)
            resultIntent.putExtras(bundle)
            LogUtils.e("onAnalyzeSuccess；" + result)
            EventBus.getDefault().post(StockTakeListEvent(result, "0", true, SCAN_STATUS_QRCODE))
            finish()
//            this@SecondActivity.setResult(RESULT_OK, resultIntent)
//            this@SecondActivity.finish()
        }

        override fun onAnalyzeFailed() {
            val resultIntent = Intent()
            val bundle = Bundle()
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED)
            bundle.putString(CodeUtils.RESULT_STRING, "")
            resultIntent.putExtras(bundle)
            LogUtils.e("onAnalyzeFailed")
//            this@SecondActivity.setResult(RESULT_OK, resultIntent)
//            this@SecondActivity.finish()
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.iv_open ->{
                if (!isOpen) {
                    iv_open.background = ContextCompat.getDrawable(this, R.drawable.dl_loc)
                    CodeUtils.isLightEnable(true);
                    isOpen = true;
                } else {
                    iv_open.background = ContextCompat.getDrawable(this, R.drawable.dl_filters)
                    CodeUtils.isLightEnable(false);
                    isOpen = false;
                }
            }
            R.id.iv_photo ->{
                val intent = Intent()
                intent.action = Intent.ACTION_PICK
                intent.type = "image/*"
                startActivityForResult(intent, REQUEST_IMAGE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE) {
            if (data != null) {
                val uri = data.data
                try {
                    CodeUtils.analyzeBitmap(
                        ImageUtils.getImageAbsolutePath(this, uri),
                        object : AnalyzeCallback {
                            override fun onAnalyzeSuccess(mBitmap: Bitmap, result: String) {
                                LogUtils.e("解析结果:$result")
                                EventBus.getDefault().post(StockTakeListEvent(result, "0", true, SCAN_STATUS_QRCODE))
                                finish()
                            }

                            override fun onAnalyzeFailed() {
                                LogUtils.e("解析二维码失败")
                            }
                        })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}