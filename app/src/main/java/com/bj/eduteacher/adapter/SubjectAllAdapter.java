package com.bj.eduteacher.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;
import com.bj.eduteacher.R;
import com.bj.eduteacher.entity.ArticleInfo;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.zzautolayout.utils.AutoUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by zz379 on 2017/1/6.
 * 专家全部话题的列表
 */

public class SubjectAllAdapter extends BaseRecyclerAdapter<RecyclerView.ViewHolder> {

    private List<ArticleInfo> mDataList;

    public SubjectAllAdapter(List<ArticleInfo> mDataList) {
        this.mDataList = mDataList;
    }

    @Override
    public int getAdapterItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_zhuanjia_subject, parent, false);
        RecyclerView.ViewHolder vh = new ViewHolderZhuanjia(v, true);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder fholder, int position, boolean isItem) {
        ArticleInfo itemInfo = mDataList.get(position);
        ViewHolderZhuanjia holder = (ViewHolderZhuanjia) fholder;

        holder.tvAllReply.setText("查看全部" + itemInfo.getReplyCount() + "条回复");
        holder.tvSubName.setText(itemInfo.getTitle());
        holder.tvSubContent.setText(itemInfo.getContent());

        List<ArticleInfo> topReplys = itemInfo.getReplyList();
        if (topReplys != null && topReplys.size() >= 1) {
            holder.rlReply1.setVisibility(View.VISIBLE);

            holder.ivUserReplyPhoto1.setImageURI(topReplys.get(0).getAuthImg());
            String phone1 = topReplys.get(0).getAuthDesc();
            if (!StringUtils.isEmpty(phone1) && phone1.length() > 10) {
                phone1 = phone1.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            }
            String name1 = StringUtils.isEmpty(topReplys.get(0).getAuthor()) ?
                    phone1 : topReplys.get(0).getAuthor();
            holder.tvUserReplyName1.setText(name1);
            holder.tvUserReplyTime1.setText(topReplys.get(0).getPostTime());
            holder.tvUserReplyContent1.setText(topReplys.get(0).getContent());
        } else {
            holder.rlReply1.setVisibility(View.GONE);
        }
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(View view) {
        return new ViewHolderZhuanjia(view, false);
    }

    public class ViewHolderZhuanjia extends RecyclerView.ViewHolder {

        TextView tvAnswer;
        TextView tvAllReply;
        TextView tvSubName, tvSubContent;
        RelativeLayout rlReply1;
        TextView tvUserReplyName1;
        TextView tvUserReplyTime1;
        TextView tvUserReplyContent1;
        SimpleDraweeView ivUserReplyPhoto1;
        LinearLayout llSubjectBody;
        TextView tvInvite;

        public ViewHolderZhuanjia(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                AutoUtils.auto(itemView);

                tvAnswer = (TextView) itemView.findViewById(R.id.tv_answer);
                tvAllReply = (TextView) itemView.findViewById(R.id.tv_allReply);

                tvSubName = (TextView) itemView.findViewById(R.id.tv_subjectName);
                tvSubContent = (TextView) itemView.findViewById(R.id.tv_subjectContent);

                rlReply1 = (RelativeLayout) itemView.findViewById(R.id.rl_reply1);

                tvUserReplyName1 = (TextView) itemView.findViewById(R.id.tv_userReplyName1);
                tvUserReplyTime1 = (TextView) itemView.findViewById(R.id.tv_userReplyTime1);
                tvUserReplyContent1 = (TextView) itemView.findViewById(R.id.tv_userReplyContent1);

                ivUserReplyPhoto1 = (SimpleDraweeView) itemView.findViewById(R.id.iv_userReplyPhoto1);

                tvInvite = (TextView) itemView.findViewById(R.id.tv_invite);

                llSubjectBody = (LinearLayout) itemView.findViewById(R.id.ll_subject);
                llSubjectBody.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (myItemClickListener != null) {
                            myItemClickListener.onSubjectClick(v, "ViewReply", getAdapterPosition());
                        }
                    }
                });
                tvAnswer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (myItemClickListener != null) {
                            myItemClickListener.onSubjectClick(v, "Answer", getAdapterPosition());
                        }
                    }
                });
                tvAllReply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (myItemClickListener != null) {
                            myItemClickListener.onSubjectClick(v, "ViewReply", getAdapterPosition());
                        }
                    }
                });
                RxView.clicks(tvInvite)
                        .throttleFirst(1, TimeUnit.SECONDS)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Object>() {
                            @Override
                            public void accept(@NonNull Object o) throws Exception {
                                if (myItemClickListener != null) {
                                    myItemClickListener.onSubjectClick(tvInvite, "Invite", getAdapterPosition());
                                }
                            }
                        });
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
        void onSubjectClick(View view, String tag, int position);
    }
}
