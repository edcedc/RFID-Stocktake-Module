package com.yc.reid.ui

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.ItemAnimator
import androidx.recyclerview.widget.SimpleItemAnimator
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.TimeUtils
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.decoration.GridSpacingItemDecoration
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnExternalPreviewEventListener
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.utils.DensityUtil
import com.lxj.xpopup.XPopup
import com.yc.reid.INVENTORY_READ
import com.yc.reid.R
import com.yc.reid.SCAN_STATUS_MANUALLY
import com.yc.reid.UPLOAD_IMAGE_SPLIT
import com.yc.reid.adapter.GridImageAdapter
import com.yc.reid.base.BaseFragment
import com.yc.reid.bean.sql.StockChildSql
import com.yc.reid.event.StockListUploadDataEvent
import com.yc.reid.mvp.impl.InventoryDescContract
import com.yc.reid.mvp.presenter.InventoryDescPresenter
import com.yc.reid.weight.FullyGridLayoutManager
import com.yc.reid.weight.GlideEngine
import com.yc.reid.weight.GlideLoadingUtils
import kotlinx.android.synthetic.main.f_inventory_upload.bt_sure
import kotlinx.android.synthetic.main.f_inventory_upload.et_company
import kotlinx.android.synthetic.main.f_inventory_upload.iv_image
import kotlinx.android.synthetic.main.f_inventory_upload.recyclerView
import kotlinx.android.synthetic.main.f_inventory_upload.tv_state
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import org.litepal.LitePal


/**
 * @Author nike
 * @Date 2023/6/8 19:42
 * @Description
 */
class InventoryDescUploadFrg : BaseFragment(), InventoryDescContract.View, OnClickListener {

    val mPresenter by lazy { InventoryDescPresenter() }

    var localMediaList = ArrayList<LocalMedia?>()

    var imageAdapter: GridImageAdapter? = null

    var data2Obj = JSONObject()

    var assetNo: String?= null

    var ids: String?= null
    //原来状态
    var default_state: Int = -1
    //改变后的状态
    var change_status: Int = -1

    var stockChildSql: StockChildSql? = null

    override fun getLayoutId(): Int = R.layout.f_inventory_upload

    override fun initParms(bundle: Bundle) {
        var bean = bundle.getString("bean")
        assetNo = bundle.getString("assetNo")
        ids = bundle.getString("ids")
        var jsonObject = JSONObject(bean)
        val data2 = JSONObject(jsonObject.optString("data")).optJSONArray("data2")
        for (i in 0 until data2.length()) {
            val obj = data2.optJSONObject(i)
            if (obj.optString("AssetNo").equals(assetNo)) {
                data2Obj = obj
                break
            }
        }
    }

