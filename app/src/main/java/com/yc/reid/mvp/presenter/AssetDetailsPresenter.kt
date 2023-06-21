package com.yc.reid.mvp.presenter

import android.net.Uri
import android.util.Base64
import android.util.Base64OutputStream
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.TimeUtils
import com.luck.picture.lib.entity.LocalMedia
import com.yc.reid.UPLOAD_IMAGE_SPLIT
import com.yc.reid.api.CloudApi
import com.yc.reid.base.BaseListPresenter
import com.yc.reid.bean.sql.ConfigDataSql
import com.yc.reid.bean.sql.UserDataSql
import com.yc.reid.mvp.impl.AssetDetailsContract
import com.yc.reid.utils.ImageUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import org.litepal.LitePal
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.net.URLEncoder


/**
 * @Author nike
 * @Date 2023/6/9 10:57
 * @Description
 */
class AssetDetailsPresenter : BaseListPresenter<AssetDetailsContract.View>(), AssetDetailsContract.Presenter{

    override fun onRequest(page: Int) {
        TODO("Not yet implemented")
    }

    override fun initData(position: Int, bean: String?) {
    }

    override fun submit(data2Obj: JSONObject, localMediaList: ArrayList<LocalMedia?>, remarks: String) {
        /*if (StringUtils.isEmpty(remarks)){
            showToast(act!!.getString(R.string.error_))
            return
        }*/

        val jsonArray = JSONArray()
        val obj = JSONObject()
        var bean = LitePal.findFirst(UserDataSql::class.java)
        obj.put("companyID", "RFIDInventory")
        obj.put("loginID", bean.LoginID!!)
        obj.put("orderNo", data2Obj.optString("orderNo"))
        obj.put("AssetNo", data2Obj.optString("AssetNo"))
        obj.put("ScanDate", TimeUtils.getNowString())
        obj.put("QRCode", "QRCode")
        obj.put("EPC", data2Obj.optString("LabelTag"))
        obj.put("Remarks", remarks)
        obj.put("statusID", "1")
        obj.put("FoundStatus", "1")


        val sbImagePath = StringBuffer()
        val sbImageBa64 = StringBuffer()
        localMediaList.forEachIndexed(){index, localMedia ->
            //////////// 图片保存本地 ////////////
//            val fileDir = Environment.getExternalStorageDirectory().toString() + "/000ks/"
//            val fileName = "00-ks-" + System.currentTimeMillis() + ".jpg"
//            val path = fileDir + fileName
//            if (!File(fileDir).exists()) {
//                File(fileDir).mkdirs()
//            }
//            val openImage = ImageUtils.openImage(localMedia.path)
//            val bitmap2Path = ImageUtils.bitmap2Path(openImage, path)

            //成功解码  500 有些图片不可以
//            val openImage = ImageUtils.openImage(localMedia.path)
//            val toBase64 = ImageUtils.encodeToBase64(openImage)
            //成功解码  200
//            val toBase64 = convertImageFileToBase64(File(localMedia.path))

            val uri = ImageUtils.getFilePathFromUri(act, Uri.fromFile(File(localMedia!!.path)))

            //成功解码  但是有些图片有问题
            val imageToBase64 = ImageUtils.imageToBase64(localMedia!!.realPath)
            sbImageBa64.append(imageToBase64).append(UPLOAD_IMAGE_SPLIT)
            LogUtils.e(localMedia!!.realPath, localMedia!!.availablePath)
//            val bitmap = ImageUtils.encodeToBitmap(imageToBase64)
//            mRootView?.setImage(bitmap)
        }
        //打印图片base64日记
//        FileUtils.writeTxtToFile(sbImageBa64.toString());

        val mediaType = "application/x-www-form-urlencoded".toMediaTypeOrNull()
        val client = OkHttpClient().newBuilder().build()
        val body = ("companyID=" + LitePal.findFirst(ConfigDataSql::class.java).companyid + "&strJson=" + URLEncoder.encode(sbImageBa64.toString(), "UTF-8") + "&loginID=" + bean.LoginID!!+
                "&orderNo=" + data2Obj.optString("orderNo") + "&AssetNo=" + data2Obj.optString("AssetNo")).toRequestBody(mediaType)
        val request: Request = Request.Builder()
            .url(CloudApi.SERVLET_URL + "FileToByte")
            .method("POST", body)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()
        ThreadUtils.executeByCpu(object : ThreadUtils.Task<Any?>() {
            @Throws(Throwable::class)
            override fun doInBackground(): Any? {
                val response: Response = client.newCall(request).execute()
                LogUtils.e(response)
                LogUtils.e("companyID=" + "RFIDInventory" + "&loginID=" + bean.LoginID!!+
                        "&orderNo=" + data2Obj.optString("orderNo") + "&AssetNo=" + data2Obj.optString("AssetNo"))
                return null
            }

            override fun onSuccess(result: Any?) {
                LogUtils.e(result.toString())
                showToast("保存成功")
            }
            override fun onCancel() {
                LogUtils.e("onCancel")
            }
            override fun onFail(t: Throwable) {
                LogUtils.e(t.message)
            }
        })

        val map = HashMap<String, RequestBody>()
        map.put("companyID", toRequestBody("companyID"))

         /*val disposable = RetrofitManager.service.UploadImage("132456", jsonArray.put(obj).toString())
            .compose(SchedulerUtils.ioToMain())
            .subscribe({ bean ->
                mRootView?.apply {
                    mRootView?.hideLoading()
                    if (bean.code == ErrorStatus.SUCCESS){

                    }
                }
            }, { t ->
                mRootView?.apply {
                    //处理异常
                    mRootView?.errorText(ExceptionHandle.handleException(t), ExceptionHandle.errorCode)
                }

            })
        addSubscription(disposable)*/
    }

