package com.bj.eduteacher.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
 * 全部学生页面adapter
 */

public class CourseDetailAdapter extends BaseRecyclerAdapter<RecyclerView.ViewHolder> {

    public static final String PAY_STATUS_FREE = "1";
    public static final String PAY_STATUS_UNPAY = "2";
    public static final String PAY_STATUS_PAYED = "3";

    private List<ArticleInfo> mDataList;
    private String payStatus;   // 1 免费，2 未购买，3 已购买

    public CourseDetailAdapter(List<ArticleInfo> mDataList) {
        this.mDataList = mDataList;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    private enum ShowType {ITEM_TYPE_DECORATION, ITEM_TYPE_ZHUANJIA_RES, ITEM_TYPE_ZHUANJIA_BLACKBOARD_TOP, ITEM_TYPE_ZHUANJIA_BLACKBOARD_MORE}

    @Override
    public int getAdapterItemViewType(int position) {
        if (mDataList.get(position).getShowType() == ArticleInfo.SHOW_TYPE_DECORATION) {
            return ShowType.ITEM_TYPE_DECORATION.ordinal();
        } else if (mDataList.get(position).getShowType() == ArticleInfo.SHOW_TYPE_ZHUANJIA_RES) {
            return ShowType.ITEM_TYPE_ZHUANJIA_RES.ordinal();
        } else if (mDataList.get(position).getShowType() == ArticleInfo.SHOW_TYPE_ZHUANJIA_BLACKBOARD_TOP) {
            return ShowType.ITEM_TYPE_ZHUANJIA_BLACKBOARD_TOP.ordinal();
        } else {
            return ShowType.ITEM_TYPE_ZHUANJIA_BLACKBOARD_MORE.ordinal();
        }
    }

    @Override
    public int getAdapterItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        RecyclerView.ViewHolder vh;
        if (viewType == ShowType.ITEM_TYPE_DECORATION.ordinal()) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_line_textview_line_douke, parent, false);
            vh = new ViewHolderDecoration(v, true);
        } else if (viewType == ShowType.ITEM_TYPE_ZHUANJIA_RES.ordinal()) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_course_res, parent, false);
            vh = new ViewHolderZhuanjia(v, true);
        } else if (viewType == ShowType.ITEM_TYPE_ZHUANJIA_BLACKBOARD_TOP.ordinal()) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_course_zhengshu, parent, false);
            vh = new ViewHolderZhuanjiaBlackboardTop(v, true);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_douke_new_detail, parent, false);
            vh = new ViewHolderZhuanjiaBlackboardMore(v, true);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder fholder, int position, boolean isItem) {
        ArticleInfo itemInfo = mDataList.get(position);
        if (fholder instanceof ViewHolderDecoration) {
            ViewHolderDecoration holder = (ViewHolderDecoration) fholder;
            holder.tvTitle.setText(itemInfo.getTitle());
        } else if (fholder instanceof ViewHolderZhuanjia) {
            ViewHolderZhuanjia holder = (ViewHolderZhuanjia) fholder;
            holder.ivPicture.setImageURI(itemInfo.getAuthImg());
            holder.tvName.setText(itemInfo.getTitle());
            holder.tvDesc.setText(itemInfo.getAuthor());
            holder.tvPreviewNum.setText("浏览" + (StringUtils.isEmpty(itemInfo.getReplyCount()) ? "0" : itemInfo.getReplyCount()));
            if ("1".equals(payStatus) || "3".equals(payStatus)) {
                if (position == 1) {
                    holder.tvPrice.setVisibility(View.VISIBLE);
                    holder.ivIconState.setVisibility(View.GONE);
                } else {
                    holder.tvPrice.setVisibility(View.GONE);
                    holder.ivIconState.setImageResource(R.drawable.ic_unlock_2);
                    holder.ivIconState.setVisibility(View.VISIBLE);
                }
            } else {
                if (position == 1) {
                    holder.tvPrice.setVisibility(View.VISIBLE);
                    holder.ivIconState.setVisibility(View.GONE);
                } else {
                    holder.tvPrice.setVisibility(View.GONE);
                    holder.ivIconState.setImageResource(R.drawable.ic_lock);
                    holder.ivIconState.setVisibility(View.VISIBLE);
                }
            }
        } else if (fholder instanceof ViewHolderZhuanjiaBlackboardTop) {
            ViewHolderZhuanjiaBlackboardTop holder = (ViewHolderZhuanjiaBlackboardTop) fholder;
            holder.ivZhengshu.setImageURI(itemInfo.getTitle());
        } else if (fholder instanceof ViewHolderZhuanjiaBlackboardMore) {
            ViewHolderZhuanjiaBlackboardMore holder = (ViewHolderZhuanjiaBlackboardMore) fholder;
            holder.tvTitle.setText(itemInfo.getTitle());
        }
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(View view) {
        return new ViewHolderDecoration(view, false);
    }

    public class ViewHolderDecoration extends RecyclerView.ViewHolder {

        TextView tvTitle;

        public ViewHolderDecoration(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                AutoUtils.auto(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.tv_latest_news);
            }
        }
    }

    public class ViewHolderZhuanjiaBlackboardMore extends RecyclerView.ViewHolder {

        TextView tvTitle;

        public ViewHolderZhuanjiaBlackboardMore(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                AutoUtils.auto(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            }
        }
    }

    public class ViewHolderZhuanjiaBlackboardTop extends RecyclerView.ViewHolder {

        SimpleDraweeView ivZhengshu;

        public ViewHolderZhuanjiaBlackboardTop(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                AutoUtils.auto(itemView);
                ivZhengshu = (SimpleDraweeView) itemView.findViewById(R.id.iv_zhengshu);
            }
        }
    }

    public class ViewHolderZhuanjia extends RecyclerView.ViewHolder {
        private SimpleDraweeView ivPicture;
        private TextView tvName;
        private TextView tvDesc;
        private TextView tvPreviewNum;
        private TextView tvPrice;
        private ImageView ivIconState;

        public ViewHolderZhuanjia(final View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                AutoUtils.auto(itemView);
                ivPicture = (SimpleDraweeView) itemView.findViewById(R.id.iv_authorPhoto);
                tvName = (TextView) itemView.findViewById(R.id.tv_authorName);
                tvDesc = (TextView) itemView.findViewById(R.id.tv_authorDesc);
                tvPreviewNum = (TextView) itemView.findViewById(R.id.tv_previewNum);
                tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
                ivIconState = (ImageView) itemView.findViewById(R.id.iv_iconState);
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
}
