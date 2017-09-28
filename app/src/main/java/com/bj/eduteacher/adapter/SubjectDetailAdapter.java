package com.bj.eduteacher.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;
import com.bj.eduteacher.R;
import com.bj.eduteacher.entity.ArticleInfo;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.zzautolayout.utils.AutoUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by zz379 on 2017/1/6.
 * 话题详情页面
 */

public class SubjectDetailAdapter extends BaseRecyclerAdapter<RecyclerView.ViewHolder> {

    private List<ArticleInfo> mDataList;

    public SubjectDetailAdapter(List<ArticleInfo> mDataList) {
        this.mDataList = mDataList;
    }

    @Override
    public int getAdapterItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        RecyclerView.ViewHolder vh;

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_subject_detail, parent, false);
        vh = new ViewHolderStudent(v, true);

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder fholder, int position, boolean isItem) {
        ArticleInfo itemInfo = mDataList.get(position);

        ViewHolderStudent holder = (ViewHolderStudent) fholder;
        holder.ivUserPhoto.setImageURI(itemInfo.getAuthImg());
        String phone = itemInfo.getAuthDesc();
        if (!StringUtils.isEmpty(phone) && phone.length() > 10) {
            phone = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        }
        String name = StringUtils.isEmpty(itemInfo.getAuthor()) ? phone : itemInfo.getAuthor();
        holder.tvCommentName.setText(name);
        holder.tvCommentTime.setText(itemInfo.getPostTime());
        holder.tvCommentContent.setText(itemInfo.getContent());
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(View view) {
        return new ViewHolderStudent(view, false);
    }

    public class ViewHolderStudent extends RecyclerView.ViewHolder {

        private SimpleDraweeView ivUserPhoto;
        private TextView tvCommentName, tvCommentTime, tvCommentContent;

        public ViewHolderStudent(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                AutoUtils.auto(itemView);
                ivUserPhoto = (SimpleDraweeView) itemView.findViewById(R.id.iv_userReplyPhoto1);
                tvCommentName = (TextView) itemView.findViewById(R.id.tv_userReplyName1);
                tvCommentTime = (TextView) itemView.findViewById(R.id.tv_userReplyTime1);
                tvCommentContent = (TextView) itemView.findViewById(R.id.tv_userReplyContent1);
            }
        }
    }

    public void setData(List<ArticleInfo> list) {
        this.mDataList = list;
        notifyDataSetChanged();
    }


    public void insert(ArticleInfo person, int position) {
        insert(mDataList, person, position);
    }

    public void remove(int position) {
        remove(mDataList, position);
    }

    public void clear() {
        clear(mDataList);
    }

    public ArticleInfo getItem(int position) {
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
    }
}
