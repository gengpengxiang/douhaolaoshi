package com.bj.eduteacher.adapter;


import android.graphics.Color;
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
import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by zz379 on 2017/1/6.
 * 全部学生页面adapter
 */

public class DoukeNewDetailAdapter extends BaseRecyclerAdapter<RecyclerView.ViewHolder> {

    private List<ArticleInfo> mDataList;

    public DoukeNewDetailAdapter(List<ArticleInfo> mDataList) {
        this.mDataList = mDataList;
    }

    private enum ShowType {ITEM_TYPE_DECORATION, ITEM_TYPE_ZHUANJIA_RES, ITEM_TYPE_DOUKE}

    @Override
    public int getAdapterItemViewType(int position) {
        if (mDataList.get(position).getShowType() == ArticleInfo.SHOW_TYPE_DECORATION) {
            return ShowType.ITEM_TYPE_DECORATION.ordinal();
        } else if (mDataList.get(position).getShowType() == ArticleInfo.SHOW_TYPE_ZHUANJIA_RES) {
            return ShowType.ITEM_TYPE_ZHUANJIA_RES.ordinal();
        } else {
            return ShowType.ITEM_TYPE_DOUKE.ordinal();
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
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_zhuanjia_res, parent, false);
            vh = new ViewHolderZhuanjia(v, true);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_douke_new_detail, parent, false);
            vh = new ViewHolderDouke(v, true);
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
            String price = itemInfo.getAgreeNumber();
            String buyType = itemInfo.getCommentNumber();
            if ("0".equals(price)) {
                holder.tvPrice.setText("免费");
                holder.tvPrice.setTextColor(Color.parseColor("#EC4E2F"));
                holder.tvBuyRes.setText("查看");
            } else {
                if ("0".equals(buyType)) {
                    holder.tvPrice.setText("¥ " + (Double.parseDouble(price)) / 100);
                    holder.tvBuyRes.setText("购买");
                } else {
                    holder.tvPrice.setText("已购");
                    holder.tvBuyRes.setText("查看");
                }
            }
        } else {
            ViewHolderDouke holder = (ViewHolderDouke) fholder;
            holder.tvTitle.setText(itemInfo.getTitle());
        }
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(View view) {
        return new ViewHolderDouke(view, false);
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

    public class ViewHolderZhuanjia extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvDesc;
        private TextView tvPrice;
        private TextView tvBuyRes;
        private SimpleDraweeView ivPicture;
        private TextView tvPreviewNum;

        public ViewHolderZhuanjia(final View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                AutoUtils.auto(itemView);
                ivPicture = (SimpleDraweeView) itemView.findViewById(R.id.iv_authorPhoto);
                tvName = (TextView) itemView.findViewById(R.id.tv_authorName);
                tvDesc = (TextView) itemView.findViewById(R.id.tv_authorDesc);
                tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
                tvBuyRes = (TextView) itemView.findViewById(R.id.tv_buyRes);
                tvPreviewNum = (TextView) itemView.findViewById(R.id.tv_previewNum);

                RxView.clicks(tvBuyRes)
                        .throttleFirst(1, TimeUnit.SECONDS)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Object>() {
                            @Override
                            public void accept(@NonNull Object o) throws Exception {
                                if (myItemClickListener != null) {
                                    myItemClickListener.onZhuanjiaClick(tvBuyRes, getAdapterPosition());
                                }
                            }
                        });
                RxView.clicks(itemView)
                        .throttleFirst(1, TimeUnit.SECONDS)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Object>() {
                            @Override
                            public void accept(@NonNull Object o) throws Exception {
                                if (myItemClickListener != null) {
                                    myItemClickListener.onZhuanjiaClick(itemView, getAdapterPosition());
                                }
                            }
                        });
            }
        }
    }

    public class ViewHolderDouke extends RecyclerView.ViewHolder {
        private TextView tvTitle;

        public ViewHolderDouke(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                AutoUtils.auto(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
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
        void onZhuanjiaClick(View view, int position);
    }
}
