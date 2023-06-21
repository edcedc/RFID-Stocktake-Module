package com.yc.reid.weight;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.yc.reid.R;
import com.yc.reid.utils.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 作者：yc on 2018/10/22.
 * 邮箱：501807647@qq.com
 * 版本：v1.0
 */

public class FileSaveUtils {

    private static NotificationManager notificationManager;
    private static Notification notification; //下载通知进度提示
    private static NotificationCompat.Builder builder;
    private boolean flag = false; //进度框消失标示 之后发送通知

    /**
     *  保存本地指定路径加刷新图库
     * @param act
     * @param imgBitmap
     * @param name
     */
    public static String save(final Context act, final Bitmap imgBitmap, String name){
        boolean orExistsDir = FileUtils.createOrExistsDir(Constants.Companion.getImgUrl());
        String fileName = Constants.Companion.getMainPath() + name + ".png";
        if (imgBitmap != null && !imgBitmap.isRecycled()){
            boolean save = com.blankj.utilcode.util.ImageUtils.save(imgBitmap, fileName, Bitmap.CompressFormat.PNG, true);
            if (save){
                MediaScannerConnection.scanFile(act,
                        new String[]{fileName},
                        new String[]{"image/png"},
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                LogUtils.e(path);
                                Glide.get(act).getBitmapPool().put(imgBitmap);
                                ToastUtils.showShort("下载成功");
                            }
                        });
            }
        }else {
            ToastUtils.showShort("下载成功");
        }
        return fileName;
    }

    /**
     * 给出url，获取视频的第一帧
     *
     * @param url
     * @return
     */
    public static Bitmap getVideoThumbnail(String url) {
        Bitmap bitmap = null;
        //MediaMetadataRetriever 是android中定义好的一个类，提供了统一
        //的接口，用于从输入的媒体文件中取得帧和元数据；
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //根据文件路径获取缩略图
            LogUtils.e(url);
            retriever.setDataSource(url);
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return bitmap;
    }

    public static void saveVideo(final Context act, final String fileUrl, final String name){

    }

    //初始化通知
    private static void initNotification(Context act) {
        notificationManager = (NotificationManager) act.getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(act);
        builder.setContentTitle("正在下载...") //设置通知标题
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(act.getResources(), R.mipmap.ic_launcher)) //设置通知的大图标
                .setDefaults(Notification.DEFAULT_LIGHTS) //设置通知的提醒方式： 呼吸灯
                .setPriority(NotificationCompat.PRIORITY_MAX) //设置通知的优先级：最大
                .setAutoCancel(true)//设置通知被点击一次是否自动取消
                .setContentText("下载进度:" + "0%")
                .setProgress(100, 0, false);
        notification = builder.build();//构建通知对象
    }

//    public static boolean save(File file, String name){
//        boolean orExistsDir = FileUtils.createOrExistsDir(Constants.imgUrl);
//        if (orExistsDir){
//            String fileName = Constants.imgUrl + name + ".png";
//            copy(file, new File(fileName));
//            LogUtils.e(fileName);
//            ToastUtils.showShort("下载成功");
//        }
//        return orExistsDir;
//    }

    /**
     * 复制文件
     * @param source 输入文件
     * @param target 输出文件
     */
    private static void copy(File source, File target) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(source);
            fileOutputStream = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
