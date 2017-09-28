package com.bj.eduteacher.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;
import com.bj.eduteacher.R;
import com.bj.eduteacher.dialog.CommendReasonInfo;
import com.bj.eduteacher.zzautolayout.utils.AutoUtils;

import java.util.List;


/**
 * Created by zz379 on 2017/5/6.
 */

public class ReasonEditAdapter extends BaseRecyclerAdapter<RecyclerView.ViewHolder> {

    private List<CommendReasonInfo> mDataList;

    public ReasonEditAdapter(List<CommendReasonInfo> mDataList) {
        this.mDataList = mDataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_reason, parent, false);
        vh = new ViewHolderReason(v, true);
        return vh;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(View view) {
        return new ViewHolderReason(view, false);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder fholder, int position, boolean isItem) {
        CommendReasonInfo item = mDataList.get(position);
        ViewHolderReason holder = (ViewHolderReason) fholder;
        holder.tvReasonName.setText(item.getReasonName());
    }

    @Override
    public int getAdapterItemCount() {
        return mDataList.size();
    }

    public class ViewHolderReason extends RecyclerView.ViewHolder {
        FrameLayout flContent;
        TextView tvReasonName;
        ImageView ivDelete;

        public ViewHolderReason(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                AutoUtils.auto(itemView);
                tvReasonName = (TextView) itemView.findViewById(R.id.tv_reasonName);
                ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
                flContent = (FrameLayout) itemView.findViewById(R.id.fl_content);
                flContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (myItemClickListener != null) {
                            myItemClickListener.onClick(v, getAdapterPosition());
                        }
                    }
                });
            }
        }
    }

    private OnMyItemClickListener myItemClickListener;

    public void setOnMyItemClickListener(OnMyItemClickListener listener) {
        this.myItemClickListener = listener;
    }

    public interface OnMyItemClickListener {
        void onClick(View view, int position);

        void onDeleteClick(View view, int position);
    }
}