    override fun initView(rootView: View) {
        mPresenter.init(this, activity)

        bt_sure.setOnClickListener(this)
        tv_state.setOnClickListener(this)
        stockChildSql = LitePal.where("ids=?", ids).findFirst(StockChildSql::class.java)
        if (stockChildSql != null){
            default_state = stockChildSql!!.type
            change_status = default_state
            tv_state.text = if (default_state == INVENTORY_READ) getText(R.string.in_stock) else getText(R.string.not_library)
            et_company.setText(stockChildSql!!.remarks)

            val iamgeList = stockChildSql!!.iamgeList
            if (!StringUtils.isEmpty(iamgeList)){
                val split = iamgeList!!.split(UPLOAD_IMAGE_SPLIT)
                split.forEachIndexed(){index, path ->
                    if (!StringUtils.isEmpty(path)){
                        localMediaList.add(LocalMedia.generateLocalMedia(activity, path))
                    }
                }
            }
        }
        val manager = FullyGridLayoutManager(activity,4, GridLayoutManager.VERTICAL, false)
        recyclerView.setLayoutManager(manager)
        val itemAnimator: ItemAnimator? = recyclerView.getItemAnimator()
        if (itemAnimator != null) {
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
        recyclerView.addItemDecoration(GridSpacingItemDecoration( 4,DensityUtil.dip2px(activity, 8f), false))
        imageAdapter = GridImageAdapter(activity, localMediaList)
        recyclerView.adapter = imageAdapter
        imageAdapter!!.selectMax = 3
        imageAdapter!!.setOnItemClickListener(object : GridImageAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                PictureSelector.create(activity)
                    .openPreview()
                    .setImageEngine(GlideEngine.createGlideEngine())
                    .setExternalPreviewEventListener(object : OnExternalPreviewEventListener {
                        override fun onPreviewDelete(position: Int) {
                            localMediaList.removeAt(position)
                            imageAdapter!!.remove(position)
                            imageAdapter!!.notifyDataSetChanged()
                        }
                        override fun onLongPressDownload( context: Context?,media: LocalMedia?): Boolean {
                            return false
                        }
                    }).startActivityPreview(position, true, localMediaList)
            }

            override fun openPicture() {
                PictureSelector.create(activity)
                    .openGallery(SelectMimeType.ofImage())
                    .setMaxSelectNum(3)
                    .setImageEngine(GlideEngine.createGlideEngine())
                    .forResult(object : OnResultCallbackListener<LocalMedia?> {
                        override fun onResult(result: ArrayList<LocalMedia?>) {
                            localMediaList.clear()
                            localMediaList.addAll(result)
                            imageAdapter!!.getData().clear()
                            imageAdapter!!.getData().addAll(result)
                            imageAdapter!!.notifyDataSetChanged()
                        }
                        override fun onCancel() {}
                    })
            }

        })
    }

    override fun setImage(bitmap: Bitmap?) {
        GlideLoadingUtils.load(activity, bitmap, iv_image)
    }

    override fun setRefreshLayoutMode(totalRow: Int) {}

    override fun setData(objects: Object) {}

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_state ->{
                XPopup.Builder(context)
                    .asCenterList(
                        getText(R.string.state), arrayOf(getString(R.string.not_library), getString(R.string.in_stock)),
                        null, default_state
                    ) { position, text ->
                        change_status = position
                        tv_state.text = if (change_status == INVENTORY_READ) getText(R.string.in_stock) else getText(R.string.not_library)
                    }.show()
            }
            R.id.bt_sure -> {
//                mPresenter.submit(data2Obj, localMediaList, et_company.getText().toString())
                val sb = StringBuffer()
                if (localMediaList.size != 0){
                    localMediaList.forEachIndexed(){i, bean ->
                        sb.append(bean!!.realPath).append(UPLOAD_IMAGE_SPLIT)
                    }
                }
                val remarks = et_company.text.toString()
                if (localMediaList.size == 0 && StringUtils.isEmpty(remarks) && default_state == change_status){
                    showToast(getString(R.string.error_))
                    return
                }
                //保存是否在库
                if (default_state != change_status){
                    stockChildSql!!.type = change_status
                    default_state = change_status
                }
                //存图片
                if (sb != null){
                    stockChildSql!!.iamgeList = sb.toString()
                }
                stockChildSql!!.remarks = remarks
                //判断时间在不在
                var scanTime = stockChildSql!!.scan_time
                if (StringUtils.isEmpty(scanTime)){
                    stockChildSql!!.scan_time = TimeUtils.getNowString()
                }
                if (stockChildSql!!.scan_status == 0){
                    stockChildSql!!.scan_status = SCAN_STATUS_MANUALLY
                }
                stockChildSql!!.save()
                var hahaha = LitePal.where("ids=?", ids).findFirst(StockChildSql::class.java)
                LogUtils.e(hahaha.type, hahaha.iamgeList, hahaha.remarks)
                showToast(getString(R.string.saved_successfully))
                EventBus.getDefault().post(StockListUploadDataEvent())
            }
        }
    }

}

