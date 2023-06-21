package com.yc.reid.utils

import android.os.Environment

/**
 * Created by yc on 2017/9/30.
 */
 class Constants {

   companion object{

       val ITEMCOUNT = 10
       val pageSize = 20

       val ZFB_PAY = "2018102061789029"
       val BAI_DU = "V5h6NjfSgukQbu55EB0zRQKY83ghiNak"
       val ShareID = "5d22bec94ca357c136000062"
       val WX_APPID = "wx1644830a681606ec"
       val WX_PARTNERID = "1553487061"
       val WX_SECRER = "f5d895afba34897355e2c2a10bd102ca"
       val QQ_APPID = "1106499610"
       val QQ_SECRET = "mB4XpdFO6laSsV4j"
       val WB_APPID = "761285940"
       val WB_SECRET = "83c30696e0218437f471581f73b5985c"

       val mainPath = Environment.getExternalStorageDirectory().toString() + "/么马/"
       val imgUrl = mainPath + "img/"
       val videoUrl = mainPath + "video/"

   }

}
