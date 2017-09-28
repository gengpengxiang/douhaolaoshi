package com.bj.eduteacher.dialog;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bj.eduteacher.R;

import java.util.List;

/**
 * Created by zz379 on 2017/2/7.
 */

public class CommendReasonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CommendReasonInfo> mDataList;

    public CommendReasonAdapter(List<CommendReasonInfo> mDataList) {
        this.mDataList = mDataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_commend_reason, parent, false);
        RecyclerView.ViewHolder vh = new ViewHolderReason(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder fholder, int position) {
        String reasonID = mDataList.get(position).getReasonID();
        String reason = mDataList.get(position).getReasonName();
        ViewHolderReason holder = (ViewHolderReason) fholder;

        if ("-1".equals(reasonID)) {
            holder.tvReasonName.setTextColor(Color.parseColor("#FE5433"));
        } else {
            holder.tvReasonName.setTextColor(Color.parseColor("#6C6C6C"));
        }
        holder.tvReasonName.setText(reason);
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public class ViewHolderReason extends RecyclerView.ViewHolder {

        private TextView tvReasonName;

        public ViewHolderReason(View itemView) {
            super(itemView);
            tvReasonName = (TextView) itemView.findViewById(R.id.tv_reasonName);
            tvReasonName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myItemClickListener != null) {
                        myItemClickListener.onClick(tvReasonName, getAdapterPosition());
                    }
                }
            });
        }
    }

    private OnMyItemClickListener myItemClickListener;

    public void setOnMyItemClickListener(OnMyItemClickListener listener) {
        this.myItemClickListener = listener;
    }

    public interface OnMyItemClickListener {
        void onClick(View view, int position);
    }
}
