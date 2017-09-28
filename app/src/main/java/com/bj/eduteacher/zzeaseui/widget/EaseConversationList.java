package com.bj.eduteacher.zzeaseui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ListView;

import com.bj.eduteacher.R;
import com.bj.eduteacher.zzeaseui.adapter.EaseConversationAdapter;
import com.bj.eduteacher.zzeaseui.model.EaseConversation;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.List;

public class EaseConversationList extends ListView {

    // protected int primaryColor;
    // protected int secondaryColor;
    // protected int timeColor;
    // protected int primarySize;
    // protected int secondarySize;
    // protected float timeSize;


    protected final int MSG_REFRESH_ADAPTER_DATA = 0;

    protected Context context;
    protected EaseConversationAdapter adapter;
    protected List<EaseConversation> conversations = new ArrayList<EaseConversation>();
    protected List<EMConversation> passedListRef = null;


    public EaseConversationList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EaseConversationList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
//        super.onMeasure(widthMeasureSpec, expandSpec);
//    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseConversationList);
        // primaryColor = ta.getColor(R.styleable.EaseConversationList_cvsListPrimaryTextColor, R.color.list_itease_primary_color);
        // secondaryColor = ta.getColor(R.styleable.EaseConversationList_cvsListSecondaryTextColor, R.color.list_itease_secondary_color);
        // timeColor = ta.getColor(R.styleable.EaseConversationList_cvsListTimeTextColor, R.color.list_itease_secondary_color);
        // primarySize = ta.getDimensionPixelSize(R.styleable.EaseConversationList_cvsListPrimaryTextSize, 0);
        // secondarySize = ta.getDimensionPixelSize(R.styleable.EaseConversationList_cvsListSecondaryTextSize, 0);
        // timeSize = ta.getDimension(R.styleable.EaseConversationList_cvsListTimeTextSize, 0);

        ta.recycle();

    }

    public void init(List<EaseConversation> conversationList) {
        this.init(conversationList, null);
    }

    public void init(List<EaseConversation> conversationList, EaseConversationListHelper helper) {
        conversations = conversationList;
        if (helper != null) {
            this.conversationListHelper = helper;
        }
        adapter = new EaseConversationAdapter(context, 0, conversationList);
        adapter.setCvsListHelper(conversationListHelper);
//        adapter.setPrimaryColor(primaryColor);
//        adapter.setPrimarySize(primarySize);
//        adapter.setSecondaryColor(secondaryColor);
//        adapter.setSecondarySize(secondarySize);
//        adapter.setTimeColor(timeColor);
//        adapter.setTimeSize(timeSize);
        setAdapter(adapter);
    }

    @Override
    public EaseConversationAdapter getAdapter() {
        return adapter;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MSG_REFRESH_ADAPTER_DATA:
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public EaseConversation getItem(int position) {
        return (EaseConversation) adapter.getItem(position);
    }

    public void refresh() {
        if (!handler.hasMessages(MSG_REFRESH_ADAPTER_DATA)) {
            handler.sendEmptyMessage(MSG_REFRESH_ADAPTER_DATA);
        }
    }

    public void filter(CharSequence str) {
        adapter.getFilter().filter(str);
    }


    private EaseConversationListHelper conversationListHelper;

    public interface EaseConversationListHelper {
        /**
         * set content of second line
         *
         * @param lastMessage
         * @return
         */
        String onSetItemSecondaryText(EMMessage lastMessage);
    }

    public void setConversationListHelper(EaseConversationListHelper helper) {
        conversationListHelper = helper;
    }
}
