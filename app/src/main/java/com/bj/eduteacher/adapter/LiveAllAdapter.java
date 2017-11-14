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
import com.bj.eduteacher.tool.ShowNameUtil;
import com.bj.eduteacher.utils.DateFormatUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.zzautolayout.utils.AutoUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by zz379 on 2017/1/6.
 * 全部直播页面Adapter
 */

public class LiveAllAdapter extends BaseRecyclerAdapter<RecyclerView.ViewHolder> {

    private List<ArticleInfo> mDataList;

    public LiveAllAdapter(List<ArticleInfo> mDataList) {
        this.mDataList = mDataList;
    }

    public enum ShowType {ITEM_TYPE_DECORATION, ITEM_TYPE_DOUKE}

    @Override
    public int getAdapterItemViewType(int position) {
        if (mDataList.get(position).getShowType() == ArticleInfo.SHOW_TYPE_DECORATION) {
            return ShowType.ITEM_TYPE_DECORATION.ordinal();
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
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_live, parent, false);
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
        } else {
            ViewHolderDouke holder = (ViewHolderDouke) fholder;
            holder.ivPicture.setImageURI(itemInfo.getArticlePicture());
            holder.tvLiveTitle.setText(itemInfo.getTitle());

            String phone;
            if (!StringUtils.isEmpty(itemInfo.getAuthDesc())) {
                phone = itemInfo.getAuthDesc().substring(3);
                if (!StringUtils.isEmpty(phone) && phone.length() > 10) {
                    phone = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                }
            } else {
                phone = "";
            }

            ShowNameUtil.showNameLogic(holder.tvAuthorName, itemInfo.getNickname(), itemInfo.getAuthor(), phone);

            holder.ivAuthorPhoto.setImageURI(itemInfo.getAuthImg());

            if ("1".equals(itemInfo.getPreviewType())) {
                holder.ivLiveIcon.setImageResource(R.drawable.ic_live_on);
                holder.tvLiveTime.setText("开始时间：" + DateFormatUtils.formatByTimeMillis(Long.parseLong(itemInfo.getPostTime()) * 1000, DateFormatUtils.FORMAT_PATTERN_EXAMPLE_2));
            } else {
                holder.ivLiveIcon.setImageResource(R.drawable.ic_live_off);
                holder.tvLiveTime.setText("结束时间：" + DateFormatUtils.formatByTimeMillis(Long.parseLong(itemInfo.getFinishTime()) * 1000, DateFormatUtils.FORMAT_PATTERN_EXAMPLE_2));
            }

            String price = itemInfo.getAgreeNumber();
            String buyType = itemInfo.getCommentNumber();
            if (StringUtils.isEmpty(price) || "0".equals(price)) {
                holder.tvPrice.setText("免费");
            } else {
                if ("0".equals(buyType)) {
                    holder.tvPrice.setText("¥ " + (Double.parseDouble(price)) / 100);
                } else {
                    holder.tvPrice.setText("已购");
                }
            }
            // 如果是已经结束的直播并且拥有直播录像的话
            if ("2".equals(itemInfo.getPreviewType()) && !StringUtils.isEmpty(itemInfo.getPlayUrl())) {
                holder.tvPlayback.setVisibility(View.VISIBLE);
            } else {
                holder.tvPlayback.setVisibility(View.GONE);
            }
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

    public class ViewHolderDouke extends RecyclerView.ViewHolder {
        private SimpleDraweeView ivPicture;
        private TextView tvLiveTitle;
        private TextView tvAuthorName;
        private SimpleDraweeView ivAuthorPhoto;
        private ImageView ivLiveIcon;
        private TextView tvLiveTime;
        private TextView tvPrice;
        private TextView tvPlayback;

        public ViewHolderDouke(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                AutoUtils.auto(itemView);
                ivPicture = (SimpleDraweeView) itemView.findViewById(R.id.iv_picture);
                tvLiveTitle = (TextView) itemView.findViewById(R.id.tv_liveTitle);
                tvAuthorName = (TextView) itemView.findViewById(R.id.tv_authorName);
                ivAuthorPhoto = (SimpleDraweeView) itemView.findViewById(R.id.iv_authorPhoto);
                ivLiveIcon = (ImageView) itemView.findViewById(R.id.iv_iconLive);
                tvLiveTime = (TextView) itemView.findViewById(R.id.tv_liveTime);
                tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
                tvPlayback = (TextView) itemView.findViewById(R.id.tv_playback);
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
