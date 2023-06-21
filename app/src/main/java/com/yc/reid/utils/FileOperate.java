package com.yc.reid.utils;

import android.content.Context;
import android.widget.Toast;

import java.io.*;

/**
 * Created by Android Studio.
 * User: ${edison}
 * Date: 2020/7/2
 * Time: 14:53
 */
public class FileOperate {
    /**
     * 【读取文件】
     * @param context
     * @param fileName
     * @return
     */
    public String ReadText(Context context, String fileName)
    {
        FileInputStream fIn = null;
        InputStreamReader isr = null;
        char[] inputBuffer = new char[255];
        String data = null;
        try
        {
            fIn =context.openFileInput(fileName);
            isr = new InputStreamReader(fIn);
            isr.read(inputBuffer);
            data = new String(inputBuffer);
            Toast.makeText(context, "read Succeed",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(context, " not read",Toast.LENGTH_SHORT).show();
        }
        finally
        {
            try {
                isr.close();
                fIn.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return data;
    }

    /**
     * 【写入文件】
     * @param context
     * @param fileName
     * @param data
     */

}
