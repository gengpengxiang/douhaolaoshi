package com.bj.eduteacher.adapter;


import android.graphics.Color;
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
import com.bj.eduteacher.tool.ShowNameUtil;
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

public class ZhuanjiaDetailAdapter extends BaseRecyclerAdapter<RecyclerView.ViewHolder> {

    private List<ArticleInfo> mDataList;

    public ZhuanjiaDetailAdapter(List<ArticleInfo> mDataList) {
        this.mDataList = mDataList;
    }

    private enum ShowType {ITEM_TYPE_DECORATION, ITEM_TYPE_ZHUANJIA_RES, ITEM_TYPE_ZHUANJIA_BLACKBOARD_TOP, ITEM_TYPE_ZHUANJIA_BLACKBOARD_MORE, ITEM_TYPE_DOUKE}

    @Override
    public int getAdapterItemViewType(int position) {
        if (mDataList.get(position).getShowType() == ArticleInfo.SHOW_TYPE_DECORATION) {
            return ShowType.ITEM_TYPE_DECORATION.ordinal();
        } else if (mDataList.get(position).getShowType() == ArticleInfo.SHOW_TYPE_ZHUANJIA_RES) {
            return ShowType.ITEM_TYPE_ZHUANJIA_RES.ordinal();
        } else if (mDataList.get(position).getShowType() == ArticleInfo.SHOW_TYPE_ZHUANJIA_BLACKBOARD_TOP) {
            return ShowType.ITEM_TYPE_ZHUANJIA_BLACKBOARD_TOP.ordinal();
        } else if (mDataList.get(position).getShowType() == ArticleInfo.SHOW_TYPE_ZHUANJIA_BLACKBOARD_MORE) {
            return ShowType.ITEM_TYPE_ZHUANJIA_BLACKBOARD_MORE.ordinal();
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
        } else if (viewType == ShowType.ITEM_TYPE_ZHUANJIA_BLACKBOARD_TOP.ordinal()) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_zhuanjia_blackboard_top, parent, false);
            vh = new ViewHolderZhuanjiaBlackboardTop(v, true);
        } else if (viewType == ShowType.ITEM_TYPE_ZHUANJIA_BLACKBOARD_MORE.ordinal()) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_zhuanjia_blackboard_more, parent, false);
            vh = new ViewHolderZhuanjiaBlackboardMore(v, true);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_zhuanjia_douke, parent, false);
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
        } else if (fholder instanceof ViewHolderZhuanjiaBlackboardTop) {
            setZhuanjiaBlackboardTopContent((ViewHolderZhuanjiaBlackboardTop) fholder, itemInfo);
        } else if (fholder instanceof ViewHolderZhuanjiaBlackboardMore) {
            setZhuanjiaBlackboardMoreContent((ViewHolderZhuanjiaBlackboardMore) fholder, itemInfo);
        } else {
            ViewHolderDouke holder = (ViewHolderDouke) fholder;
            holder.tvTitle.setText(itemInfo.getTitle());
            if (!StringUtils.isEmpty(itemInfo.getArticlePicture())) {
                holder.ivPicture.setImageURI(itemInfo.getArticlePicture());
            }
            String desc = itemInfo.getContent();
            holder.tvAuthorDesc.setText(StringUtils.isEmpty(desc) ? "暂无" : desc);
            String type = itemInfo.getAuthor();
            if ("yc".equals(type)) {
                holder.tvType.setText("原创");
            } else {
                holder.tvType.setText("推荐");
            }
        }
    }

    private void setZhuanjiaBlackboardMoreContent(ViewHolderZhuanjiaBlackboardMore holder, ArticleInfo itemInfo) {
        List<ArticleInfo> subMoreList = itemInfo.getReplyList();
        if (subMoreList != null && subMoreList.size() >= 2) {
            holder.llSubMore1.setVisibility(View.VISIBLE);
            holder.llSubMore2.setVisibility(View.VISIBLE);

            holder.tvSubTitle1.setText(subMoreList.get(0).getTitle());
            holder.tvSubReplyCount1.setText(subMoreList.get(0).getReplyCount() + " 条回复");
            holder.tvSubTitle2.setText(subMoreList.get(1).getTitle());
            holder.tvSubReplyCount2.setText(subMoreList.get(1).getReplyCount() + " 条回复");

        } else if (subMoreList != null && subMoreList.size() == 1) {
            holder.llSubMore1.setVisibility(View.VISIBLE);
            holder.llSubMore2.setVisibility(View.GONE);
            holder.tvSubTitle1.setText(subMoreList.get(0).getTitle());
            holder.tvSubReplyCount1.setText(subMoreList.get(0).getReplyCount() + " 条回复");

        } else {

        }
    }

    private void setZhuanjiaBlackboardTopContent(ViewHolderZhuanjiaBlackboardTop holder, ArticleInfo itemInfo) {
        holder.tvAllReply.setText("查看全部" + itemInfo.getReplyCount() + "条回复");
        holder.tvSubName.setText(itemInfo.getTitle());
        holder.tvSubContent.setText(itemInfo.getContent());

        List<ArticleInfo> topReplys = itemInfo.getReplyList();
        if (topReplys != null && topReplys.size() >= 2) {
            holder.rlReply1.setVisibility(View.VISIBLE);
            holder.rlReply2.setVisibility(View.VISIBLE);

            holder.ivUserReplyPhoto1.setImageURI(topReplys.get(0).getAuthImg());
            String phone1 = topReplys.get(0).getAuthDesc();
            if (!StringUtils.isEmpty(phone1) && phone1.length() > 10) {
                phone1 = phone1.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            }
            String name1 = topReplys.get(0).getAuthor();
            String nick1 = topReplys.get(0).getNickname();

            ShowNameUtil.showNameLogic(holder.tvUserReplyName1, nick1, name1, phone1);
            
            holder.tvUserReplyTime1.setText(topReplys.get(0).getPostTime());
            holder.tvUserReplyContent1.setText(topReplys.get(0).getContent());

            holder.ivUserReplyPhoto2.setImageURI(topReplys.get(1).getAuthImg());
            String phone2 = topReplys.get(1).getAuthDesc();
            if (!StringUtils.isEmpty(phone1) && phone1.length() > 10) {
                phone2 = phone2.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            }
            String name2 = topReplys.get(1).getAuthor();
            String nick2 = topReplys.get(1).getNickname();

            ShowNameUtil.showNameLogic(holder.tvUserReplyName2, nick2, name2, phone2);
            
            holder.tvUserReplyTime2.setText(topReplys.get(1).getPostTime());
            holder.tvUserReplyContent2.setText(topReplys.get(1).getContent());
        } else if (topReplys != null && topReplys.size() == 1) {
            holder.rlReply1.setVisibility(View.VISIBLE);
            holder.rlReply2.setVisibility(View.GONE);

            holder.ivUserReplyPhoto1.setImageURI(topReplys.get(0).getAuthImg());
            String phone1 = topReplys.get(0).getAuthDesc();
            if (!StringUtils.isEmpty(phone1) && phone1.length() > 10) {
                phone1 = phone1.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            }
            String name1 = topReplys.get(0).getAuthor();
            String nick1 = topReplys.get(0).getNickname();

            ShowNameUtil.showNameLogic(holder.tvUserReplyName1, nick1, name1, phone1);
            
            holder.tvUserReplyTime1.setText(topReplys.get(0).getPostTime());
            holder.tvUserReplyContent1.setText(topReplys.get(0).getContent());
        } else {
            holder.rlReply1.setVisibility(View.GONE);
            holder.rlReply2.setVisibility(View.GONE);
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

    public class ViewHolderZhuanjiaBlackboardMore extends RecyclerView.ViewHolder {

        LinearLayout llSubMore1, llSubMore2;
        TextView tvSubTitle1, tvSubTitle2;
        TextView tvSubReplyCount1, tvSubReplyCount2;
        TextView tvAllSubject;

        public ViewHolderZhuanjiaBlackboardMore(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                AutoUtils.auto(itemView);
                llSubMore1 = (LinearLayout) itemView.findViewById(R.id.ll_subMore1);
                llSubMore2 = (LinearLayout) itemView.findViewById(R.id.ll_subMore2);
                tvSubTitle1 = (TextView) itemView.findViewById(R.id.tv_subjectName1);
                tvSubTitle2 = (TextView) itemView.findViewById(R.id.tv_subjectName2);
                tvSubReplyCount1 = (TextView) itemView.findViewById(R.id.tv_subjectReplyCount1);
                tvSubReplyCount2 = (TextView) itemView.findViewById(R.id.tv_subjectReplyCount2);
                tvAllSubject = (TextView) itemView.findViewById(R.id.tv_allReply);

                llSubMore1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myItemClickListener.onSubjectClick(v, "SubMore1", getAdapterPosition() - 1);
                    }
                });
                llSubMore2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myItemClickListener.onSubjectClick(v, "SubMore2", getAdapterPosition() - 1);
                    }
                });
                tvAllSubject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myItemClickListener.onSubjectClick(v, "SubAll", getAdapterPosition() - 1);
                    }
                });
            }
        }
    }

    public class ViewHolderZhuanjiaBlackboardTop extends RecyclerView.ViewHolder {

        TextView tvAnswer;
        TextView tvAllReply;
        TextView tvSubName, tvSubContent;
        RelativeLayout rlReply1, rlReply2;
        TextView tvUserReplyName1, tvUserReplyName2;
        TextView tvUserReplyTime1, tvUserReplyTime2;
        TextView tvUserReplyContent1, tvUserReplyContent2;
        SimpleDraweeView ivUserReplyPhoto1, ivUserReplyPhoto2;
        LinearLayout llSubjectBody;
        TextView tvInvite;

        public ViewHolderZhuanjiaBlackboardTop(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                AutoUtils.auto(itemView);
                tvAnswer = (TextView) itemView.findViewById(R.id.tv_answer);
                tvAllReply = (TextView) itemView.findViewById(R.id.tv_allReply);

                tvSubName = (TextView) itemView.findViewById(R.id.tv_subjectName);
                tvSubContent = (TextView) itemView.findViewById(R.id.tv_subjectContent);

                rlReply1 = (RelativeLayout) itemView.findViewById(R.id.rl_reply1);
                rlReply2 = (RelativeLayout) itemView.findViewById(R.id.rl_reply2);

                tvUserReplyName1 = (TextView) itemView.findViewById(R.id.tv_userReplyName1);
                tvUserReplyName2 = (TextView) itemView.findViewById(R.id.tv_userReplyName2);
                tvUserReplyTime1 = (TextView) itemView.findViewById(R.id.tv_userReplyTime1);
                tvUserReplyTime2 = (TextView) itemView.findViewById(R.id.tv_userReplyTime2);
                tvUserReplyContent1 = (TextView) itemView.findViewById(R.id.tv_userReplyContent1);
                tvUserReplyContent2 = (TextView) itemView.findViewById(R.id.tv_userReplyContent2);

                ivUserReplyPhoto1 = (SimpleDraweeView) itemView.findViewById(R.id.iv_userReplyPhoto1);
                ivUserReplyPhoto2 = (SimpleDraweeView) itemView.findViewById(R.id.iv_userReplyPhoto2);

                tvInvite = (TextView) itemView.findViewById(R.id.tv_invite);

                llSubjectBody = (LinearLayout) itemView.findViewById(R.id.ll_subject);
                llSubjectBody.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (myItemClickListener != null) {
                            myItemClickListener.onSubjectClick(v, "ViewReply", getAdapterPosition() - 1);
                        }
                    }
                });
                tvAnswer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (myItemClickListener != null) {
                            myItemClickListener.onSubjectClick(v, "Answer", getAdapterPosition() - 1);
                        }
                    }
                });
                tvAllReply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (myItemClickListener != null) {
                            myItemClickListener.onSubjectClick(v, "ViewReply", getAdapterPosition() - 1);
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
                                    myItemClickListener.onSubjectClick(tvInvite, "Invite", getAdapterPosition() - 1);
                                }
                            }
                        });
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
                                    myItemClickListener.onZhuanjiaClick(tvBuyRes, getAdapterPosition() - 1);
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
                                    myItemClickListener.onZhuanjiaClick(itemView, getAdapterPosition() - 1);
                                }
                            }
                        });
            }
        }
    }

    public class ViewHolderDouke extends RecyclerView.ViewHolder {
        private SimpleDraweeView ivPicture;
        private TextView tvTitle, tvAuthorDesc;
        private TextView tvType;

        public ViewHolderDouke(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                AutoUtils.auto(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
                tvAuthorDesc = (TextView) itemView.findViewById(R.id.tv_authorDesc);
                ivPicture = (SimpleDraweeView) itemView.findViewById(R.id.iv_picture);
                tvType = (TextView) itemView.findViewById(R.id.tv_type);

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

        void onSubjectClick(View view, String tag, int position);
    }
}
