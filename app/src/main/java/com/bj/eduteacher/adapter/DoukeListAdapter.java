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
 * 全部学生页面adapter
 */

public class DoukeListAdapter extends BaseRecyclerAdapter<RecyclerView.ViewHolder> {

    private List<ArticleInfo> mDataList;

    public DoukeListAdapter(List<ArticleInfo> mDataList) {
        this.mDataList = mDataList;
    }

    public enum ShowType {
        ITEM_TYPE_DECORATION, ITEM_TYPE_TEACHER,
        ITEM_TYPE_ZHUANJIA, ITEM_TYPE_ZHUANJIA_ALL,
        ITEM_TYPE_ZHUANJIA_RES, ITEM_TYPE_DOUKE,
        ITEM_TYPE_LIVE
    }

    @Override
    public int getAdapterItemViewType(int position) {
        if (mDataList.get(position).getShowType() == ArticleInfo.SHOW_TYPE_DECORATION) {
            return ShowType.ITEM_TYPE_DECORATION.ordinal();
        } else if (mDataList.get(position).getShowType() == ArticleInfo.SHOW_TYPE_ZHUANJIA) {
            return ShowType.ITEM_TYPE_ZHUANJIA.ordinal();
        } else if (mDataList.get(position).getShowType() == ArticleInfo.SHOW_TYPE_ZHUANJIA_ALL) {
            return ShowType.ITEM_TYPE_ZHUANJIA_ALL.ordinal();
        } else if (mDataList.get(position).getShowType() == ArticleInfo.SHOW_TYPE_ZHUANJIA_RES) {
            return ShowType.ITEM_TYPE_ZHUANJIA_RES.ordinal();
        } else if (mDataList.get(position).getShowType() == ArticleInfo.SHOW_TYPE_TEACHER) {
            return ShowType.ITEM_TYPE_TEACHER.ordinal();
        } else if (mDataList.get(position).getShowType() == ArticleInfo.SHOW_TYPE_LIVE) {
            return ShowType.ITEM_TYPE_LIVE.ordinal();
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
        } else if (viewType == ShowType.ITEM_TYPE_ZHUANJIA.ordinal()) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_zhuanjia, parent, false);
            vh = new ViewHolderZhuanjia(v, true);
        } else if (viewType == ShowType.ITEM_TYPE_ZHUANJIA_ALL.ordinal()) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_header_zhuanjia_all, parent, false);
            vh = new ViewHolderZhuanjiaAll(v, true);
        } else if (viewType == ShowType.ITEM_TYPE_ZHUANJIA_RES.ordinal()) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_zhuanjia_home_res, parent, false);
            vh = new ViewHolderZhuanjiaRes(v, true);
        } else if (viewType == ShowType.ITEM_TYPE_TEACHER.ordinal()) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_teacher, parent, false);
            vh = new ViewHolderTeacher(v, true);
        } else if (viewType == ShowType.ITEM_TYPE_LIVE.ordinal()) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_live, parent, false);
            vh = new ViewHolderLive(v, true);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_douke, parent, false);
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
            holder.tvAuthorName.setText(itemInfo.getAuthor());
            holder.tvAuthorDesc.setText(itemInfo.getTitle());
            holder.tvAuthorTitle.setText(itemInfo.getAuthDesc());
            holder.tvAuthorNews.setText(itemInfo.getContent());
            if (!StringUtils.isEmpty(itemInfo.getAuthImg())) {
                holder.ivAuthorPhoto.setImageURI(itemInfo.getAuthImg());
            }
        } else if (fholder instanceof ViewHolderZhuanjiaAll) {
            String text = itemInfo.getTitle();
            ViewHolderZhuanjiaAll holder = (ViewHolderZhuanjiaAll) fholder;
            holder.tvTitle.setText(text);
            if ("查看全部".equals(text)) {
                holder.topLine.setVisibility(View.VISIBLE);
            } else {
                holder.topLine.setVisibility(View.GONE);
            }
        } else if (fholder instanceof ViewHolderZhuanjiaRes) {
            ViewHolderZhuanjiaRes holder = (ViewHolderZhuanjiaRes) fholder;
            holder.ivPicture.setImageURI(itemInfo.getAuthImg());
            holder.tvName.setText(itemInfo.getTitle());
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
        } else if (fholder instanceof ViewHolderTeacher) {
            ViewHolderTeacher holder = (ViewHolderTeacher) fholder;
            holder.tvAuthorName.setText(itemInfo.getAuthor());
            holder.tvAuthorDesc.setText(itemInfo.getTitle());
            String newTeacher = itemInfo.getContent();
            if (!StringUtils.isEmpty(newTeacher) && !"0".equals(newTeacher)) {
                holder.tvNewTeacher.setVisibility(View.VISIBLE);
            } else {
                holder.tvNewTeacher.setVisibility(View.INVISIBLE);
            }
            if (!StringUtils.isEmpty(itemInfo.getAuthImg())) {
                holder.ivAuthorPhoto.setImageURI(itemInfo.getAuthImg());
            }
        } else if (fholder instanceof ViewHolderLive) {
            ViewHolderLive holder = (ViewHolderLive) fholder;
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
        } else {
            ViewHolderDouke holder = (ViewHolderDouke) fholder;
            holder.tvTitle.setText(itemInfo.getTitle());
            holder.tvAuthorName.setText(itemInfo.getAuthor());
            if (!StringUtils.isEmpty(itemInfo.getAuthImg())) {
                holder.ivAuthorPhoto.setImageURI(itemInfo.getAuthImg());
            }
            if (!StringUtils.isEmpty(itemInfo.getArticlePicture())) {
                holder.ivPicture.setImageURI(itemInfo.getArticlePicture());
            }
            String desc = itemInfo.getAuthDesc();
            holder.tvAuthorDesc.setText(StringUtils.isEmpty(desc) ? "暂无" : desc);
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

    public class ViewHolderZhuanjiaAll extends RecyclerView.ViewHolder {

        TextView tvTitle;
        View topLine;

        public ViewHolderZhuanjiaAll(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                AutoUtils.auto(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.tv_name);
                topLine = itemView.findViewById(R.id.topLine);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (myItemClickListener != null) {
                            myItemClickListener.onZhuanjiaAllClick(v, getAdapterPosition() - 1);
                        }
                    }
                });
            }
        }
    }

    public class ViewHolderZhuanjia extends RecyclerView.ViewHolder {
        private TextView tvAuthorName, tvAuthorDesc, tvAuthorTitle, tvAuthorNews;
        private SimpleDraweeView ivAuthorPhoto;

        public ViewHolderZhuanjia(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                AutoUtils.auto(itemView);
                ivAuthorPhoto = (SimpleDraweeView) itemView.findViewById(R.id.iv_authorPhoto);
                tvAuthorName = (TextView) itemView.findViewById(R.id.tv_authorName);
                tvAuthorDesc = (TextView) itemView.findViewById(R.id.tv_authorDesc);
                tvAuthorTitle = (TextView) itemView.findViewById(R.id.tv_title);
                tvAuthorNews = (TextView) itemView.findViewById(R.id.tv_news_title);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (myItemClickListener != null) {
                            myItemClickListener.onZhuanjiaClick(v, getAdapterPosition() - 1);
                        }
                    }
                });
            }
        }
    }

    public class ViewHolderTeacher extends RecyclerView.ViewHolder {

        private TextView tvAuthorName, tvAuthorDesc, tvNewTeacher;
        private SimpleDraweeView ivAuthorPhoto;

        public ViewHolderTeacher(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                AutoUtils.auto(itemView);
                ivAuthorPhoto = (SimpleDraweeView) itemView.findViewById(R.id.iv_authorPhoto);
                tvAuthorName = (TextView) itemView.findViewById(R.id.tv_authorName);
                tvAuthorDesc = (TextView) itemView.findViewById(R.id.tv_authorDesc);
                tvNewTeacher = (TextView) itemView.findViewById(R.id.tv_newTeacher);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (myItemClickListener != null) {
                            myItemClickListener.onTeacherClick(v, getAdapterPosition() - 1);
                        }
                    }
                });
            }
        }
    }

    public class ViewHolderZhuanjiaRes extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvPrice;
        private SimpleDraweeView ivPicture;

        public ViewHolderZhuanjiaRes(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                AutoUtils.auto(itemView);
                ivPicture = (SimpleDraweeView) itemView.findViewById(R.id.iv_authorPhoto);
                tvName = (TextView) itemView.findViewById(R.id.tv_authorName);
                tvPrice = (TextView) itemView.findViewById(R.id.tv_price);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (myItemClickListener != null) {
                            myItemClickListener.onZhuanjiaResClick(tvPrice, getAdapterPosition() - 1);
                        }
                    }
                });
            }
        }
    }

    public class ViewHolderLive extends RecyclerView.ViewHolder {
        private SimpleDraweeView ivPicture;
        private TextView tvLiveTitle;
        private TextView tvAuthorName;
        private SimpleDraweeView ivAuthorPhoto;
        private ImageView ivLiveIcon;
        private TextView tvLiveTime;
        private TextView tvPrice;

        public ViewHolderLive(View itemView, boolean isItem) {
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
            }
        }
    }

    public class ViewHolderDouke extends RecyclerView.ViewHolder {
        private SimpleDraweeView ivAuthorPhoto;
        private SimpleDraweeView ivPicture;
        private TextView tvTitle, tvAuthorName, tvAuthorDesc;

        public ViewHolderDouke(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                AutoUtils.auto(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
                tvAuthorName = (TextView) itemView.findViewById(R.id.tv_authorName);
                tvAuthorDesc = (TextView) itemView.findViewById(R.id.tv_authorDesc);
                ivAuthorPhoto = (SimpleDraweeView) itemView.findViewById(R.id.iv_authorPhoto);
                ivPicture = (SimpleDraweeView) itemView.findViewById(R.id.iv_picture);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (myItemClickListener != null) {
                            myItemClickListener.onClick(v, getAdapterPosition() - 1);
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
        void onClick(View view, int position);

        void onZhuanjiaClick(View view, int position);

        void onZhuanjiaAllClick(View view, int position);

        void onZhuanjiaResClick(TextView view, int position);

        void onTeacherClick(View view, int position);
    }
}