    var sss: String = "/9j/4AAQSkZJRgABAQIAJQAlAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCADWAfQDASIAAhEBAxEB/8QAHAABAAIDAQEBAAAAAAAAAAAAAAIGAQQFAwcI/8QAThAAAQQBAQQCCwwIBgEDBQAAAQACAxEEBQYSITETIgcUF0FRVFWRkpTRFiMyNlNhZHGBk7GyFTRSc3SCodIzQkRjZeEkQ6LBNWJ1wvH/xAAaAQEBAQADAQAAAAAAAAAAAAAAAQIDBAUG/8QAMREBAAEDAgQEBQQCAwEAAAAAAAECERMDURIhQfAxUmHRBBRxgaEFIpGxMkIGU8Hh/9oADAMBAAIRAxEAPwDp7cbb7R6JtdHo2iY2PO12LFI2LtLppHOIN1XE8lWMrsq7aYE5gzcXDxpgATHPp3RuAPI0SCu3tfqjtK7KE7ydOME+kRQTx587oWyRuBtrXtBLT847y08fP2Ewpsw6dBp8uTvQP3NQmLscij0jI5HxvJbdd4E94help0URREzRfk69VVV55uUOzBtT/wAZ6kPapDsvbUnyZ6kPavdmobNti0x2IdCxsVj3nUcaeHppnu6QH3t7mFzm7vwaIFA2OK6nbuxGNlRkv0TIYZs+QhkNt3Hi4Gnqiq5Ad7vLkmjTj/Tf8MXqv/k57eyZts6ETDBxjEYzKJBph3SwGi6+W6DzPJeA7Le1B8m+pj2rraXtRoo0Vm5Jo+BqOXpeVFO0YzWxNmMjTG11td1d0HgbHhWnqh0mPYGLXDhYjdUz4/0eGsx2Nj3o3npJ2AAAEtDRYAq1ODTiedHp+Z9lvMxyq7tHf2Zj7JO2sojMeDjvErC+Pd00nfaOZFcwO+RyUJOyjtbA2N00OFGJW78Zfgboe3wizxHzhbWyW1umabszhjLy2Mz8Od+NHG4Ek48r43Pd9QAcFts1fZifaGZ0uoYcuFgxYuLix5UbOiljb/iODnRvPfPVAaT4UmiiJmODvu352SKqpi/F33f8buOOyxtN/wAd6mPaveHsnbWZAkMMGFKI2l7zHgb240d80eA+crZi1fZTEnwIYcbSJYH6tN2xJLj77o8XpAWkEjkQK43wscLXv+lNkpYnSSHS2znGzY6ZjBgsye88A2r3eR5/Opw6f/WTVVH+zmDsq7Snyd6mPapDsqbSHyd6mParA3O2Oz9ZEbINOkDdRd2qzHw+Bj6Hq7zWtt7d+7BvzKsbd4sWHLo0PRYkeZ2g12UMaJsYLyTxLQ1tEjwgJTTpVTETRa/tdZmqL/u8PezZHZS2k/4/1Me1ZHZR2j/4/wBUHtVJCmFzfL6Xlhx5Kt11HZP2i/4/1Qe1SHZO2i/4/wBUHtVKCmE+X0vLBkr3XQdk3aH/AI/1Qe1ZHZL2h8GB6oPaqaFIJ8vpeWDJVuuQ7JW0HgwPVB7VIdknaD6B6o32qnBTCny+l5TJXuuA7I+v/QPVG+1SHZG14+I+qtVQCkEwaXlMle63jsia99B9Vasjsh679B9VaqkFMJg0vKZK91sHZB1z6F6q1SHZA1z6F6q1VQKQUwaXlTJXutQ2/wBb+heqtUht7rf0L1VqqoUwmDT8pkr3Wkbea19C9WapDbrWj4n6s1VcKYTBp+UyV7rONudZ+h+rNWRtxrH0P1ZqrIUwpg0/KZK91lG22sfQ/VmqQ211f6J6s1VsKQTBp+UyV7rINtNX+ierNUhtlq30T1dqrgUwpg0/KZK91hG2Oq/RfV2qQ2v1X6L6u1V4KYTBp+UyV7rANrtU+i+rtUhtbqn0X1dq4AUgmDT2Mle7vjavUz4r6u1ZG1WpfRvV2rhBTCYNPYyV7u4NqNS+jfcNUhtPqP0b7hq4YUwph09jJXu7Y2m1H6P9w1ZG0mofR/uGrjBSCYdPYyV7uyNo9Q+j/cNUhtDnnxf7hq44Uwph09jLXu642gzvo/3DVka9nf7H3LVygpBMOnsZa93VGu5v+x9y32KQ1zM/2PuW+xcsKQUw6exlr3dQa1mf7P3LfYsjWcv/AGfuW+xc0KYTDp7GWvd0Rq+X/s/ct9ikNWyv9n7lvsXOCmEw6exlr3dAarlf7P3TfYpDU8n/AGvum+xaAUwmHT2Mte7dGpZB+S+6b7FIahkH5L7pvsWkFMKYdPYy17ulJlNgMDZ87GiknoRsdC23uIvdb4TXeC0tR12DT8bJkGZBPLjFgkgjhZvtLyA3eH+W7vj3lztrsc6npTdNiGGZJmxB0mTKG9rDdB6UAEOLh/lAI49+lXDgv0jZPP0+V+NM4Zccjc1kwdLmb0jSXygkuEg5E2QeFcF581futaP4d2Kf23ur3ZJlkk2sJe82Mdg4cAOfIBFDsj/Gx37hn/yiTHOSJ5Q+k7QdjXR9rNU/Sedk5sc3RRxbsL2htNbw5tPHiVyx2ENmx/rtU+9Z/avosJ6no/lavS1unX1Ii0STRTM3mHzfuI7OePap96z+1Z7ieznjup/es/tX0e0tX5jV8yY6dnznuKbOj/W6n94z+1Z7i+z1AHO1MgcgZW8P/avotpafMavmMdOz513F9nh/rdS+8Z/asjsM7Pj/AFupfeM/tX0S0tPmNXzGOnZ887jWz/jupfeM/tWe45oHjuo/eM/tX0K0tPmNXzGOnZ8/b2H9CYQ5udqQI5EStB/KsnsQaE5xc7O1JxPMmRpJ/wDar/aWmfV8xjp2UDuQaD45qHps/tWe5DoPjmoem3+1X60tM+r5jHTsoXci0LxzUPTb/as9yTQvG8/02/2q+Wlpn1fMY6dlE7kuh+N5/pt/tWe5Ponjef6bf7VerS1M+p5jHTso3co0TxvP9Nv9qz3KtF8bzvTb/arxaWmfU8xjp2UjuV6KP9Xnem3+1ZHYt0bxvO9Jv9qu1paZ9TzGOnZSu5do3jed6TfYs9zDR/G830m+xXS0tM+p5jHTspncx0fxvN9JvsWe5npHjWb6TfYrlaWmfU8xjp2U3uaaR41mek32LPc10nxrM9JvsVxtLTPqbmOnZT+5vpPjWZ6TfYs9zjSvGsz0m+xW+0tM+puY6NlR7nWlD/VZfpN9iz3O9L8ay/O32K22lpn1NzHRsqfc90zxrK87fYs9z7TPGcrzt9itdpaZtTcx0bKr7gNN8ZyvO32LPuB03xnK87fYrTaWmbU3MdGyr+4PTvGcnzt9iz7hNO8ZyfOPYrPaWmbU3MdGys+4bT/Gcnzj2LPuHwPGcjzj2Ky2lpm1NzHRsrfuJwPGcj+nsWfcVg+M5H9PYrHaWmbU3MdGyu+4zB8YyP6exZ9xuF4xP/RWG0tTNqbmOjZX/cdheMT/ANFn3IYfjE/9F37S0zam5jo2cH3I4fjE39Fn3J4njE39F3bS0zam5jo2cP3KYvjE39Fn3LYvjE39F27S0zV7mOjZxfcvjfLy+YLPuZxvl5fMF2bS0zV7mOjZx/c1j/Ly+YLPucx/l5fMF17S0y17mOjZyfc7B8vJ5gs+5+D5eTzBdW0tMte5jo2cv9AQ/LyeYLP6Bh+Xf5gunaWmWvcx0bOb+g4h/wCu/wAwT9CRfLP8wXStLTLXuY6Nlfl2K0WeV8s2HjSSPO8578dpLj4SVhuw+hMcHNwMUOBsEY7eBVhtLWeOrdeCl8E7JkfRbYyRgk7sLBZ76KfZQ+O0/wC6Yizdqz7lEer9jfytU7Xkw8Psb+VqlakDgZu3OiYGq5umyOzZMrBjEuS3HwpJREwgEOJaKqiF3MLMx9SxIsvCmZkY8zQ+OSI7wc3whfK49VwtC7L22OZqTzHDJgwtiBY4mZwYzqtocSfAuJomk6hokGy2LtSybF2ekdlTzwuLwyOR1mNsu7yNcQD3ye+kTyie+q1Rae9ofduN1RvwUlHj1Tw58OSofY9x9Q1DsfSYmsHI6Kd80eM6YuEna54MJJ4/V81KlbNP2i1nWBo73T9Nslj5Ld4vNT5BJbDfHjQAIvvhWeU2+/ffikeF/s+4kOABLXAHkSOaU4X1XcOfDkvh2hHMfmbLM0l+a/WHxZI19rzJdf7u9wBv4Pz1Sxstn5Mub2PcQz5TsjHly2Z0bt+2GjQfff8ABasRebJM2i77lxq6NeGkp3Hqu4c+BXy3skv1LSdoNO1LTW5Uh1DEm0tzIiS1kj63HkchV8/mXB0qHVYdUzsDMxtQz8fZjAy4gxsr2Oyy9x3KcO/uOPEWR3u8s3/9/Hcfy1bv69z/AA+4EOFW1wvlYPFRmkbjwvmnPRxMaXOe8EBoAsk/UAvg+m5uU/UJZMJ0UMOZs7kyT42AydsccoYS1ri9zrkb4RX1L00jT8mRmzsDIsp7tS2fyxlsc556aQA7gdZ52BXLvJPLv6+xFr9+nu+3YOdi6lhRZmDOzIxZhcc0Ztr+NcD3+Khp+p4WrY7sjTsqLLhbIYi+E7wDxzb9YVH7EcOmN2FxsdkbG5u6WahGd4PB3nAB4PLhfJUDTcSbTdhtYxMOGXG1eLVHNz44w8Ttwt9u9Q/Z5WRxWpi1Ux34sxeYie/CX6CNtNOBB+cUuHnbW6Tg6lLpxdlZOZDH0s0OFivyHQs8L9wHd+1VbYh7vdptENIdK7ZYMh7XJLjH0+6N/oy75ruu/S1tns2HY/a7ayPX3uxTnZPbmLlSRuLMiOuTXAG3C/g81Nvp37/Re+/6fSMPKi1DDhy8RxlgmYJGPDTxaRY+pewDnCw1xHzAlfF9pMs5Wq7R5OrDNgbJpkcmz0bmyRkPIvqNb/6m9Vg8eJXPzcfV8vJ112sty3Z8GzMU1Fzxu5AaOtQNb/h+e0mevfX2PTvp7vto1PCdqbtNblRHObF0zscHriP9qvB8689I1fF13Edk4IyDG2Qxnpcd8Tt4VfBwBrjzXyfQ8bGHZF0jUNbiIOZoeM+KecPHS5VNHP8AbvvFczTRnZmjbHQ5bs2RsuvZLJw58lujscHG73f+1bc+Hvxsl/28Xfhd96p3Hqu4c+HJKdYG66zyFc18KwcnNwWabJPLlRadgbV5EbnPL92KChugnnuXfPhzXjHkavkaflSdLO3R37TznMklZK9rYSBub7Wlrujs94gKRzi/fT3/AAvXv19vy+2anq2PpIgGSzJfJO8siix8d8sjiBZ6rRdULteei6/pu0OJJk6bOZGRyOila9hY+N45tc08QfrVEwciXQ9jdoZdJ2mxMsxzCSCRuJK6DDL+bGgmRzm9+hdK07G7PYOz+i/+JLNkSZru2sjJn+HK9wskihQ8AoedI6999Tvv8LJaWoWloJ2lqFpaCdpahaWgnaWoWloJ2lqFpaCdpahaWgnaWoWloJ2lqFpaCdpahaWgnaWoWloJ2lqFpaCdpahaWgnaWoWloJ2lqFpaCdpahaWgnaWoWloJ2lqFpaCdpahaWgnaWoWloPhnZQ+Os37piJ2T/jpN+6YiD7e0/g38oWbULo/Y38oS1IHMl2v0HFyn4cuv4UWQxxY6F2QA5rgaIr610MPOizI5JcWRzmxyvge4Atp7TTh5wuXOzIO2OFO1svQs06eMyC90OMkZDb8JAPD5lWWaVqWNmvzdPhyo86fUdRaXOc8M3C2QxEtPVDS/dIdXM81el/r/AGtu/s+gneBtwNnj1hzXL0fZ7T9Cdmy4MMofmzmfIllkMjnvPznvfMq7sNj5sDsg5D5mh0MXSQyY2RHU3HedvTOO848judXgFp5+Pml2uFmPqrtfdJN2rPEJRF0G71Gtd/hjqcKPW3/OnhKRzfQyZDTDvmuTTfD7FnelIIuQgcxZXzuDTpczMhx8fH1SLQZNTiLYpjNG4M7Xf0l73XDC/dHHgTy7y1HaXqc+mzmaHUzLi6VN2oN+UETNnk6Pkes8N3au7BHNW3f2usRfv1fTQXAEjeA5Ei6UJZ2QdEJpNzpXiOIPNbzjZAb4TwPmVBZj62/bOSeaSSKXtkmFwxsh7HQdHwaXh3Qtbd2CN6x31ofovGn0zTH5mma1LnY+VDJqjpI8k7zuuHuaAev1iDcd9UhI6I+nRZPbLC+KYyta4tJa4miDR8xBU7eWkkuIPM8aK+Z5uPqbo5RmY+rys6PO7R6ASksyTO4xOdu8QN3d3S7qALfZp+pDVTqEzMx2WzVcZm8HvLOhMLBKQ34O5vXZqrUjn36279FnlMx9V5dks6dmO6YGZzC9sZdbi0EAkDwAkecL0LntADi8AcQCSKVM2mwJpNo8TNxsad2UNOyIcaeMPLY8i2mPerqt/wA3Fw3fD3lLZGOWLLyHQY+oY+CcWDfZnNkDjldbpCBJx5btkdU8K76Rz7+vf3hJ5d993WZ+q4vTwRnJ35Jp3YzN233I0FzmEjkQGnmthuQ0ZBx2SkTboeY2kh1EkA19YI+xfO8fZ+GTWBiv0/NYx2vTTZD/AH5rHQujl3HB91XWo7pviAe8vJmNqztK6HKdqsTBgRRl3Qyy2RkydVzW9c2zdDi3rbpBSPDvZZi1++tn0wOe0kAvaTzAsLAc4AAF1DiKJVQw8PKyNhcrGjhlxMi5HQbplaSQ62uAkPSNBr4LjY+1amK3XcvMxnvhy4W6hK7UXCQuAxiwODYXfs3cbq753uHApPff8oumZnR6diuycqV8UJc1pdTjZc4NHAc7LgPtWxcu8W9fe7442vksGn6s7Qc5uWM10zosc5cMWJk7zpRkML3hzy4PdQcfehVcfAFuZWFOcB4ibqUem9vyyYmNkYuVKHR9G0U9sZEzBv7xbfDnfeTor6MNQg7ZEAym9PvGMM3+tvBocW18zSDXgXuXP4OJf8xJP9F81h0/Kl1LTcnUtNzhHj5x3WF00vQh2LEG8W8S3pAQXcrBvvrc2LxtVh1J8moyTNyDA4ZjHY2Q1r5t8dbpJHGNx510Yqj3hStknwuvceSJ5XsjlMksR3XgOJLSQDXmIP2qVmzzvvqg67j5s2XqAldnNx+32OjZ2tkTwytGOwURCQ8N3rILeG8OK6mWM1ux2mPbi5MM0EuNLNBG58sjWNeC8cOs/h3uJPLipHt+SVqAceTSfqC82TxyyyxRyNfJEQJGNNllixfgscV8/wC0czV8yV2Tj6m3Fc/U5WMeZYubozDYBBHIlo+b61qR6Xll+U+TE1Nmo5jdNe6eMSguYOiE3WHVa4EGxwNX3rViL2Wz6ed4GiCD9S855o8aHpp3iKK2t338BZIAF/OSB9ZVJ1GLK0/NzMDDxdQMUs+A7F6Fsr2MjY9vSW/iG8jYJsjwhcPUNKyMo5zX4Wfnt6XppJXw5UUoAyGHdLSdyXqB1dGOAb4SpHOxZ9WpwBJaaBoml4OzIW57cFzyMh8JnazdPFgIaTfLmQqDj4uqe6wSsdPAwTtOJvYuS4HF6Omsc4uEbRd2HjfB8KxoHSafqDNSl0vV5MqPSiM9zseVzpckyt3gze4O756nCuSsc+/RJfRbS1C+CWoJ2lqFpaCdpahaWgnaWoWloJ2lqFpaCdpahaWgnaWoWloJ2lqFpaCdpahaWgnaWoWloJ2lqFpaCdpahaWgnaWoWloJ2lqFpaCdpahaWgnaWoWloPiPZO+Ocv7liJ2TfjlL+5YiD7YTTvsb+ULFrDzT/wCVv5Qo2pAnaWoWlqidpahaWgnaWoWloJ2lqFpaCdpahaWgnaWoWloJ2lqFpaCdpahaWgnaWoWloJ2lqFpaCdpahaWgnaWoWloJ2lqFpaCdpahaWgnaWoWloJ2lqFpaCdpahaWgnaWoWloJ2lqFpaCdpahaWgnaWoWloJ2lqFpaCdpahaWgnaWoWloJ2lqFpaCdpahaWgnaWoWloJ2lqFpaCdpahaWgnaWoWloPi3ZM+OMn7ln4InZL+OEn7ln4Ig+0yn3z+Vv5Qo2kx99/lb+UKFpAnaWoWloJ2lqFpaCdqEsohic82a5Ad894JaxjN7YzwT/hY5BPzvPIfYOP2hdf4rXjQ0prlqinimyGbhTxaY+aB7nZ8bd9w3juv75aBy+r6lztC1pmq4weDTu+FV9C2213N7J8ukTyMOD080QhEbRuBgdRBq74Dme+t3UITs3tbvxisLPJkj8DXf5mrzvgNeujUw6s/wCUXhy6kRVF46LnaWvGKUSRteDwIU7XsuBO0tQtLQTtLULS0E7S1C0tBO0tQtLQTtLULS0E7S1C0tBO0tQtLQTtLULS0E7S1C0tBO0tQtLQTtLULS0E7S1C0tBO0tQtLQTtLULS0E7S1C0tBO0tQtLQTtLULS0E7S1C0tBO0tQtLQTtLULS0E7S1C0tBO0tQtLQTtLULS0E7S1C0tBO0tQtLQfGuyV8b3/uGfgidkn43P8A3Ef4IiPs2Qam/lb+ULztSyTU38rPyheVqQqdpahaWqJ2lqFpaDE83RRFwG840GtH+Zx4AedbmPEMXGZDvAvHWe79p55lc+D3/MdKf8PH6rfnkPM/YP6lbVr5D9b/AFCKtbDT4U/27/w+l+28qBoeC2Ps3a47huxQOmA8G+GAf/srptLpX6a0SWCOu2ovfcd3gcO99o4LSxdLbBtfqerCaEuycSCExA9dm4XW4jwGx5l2Q7dIIPELz9X4+clNdPSI/pujS/bMSrWyuq9uYIjfwkZ1XA8wQrFapmrwnZ/almZEN3DzzvUOTZP8w+3mrZDMJYmvaeBC+3+F+Ip+I0qdWnq6FdM0VWe9pahaWuwwnacSLANfUoWqL2QYs2HK0zOwGzOfM2bTniIE10req6h4COfzoL6bHMEfYoOniZIyN8rGvkvca5wBdXOh3/sXyTBfrul4Or5mHDkPn0uGLS4nyMc47gkc6SUNo71NI40eXI8lLU87UH5ehZ2XrTJTjz5hjzsLHMhjBiG61wdC0Ek8PgC78KXjxWI6PrlrJscwR9YXzCDaTaV+o6ZBqALm5WJH0uLiMYHxvLCXGVr2E7p4E7rhV1z4Lm6BmaxjZLc0OyG5smhMOLiDGa2OeVrn+9gBnCqBoFp+eknl39fZNu9vd9gtLXzrQtc1jJZjPzdoI3NlmgHRQ4Jlla4g70cnvUYY0/tcS0jmV9CvirMWS6Qe1xcA4EtNOAPI8+PnWePgKqjxnwa7lTwZUrIZtThhdCIWlrmuhjBfvEE2O8Qa4cQVWznZOm6RpuUzWMuKWDT5oXGXHaQJRKwljgWcw0k/y8zxtbnbvwV9PtRfI2Nhe9zWtHEucQAPtVAyde1OKaoNXmnwenLceZuMwTZXBnVaDHuENcXAjqEjkTXHt5M5zg3AfkPmbPqbmP6TH6PcijO+5lH4TaaG73f3lBZBI0uc0OaXN+EAeI+vwKVr55kapq0OZHkY+XjYMOe9+SJchp3ZTv7oYSInkgMa01bD1ufg6DNR1DMzocZmsZEGXLmOiyMVuKwjGjAfRaXMN3utO8S4ceSE8lzu1njV0a8NcFUcjNys7Y7NxJJMk6j2lO/fZBRcGvcwEcN3eO6OAHfsAcFo4T45NcxpWzy5Wectro8l0ZDn4XQ8bpoaG718KHHvJ1F74+AqLJGvYHsc1zXCw5psEfMVSNey3y6liZuPkZLYcnCZ0ML8cFsh6ZhIILSQ4NN8wRS0dN1XWsd+NiDMxsOKHGYIsaeN5MrOiveAERJO9f8A6g+DW74XQfR7S1VtntXmzMAw5OZkS5E0r4ocroGlm8Iw4uaWsaN0G632g3wNrmahquuPxYspgdHLE50ABxg+pWxu33ixyLjXg6vDmrYXy0tUrUsjK0faYSv1PLdH0OM1+/AwskjErhK47rOFA2arn3+C0tQ2p1OxJhZD2Bs0r4hJFTcmMPprGt6JziaBPNnAjie9PEfQGSNkaHMc1zTyLSCCpWvnWnanq+LnYeG3IhxcVjh0cEzXA5LTI/fr3o27vDrsANXYU9N1vW9R1GPFbnyNgnkY4yCFr3wNIkuMkxNaHCm3wdR75VJ5PoJe1r2tLgHOstBPE1zod9ZtUFmPmSbVZEUeqZkMrMnKk/wI3dGwxx7paXMqnURxv4PCjZXWOq5udp+lwwzzY2bK7G7bkbjXutkY5x3d4EXbfnrhfNSOcX+n5J8VotLVAGt6+3UsbHlz4II2v3GvyYy3tqpntdYbE4XutbydHxdfEHh0NGmnx5e1f0nkumOoZbXY82KCP87mgva0breTr43yHgQW++F8aWadRO66hzNclRsXC2i7f1SNxw+2p2Y5lk7alDHtuXeDHdH1DyFAGh37Wlhsg7U2ebNDGNYh6ICJrJXSGIPNbkhADQBZdY4ixY5pHMl9FtLULS0E7S1C0tBO0tQtLQTtLULS0E7S1C0tBO0tQtLQTtLULS0E7S1C0tBO0tQtLQfH+yT8bXfuI/wROyR8bHfuI/wREfZMs+//AMjPyheFr0zD/wCT/Iz8oXhakKnaWoWlqidrxy5zjYcswFljbCnaw4BzS0iwRRCkxMxaFjxesTYYII4Y54nNaOLukHWJ4k8++VMPYCPfI/THtVL1XYjF1CUvZuR/MLH4LndzaH5cekV8rV/xqqqqap1ec+n/ANd2PjIiLWe+kbAZOm9kjL2nfrrJcaV0j2w7/Xdvj4Lu9utvh9Q5K/b7PlY/THtXzrubQ/Lj0nJ3Noflx6RXLr/oGrrzFWpreERH+PSPuzR8TTRyiFx2jxcbUNnsuOaeJhiYZo5C8dR7eI8/JcjY/Vu39OY0nrALi9zaH5YekVZND0FmjM3WuB+pen+mfAVfBUTRNfFE+lrfmXDrasak3s7tpahaWvTcKdoHEciR9ShaWg9N48OJ4cuPJZ6WS76R9+HeK8rS0Hpvu3d3edXgvgm+6iN51Hnx5rztLQepkeeb3H6yo2oWloJ7x8JUJmMyYXQzt6SNwAcx3EEDiEtLQepkeSSXut3M3zXjJBDLK2WSJr5GNcxrnCyGureA+Y0PMs2loMwtbjRMigHRRsaGtazgABwAU991bu86vBfBedpaCe8fCVkvcRRc4i74ledpaD033C+sePPjzTpHAEBzqPMWvO0tB6F7ibLiT85TePhPnXnaWg9A9w5OI+op0j+PXdx58ea87S0HoHuAIDnAHmL5oXuPNzj9ZXnaWg9N41VmvBaxvHwnzqFpaD0D3C6c4Xzo803jVWa8FrztLQTtZ33URvOo8xa87S0E7S1C0tBO0tQtLQTtLULS0E7S1C0tBO0tQtLQTtLULS0E7S1C0tBO0tQtLQTtLULS0HyTsj/Gt37iP8ETsjfGo/w8f4IiPsOb+s/yM/KFrrYzf1n+Rn5QtdSFEXIdtRorM2fDdnNGRBPHjSM3H22R/wAEcuN1zHDwrezs/F07CyMzLnbHj47S+V/wtwD5hZV6XOtmyi8454ZYo5GSsLJACw7w6wK9LB5EFAReWTkMxcd80jZHMbzEUTpHfY1oJP2Bc/C2i07UJ4osd2R76x72vlx3xNIaQDxeBfE96++g6qLG83e3d4b1Xu3x8yFzQaLmg1dEi6QZRR6Rm7vb7a7x3hxXNn1/BiwY8qMyTsmMbYWxN4yl97obvEC6BuyK79IOoi8ociOaCOXjH0jA8Nk6rgPnB5KL8zHjmgifMwPyCWxC73iBZ4/UEHuiiZGBjnhwc1oJO71uX1fgjJGPALXCyA6jwIB8I5hBJFjfbx6zeHPiOC8nZeOzJhx3StEszXOjb+0G1ZB+0IPZFix4R51qz6ljwZOPA4uc6fe3DG3fHVq7r6x/0g20WuM2E6icEEmYRdMaogDe3a+uyvYSMN09hrn1hwQSRa2bnQYGG7Kmdcba+DRJsgcOPHmthxax1Oc0G6FmrQZRREjC0uEjKHM7wpZ3m2BvNs8hfEoMovDIzMfFjEk0zGMMjYwbvrONAcPnKx27D+kDhWRKIhNZqiC4tFHw2EGwiwHNLi0OBcOYB4hLAFkivrQZRR32dXrt63wesOP1eFePbsP6RGCCTMYjNwogAODePz2Qg2EXhBmY+VG6SGZjmtkdGTddZpII4/OCvexdWEBFjfbdb7bq6sXS0M/WMfBDAWSzSySiKOKJot7i0u6pcWtIABJN96ufBB0EWGkOrwkXVrSxdXwszI6HHl3yY+kDuTSN8srjxu2nhSDeRYDmlxaHAuHMXxC1cnUsfFxO2ZC/c3wwDdpxJeGcjXfPm4oNtFjfYC4b7eqaPWHD61ry5+LDOIHy++uj6UMa0uJbYbYoceJCDZReOPlQZTHvgla9rHujcR3nNNOH2Fepc0AEuaAeRJpBlFpRanFPq0+nxRSvfA0GWUbu4xxFhp629dEHgK+dYxNXws6foseXf96EodyaQXObQvjdtPCkG8i845o5Wtc11b10HAtPA1yPHvKdjwjzoMotbMzoMGB00pJDXMaQ2iQXODRwvlZC9y9gAO+2jyO8OP1eFBJFrQ5+LPK+Nko6SNjZHteC0ta66JBquR+pbIIIBBBB5EICIiAiIgIiICIiAiIg+TdkX41H+Hj/AAROyL8aj/Dx/giI+w5v6z/Iz8oWutjN/Wf5GflC11IV8/1DYjU8rWps+GaCMyaq3JJ6TiYQ1vzfCDm2B/VajOx/qj9PyMV7MCF50+bGdMyTeObK5+82SQbvAjnZs2edL6WiW5W78LLfnfvxu+eZexGXmZeLLNp8AxWYjYDh42ayHoXtdZe1/REdbmaDTfMlW/Q8B+BBlMfhY2KZMl8oGPK5/SXXXcXf5z364LqItXZsyDRBVYGzJnx8CHOx8XIZjwZLHMk67d+RwLSARR+vvKyPeyKN0kj2sY0W5zjQA+crAljL2sEjC5zd5rd4WR4R8ylluqWkbMZmFrcGZlsdM9gae2G5gG772GlhYY95wsH/ADgcQa4L11XZ/LzJNTazDxXvyXB8ec6UNla0bvvPFhIad0iwa63EHiDa1h7mxsc97g1rRZc40AEnmRyVbRdlmYmY2bIwY2MbC9jGSztyDG9zhZbTGtAIvgAOZ8JWpp+x8jIMaHL07TRHDJjF0bQ17XiNrw51btW7eBrzlXKKWOeISwyMkjdyexwcD9oUIcqGeSWOJ5c6I7r6aaB8F1R+xPQuqWPs3qOJgvgbgadPkux2Rx5kr2l0IDA0xgOYerwdRFDrcQe/5wbI5ceLGztXF6mVK+JksrXmBr4dzea4RgbzX9ag0cuBB4q7ogq2hbOz6bpmoY78cxTZEAj3jltkZI7dcN6mxt3bviTvE988FLQdBytN1p2TJjY0cZiLXy74kkkd1eR3Q5rer8Elw5VSsnSx7+50jN663d4XdXXm4qaeopj9kJmYeMIMbD6VsbxlNLt0ZPvzXhj3AWQQHDjYF8iLW1g7OzY2bp+VJgYJEUuQ7oQ5p7UZIWlojO7x3d08AG/CNUrSiRNiebgZmi5T9QyZsV2O2JzXZETZBvAZRZuBxbyquJ+tcHF2O1KKQFscMEIlbIIzkNeW0Wbx6kbW8d3vDwK+okcieas7PaFk6Zn9LLhYePu4nQPmgfvPyH74d0j+qDZ58STx5rl4myGc7U2T52PimF0jH5LOkYWTOa/eLgxsbQBRPBxcfnV6RL87kqRLslljpI2YOnzMc6oHySAdptEzn9Qbpq2kDq1W7XEUu3qmm5M+tduxYOHmAwhkTslwvFeC477Wlpu7F1R6o4ruIgo+l7HZDNRilz8TFOJvtklgfIyRrnhkjS7dbG1tEubwIPAcSaXtibHzQ4pL48Vua2eB0WQDboo2ABwa6rHC+A58lckQUmLZTK7Qkjfp2nN3RjDtYPDo8l8T950jiW8HOFjiCfCSt3XtAyNTzS+PAw3tkw247JpJQHYjw8nfYN02QCKotPBWlFbisaHs9Ppuuz5mQx8jnGU9sjKaRIHOsB0e4HWBw4vNVw5rUytntTyYG4kmJjyY0JyCHNzOjdMJJN4D4B3KHA3vA+Cirkigo0OyWcM3EnyYInNYG0zEyGY4xiJHO4DoyDYIvcLLIPh4dPZ/QsnTNRE02FhwbmKYHzwv3n5L98O6R3VB40eZJF81ZkS4pE+yeTeQ2LStMkY9+SGFzwyjI7eZNW58Ng6tc/ARyXe1XSMjKx8QY8ze2Y2HHlmfwLontDZD854B1eELsonSx1uo2XsbkS5eoFkG82bpBDN241rWscKEbmdHvEAcPh1wBA4LqybLwyauyV2DgvwmZbZWRPjaQ1ghLKDSKHWo181qyIg4OzmmvxZMuWYOLWyHGxd9m64QNcSOfeLnO+wBceHYxwx8yN+nac1/a0kWNI2uMhlc9slbvUIBAviRXNXZEFX0PZ7I07XZ8zIje9znSntkZbSHhzrAMe4HcBw4vIFcFpSbLZxlcXYmDO58rHxzySdbGAndIdwFp4uDhyI4ijauqJt6G6j6fsZLFLEMvGbKGzxunkky2yMyGtJJJjEYNm76znHiRa9cjZPI95EOHgu3BLGxxcGmBpnEjC3qngGgtoVV+BXNFYm1pJ5qflaDkwwuhxsaGCSbUZd2bHaDvwzE75eABu02hxJ4tHzBb20GhyZzcZuNhYmTDFBJA2DIfuNiLg0NkbwItu7XK+PAhWJFm3Ky353cfR9EZp2dm5MsWO+afox2wIx0kgEbWuLjV8XAmrN3a4OPsY9uJlRyafpzJW43RY0jK/xOke8SfBG4ac0XxPDmrsiqeioads3m4mtYuU7GxajJMk7pBIa69BjS3eYetxIdR42O+vfUNnMrJ1HKnhdE2KR1sBdRIkG7PfDh1Q2vDXeVoRBRJdj8zpcx3azcmSSQvbK/Na1kzDKH7jmdHvfBFcXOArgKK9HbKZnTRSxafgx3O6RsJla6HEaXMJa1m5RPVLt5m4QT3xau6J0sKNPshmOgaxuNiOecfGbJI2VrHuMW8HR7xYeq4EcSCOqLB4VY9EwsnTcPGw+12xYzI3lwfk9K9jy+w0ENa0tonkBXAALrIrcERFAREQEREBERAREQfJuyL8aj/Dx/gidkX41H+Hj/AAREfYc39Z/kZ+ULXWxm/rP8jPyha6kKIiKgiIg1NUhfkaXlQxs33vjLQ3w/MuPPpOZjzCSF0k1NewGM7rmw7zCGA+Gt7j+CsaKxNkmLq/Dh6i7thzTlR7sL+1Gyym2kk7u9xNn677y2tKingw8jpu3Hk8WxzR04cOIbb3Xf1rrIkzeLEU2m6u42Hm9qsfuZcUkUeO2OMyFoBBp/VBo8PCoO07Kxoi2GLNO8ZiwRTu6sheSxzrd8Gvs8IKsqK8c3TghwJMDOfjgl+V0znzueWzuHIO6OuPAXVDzrwysXUxC+GNuYRZfHI17nuDtxvD4beG9vcTYFclZkTiOFW5sPUG5M8mPHktmlp5kEnUPvNVzoHe+bwKceDlzSyAtz4sQMkMMck7g8OptbxDr57xHH2KwonEcLyxuk7Vh6a+l6Nu/fPeoX/VeqIsy1HIREQEREBERAREQEREBERAREQEREBERAREQEREBERAREQEREBERAREQEREBERAREQEREBERAREQfJuyL8aj/AA8f4InZF+NR/h4/wREfYc39Z/kZ+ULXWxm/rP8AIz8oWupCiIioIiICIiAiIgIiICIiAiIgIiICIiAiIgIiICIiAiIgIiICIiAiIgIiICIiAiIgIiIPaPHfJHvh0bW2QN99WR//AFS7Ud8rB96FJv6lH+8f+DVFZmbLEHajvlYPvQnajvlYPvQuTNqefJqM+LpuDBOMbcE758gx9Zw3g1gDXWaIJuhxHHnXm7avSWF9yZFB26x3a76m64Z72a69OIBrlacUlna7Ud8rB96E7Ud8rB96FXcra7GhdF0eLlOY+Odz3Pge3oXRbtteACW/C4+DhztbMe1OlySNYH5LQ9xYx78d7WSEPDCGkjjTiAfOl5LQ7PajvlYPvQnajvlYPvQuNnbV6Pp0r4snJc17HuY8Bh6m7VuP/wBo3hxXZBBAINg8QU4p8SzxywzCxJsqeeBsMLC95D7oAWeC1p8tkEEcpZK8SOaxjImbznF3IALx2m+K2q/wr/wUj8LRv4zGVibkvTtjI8k6r6r/ANp0+R5J1X1X/tbrWt3G9UcvAs7rf2R5lOIs0enyPJOq+q/9p0+R5J1X1X/ta8eo5uXnysw8DHfhwT9BLLJOWyOcPhFjQ0ggX3yLo0vEbV6Q6R0QGUZ2yCLoG4rnSOcQ4imjifgu8ycRZvdPkeSdV9V/7Tp8jyTqvqv/AGtVu0mlPjEjendEIulllGM7cgbx/wAU11PgngfAvLB2kw9U1TGxsEhzHCXphIzdewta0t4XyNnzJeSzf6fI8k6r6r/2nT5HknVfVf8Atb2639keZZDW2OqOfgTiLOZgajj6kyZ2P0g6GZ0EjZGbpa9tWK+1barGw/DT9SrylN+DVZ1pHybsi/Go/wAPH+CJ2RfjUf4eP8ERH2HN/Wf5GflC11sZv6z/ACM/KFrqQoiIqCIiAiIgIiICIiAiIgIiICIiAiIgIiICIiAiIgIiICIiAiIgIiICIiAiIgIiICIiDaZ+pR/vH/g1Yo+A+ZcnO0yLUHMM0+YwMBDWw5Lo28eZod/l5lq+5vD8Z1L16T2rMxdbtzI0Z8mfJl4uo5mE+YNE7YWsc2Xd4A9dp3TXC20arwLVZsnjNyIpHZWXIyB+/jRO3agHSCQtB3bIJaPhEkAUKUfc3h+M6l69J7U9zeH4zqXr0ntSIsXu9MzZbHzA/wD8rKhMj5nPLA07wlDQ5vFp4dUUeY8KzPsvjT4uPD0+Sw45kdFI3dsOc8SXxFGnAUOXhteXubw/GdS9ek9qe5vD8Z1L16T2paS6B2QiM3bJz8l2W57nSTy48Em/vbtjddGWji0UQAVYgCABR8y4Hubw/GdS9ek9qe5vD8Z1L16T2pw9C7a2nB9y2q8D+qv73zKR+Fo38ZjLRk2YwJY3RyT6i5jgWua7NkIIP2reztNxtRwe08gSdCC0jo5Cxw3eXWHFWIsTLoNkj3G++M5ftBZ6SP5RnpBVT3B6B8jletye1Z9wegfI5XrcntU4S7qP0dnbc8sGq5OPBkSdJPjRuj3HuPwiCQXNvv7pC1NN2VwNNzYMpmdJI+ANDAWxMFNDgAdxos082TxNDitb3B6B8jletye1PcHoHyOV63J7U4S7cGzOI2OWBmozsxshjo8mG4yJgS7iSW20jeItpHIL207RIMCeGY5/TPhDwz3qCIU4AGxG1t8uZ4rm+4PQPkcr1uT2p7g9A+RyvW5Pali61dJH8oz0ggkjse+M5/tBVX3B6B8jletye1Y9wegfI5XrcntThLo7Ef8A0/Uv/wAlN+DVZ1o6VpGHo2K7GwmPbG55kIfIXkuIAJs/UFvLSPk3ZF+NR/h4/wAETsi/Go/w8f4IiLNk9lLTZZ3OGnZdABotzeNCv/heXdO07yflem1EUU7p2neT8r02p3TtO8n5XptREDunad5PyvTandO07yflem1EQO6dp3k/K9Nqd07TvJ+V6bURA7p2neT8r02p3TtO8n5XptREDunad5PyvTandO07yflem1EQO6dp3k/K9Nqd07TvJ+V6bURA7p2neT8r02p3TtO8n5XptREDunad5PyvTandO07yflem1EQO6dp3k/K9Nqd07TvJ+V6bURA7p2neT8r02p3TtO8n5XptREDunad5PyvTandO07yflem1EQO6dp3k/K9Nqd07TvJ+V6bURA7p2neT8r02p3TtO8n5XptREDunad5PyvTandO07yflem1EQO6dp3k/K9Nqd07TvJ+V6bURA7p2neT8r02p3TtO8n5XptREDunad5PyvTandO07yflem1EQO6dp3k/K9Nqd07TvJ+V6bURA7p2neT8r02p3TtO8n5XptREDunad5PyvTandO07yflem1EQO6dp3k/K9Nqd07TvJ+V6bURA7p2neT8r02p3TtO8n5XptREDunad5PyvTandO07yflem1EQO6dp3k/K9Nqd07TvJ+V6bURA7p2neT8r02p3TtO8n5XptREDunad5PyvTandO07yflem1EQO6dp3k/K9Nqd07TvJ+V6bURA7p2neT8r02p3TtO8n5XptREDunad5PyvTandO07yflem1EQO6dp3k/K9Nqd07TvJ+V6bURBS9qNah17WO3YYpImmJrN19E8PqREVH//2Q=="

    fun convertImageFileToBase64(imageFile: File): String {
        return FileInputStream(imageFile).use { inputStream ->
            ByteArrayOutputStream().use { outputStream ->
                Base64OutputStream(outputStream, Base64.DEFAULT).use { base64FilterStream ->
                    inputStream.copyTo(base64FilterStream)
                    base64FilterStream.close()
                    outputStream.toString()
                }
            }
        }
    }

}