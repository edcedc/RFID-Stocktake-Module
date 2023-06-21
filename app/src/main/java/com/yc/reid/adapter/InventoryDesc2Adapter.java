package com.yc.reid.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.LogUtils;
import com.yc.reid.R;
import com.yc.reid.bean.DataBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @Author nike
 * @Date 2023/6/9 15:03
 * @Description
 */
public class InventoryDesc2Adapter  extends BaseAdapter {

    private Context act;
    private JSONArray jsonArray;

    public InventoryDesc2Adapter(Context act, JSONArray jsonArray) {
        this.act = act;
        this.jsonArray = jsonArray;
    }

    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public Object getItem(int i) {
        return jsonArray.optJSONObject(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = convertView.inflate(act, R.layout.i_inventory_desc_text2, null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        try {
            JSONObject object = jsonArray.getJSONObject(i);

            viewHolder.tv_text.setText(object.optString("title") + "：" + object.optString("text"));
            if (object.optString("title").equals("InventoryStatus")){
                if (object.optInt("text") == 1){
                    viewHolder.tv_text.setText(act.getText(R.string.in_stock));
                }else {
                    viewHolder.tv_text.setText(act.getText(R.string.not_library));
                }
            }
            if (object.optString("title").equals("FoundStatus")){
                if (object.optInt("text") == 116){
                    viewHolder.tv_text.setText("Scan status：RFID");
                }else if (object.optInt("text") == 117){
                    viewHolder.tv_text.setText("Scan status：QRCode");
                }else if (object.optInt("text") == 118){
                    viewHolder.tv_text.setText("Scan status：Manually");
                }else {
                    viewHolder.tv_text.setText("Scan status：");
                }
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return convertView;
    }

    class ViewHolder{

        AppCompatTextView tv_text;

        public ViewHolder(View convertView) {
            tv_text = convertView.findViewById(R.id.tv_text);
        }

    }

}
