package com.bj.eduteacher.media;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bj.eduteacher.R;
import com.hpplay.bean.CastDeviceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zz379 on 2017/11/22.
 */

public class LeboDevicesAdapter extends BaseAdapter {

    private List<CastDeviceInfo> mDataList = new ArrayList();
    private Context mContext;
    private int currSelectPosition = -1;
    private int currState = -1;

    public LeboDevicesAdapter(Context context, List<CastDeviceInfo> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    public void LeboDevicesAdapter(Context context, List<CastDeviceInfo> dataList,
                                   int position, int state) {
        this.mContext = context;
        this.mDataList = dataList;
        this.currSelectPosition = position;
        this.currState = state;
    }


    /**
     * 更新连接状态
     *
     * @param state
     */
    public void updateConnectState(int state) {
        try {
            this.currState = state;
            this.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 选择设备
     *
     * @param position
     */
    public void updateSelectPosition(int position) {
        try {
            this.currState = -1;
            this.currSelectPosition = position;
            this.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getCount() {
        return this.mDataList.size();
    }

    public Object getItem(int position) {
        return this.mDataList.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.dialog_lebo_list_item, (ViewGroup) null);
                holder = new ViewHolder();
                holder.tvName = (TextView) convertView.findViewById(R.id.item_textview);
                holder.progressBar = (ProgressBar) convertView.findViewById(R.id.item_progresbar);
                holder.ivIcon = (ImageView) convertView.findViewById(R.id.item_imageview);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvName.setText(mDataList.get(position).getHpplayLinkName());
            if (this.currSelectPosition == position) {
                if (this.currState == 0) {  // 连接失败
                    holder.ivIcon.setSelected(false);
                    holder.progressBar.setVisibility(View.GONE);
                    holder.ivIcon.setVisibility(View.VISIBLE);
                } else if (this.currState == 1) {   // 连接成功
                    holder.ivIcon.setSelected(true);
                    holder.progressBar.setVisibility(View.GONE);
                    holder.ivIcon.setVisibility(View.VISIBLE);
                } else if (this.currState == -1) {  // 初始化
                    holder.ivIcon.setVisibility(View.GONE);
                    holder.progressBar.setVisibility(View.VISIBLE);
                } else {    // 隐藏所有
                    holder.ivIcon.setVisibility(View.GONE);
                    holder.progressBar.setVisibility(View.GONE);
                }
            } else {
                holder.ivIcon.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }


    private static class ViewHolder {
        TextView tvName;
        ProgressBar progressBar;
        ImageView ivIcon;

        private ViewHolder() {
        }
    }
}
