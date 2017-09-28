package com.bj.eduteacher.adapter;


import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;
import com.bj.eduteacher.R;
import com.bj.eduteacher.entity.ClassItemInfo;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.zzautolayout.utils.AutoUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by zz379 on 2017/1/6.
 * 全部学生页面adapter
 */

public class RankListAdapter extends BaseRecyclerAdapter<RecyclerView.ViewHolder> {

    private List<ClassItemInfo> mDataList;

    public RankListAdapter(List<ClassItemInfo> mDataList) {
        this.mDataList = mDataList;
    }

    @Override
    public int getAdapterItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        RecyclerView.ViewHolder vh;

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_student_info_ranklist, parent, false);
        vh = new ViewHolderStudent(v, true);

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder fholder, int position, boolean isItem) {
        ClassItemInfo itemInfo = mDataList.get(position);

        ViewHolderStudent holder = (ViewHolderStudent) fholder;
        holder.tvStudRankNum.setText(String.valueOf(position + 1));

        if (!StringUtils.isEmpty(itemInfo.getStudImg())) {
            holder.imgStudPhoto.setImageURI(Uri.parse(itemInfo.getStudImg()));
        }

        holder.tvStudName.setText(itemInfo.getStudName());
        holder.tvStudGrade.setText("Lv." + itemInfo.getStudGrade());
        holder.tvStudBadge.setText(itemInfo.getStudBadge());
        holder.tvStudBadgePro.setText(itemInfo.getStudBadgePro());
        holder.tvStudScore.setText(itemInfo.getStudScore());
        holder.tvStudClassName.setText(itemInfo.getStudPingyu());
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(View view) {
        return new ViewHolderStudent(view, false);
    }

    public class ViewHolderStudent extends RecyclerView.ViewHolder {

        private TextView tvStudRankNum;
        private SimpleDraweeView imgStudPhoto;
        private TextView tvStudName;
        private TextView tvStudScore, tvStudBadge, tvStudGrade;
        private TextView tvStudBadgePro;
        private TextView tvStudClassName;

        public ViewHolderStudent(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                AutoUtils.auto(itemView);
                tvStudRankNum = (TextView) itemView.findViewById(R.id.tv_studentRankNum);
                imgStudPhoto = (SimpleDraweeView) itemView.findViewById(R.id.img_kidPhoto);
                tvStudName = (TextView) itemView.findViewById(R.id.tv_studentName);
                tvStudScore = (TextView) itemView.findViewById(R.id.tv_studentScore);
                tvStudBadge = (TextView) itemView.findViewById(R.id.tv_studentBadge);
                tvStudGrade = (TextView) itemView.findViewById(R.id.tv_studentGrade);
                tvStudBadgePro = (TextView) itemView.findViewById(R.id.tv_studentBadgePro);
                tvStudClassName = (TextView) itemView.findViewById(R.id.tv_className);
            }
        }
    }

    public void setData(List<ClassItemInfo> list) {
        this.mDataList = list;
        notifyDataSetChanged();
    }


    public void insert(ClassItemInfo person, int position) {
        insert(mDataList, person, position);
    }

    public void remove(int position) {
        remove(mDataList, position);
    }

    public void clear() {
        clear(mDataList);
    }

    public ClassItemInfo getItem(int position) {
        if (position < mDataList.size())
            return mDataList.get(position);
        else
            return null;
    }

    private OnMyItemClickListener myItemClickListener;

    public void setOnMyItemClickListener(OnMyItemClickListener listener) {
        this.myItemClickListener = listener;
    }

    public interface OnMyItemClickListener {
        void onClick(View view, int position);

        void onCommendClick(View view, int position);
    }
}
