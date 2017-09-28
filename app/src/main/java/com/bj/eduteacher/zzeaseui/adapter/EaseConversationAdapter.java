package com.bj.eduteacher.zzeaseui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.bj.eduteacher.R;
import com.bj.eduteacher.zzeaseui.model.EaseAtMessageHelper;
import com.bj.eduteacher.zzeaseui.model.EaseConversation;
import com.bj.eduteacher.zzeaseui.utils.EaseCommonUtils;
import com.bj.eduteacher.zzeaseui.utils.EaseSmileUtils;
import com.bj.eduteacher.zzeaseui.utils.EaseUserUtils;
import com.bj.eduteacher.zzeaseui.widget.EaseConversationList;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * conversation list adapter
 */
public class EaseConversationAdapter extends ArrayAdapter<EaseConversation> {
    private List<EaseConversation> conversationList;
    private List<EaseConversation> copyConversationList;
    private ConversationFilter conversationFilter;
    private boolean notiyfyByFilter;

    protected int primaryColor;
    protected int secondaryColor;
    protected int timeColor;
    protected int primarySize;
    protected int secondarySize;
    protected float timeSize;

    public EaseConversationAdapter(Context context, int resource,
                                   List<EaseConversation> objects) {
        super(context, resource, objects);
        conversationList = objects;
        copyConversationList = new ArrayList<EaseConversation>();
        copyConversationList.addAll(objects);
    }

    @Override
    public int getCount() {
        return conversationList.size();
    }

    @Override
    public EaseConversation getItem(int arg0) {
        if (arg0 < conversationList.size()) {
            return conversationList.get(arg0);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ease_row_chat_history, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.unreadLabel = (TextView) convertView.findViewById(R.id.unread_msg_number);
            holder.message = (TextView) convertView.findViewById(R.id.message);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            holder.msgState = convertView.findViewById(R.id.msg_state);
            holder.list_itease_layout = (RelativeLayout) convertView.findViewById(R.id.list_itease_layout);
            holder.motioned = (TextView) convertView.findViewById(R.id.mentioned);
            holder.classname = (TextView) convertView.findViewById(R.id.classname);
            convertView.setTag(holder);
        }
        holder.list_itease_layout.setBackgroundResource(R.drawable.ease_mm_listitem);
        holder.message.setText("");

        // get conversation
        EMConversation conversation = getItem(position).getEmConversation();
        // get username or group id
        String username = conversation.conversationId();

        if (conversation.getType() == EMConversationType.GroupChat) {
            String groupId = conversation.conversationId();
            if (EaseAtMessageHelper.get().hasAtMeMsg(groupId)) {
                holder.motioned.setVisibility(View.VISIBLE);
            } else {
                holder.motioned.setVisibility(View.GONE);
            }
            // group message, show group avatar
            holder.avatar.setImageResource(R.drawable.ease_group_icon);
            EMGroup group = EMClient.getInstance().groupManager().getGroup(username);
            holder.name.setText(group != null ? group.getGroupName() : username);
        } else if (conversation.getType() == EMConversationType.ChatRoom) {
            holder.avatar.setImageResource(R.drawable.ease_group_icon);
            EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(username);
            holder.name.setText(room != null && !TextUtils.isEmpty(room.getName()) ? room.getName() : username);
            holder.motioned.setVisibility(View.GONE);
        } else {
            EMMessage message = conversation.getLastMessage();
            // 设置名字和头像
            EaseUserUtils.setUserAvatar(getContext(), getItem(position).getUserPhoto(), holder.avatar);
            EaseUserUtils.setUserNick(getItem(position).getUserNick(), holder.name);
            holder.motioned.setVisibility(View.GONE);
        }

        if (getItem(position).getClassName() != null && !"".equals(getItem(position).getClassName())) {
            holder.classname.setVisibility(View.VISIBLE);
            holder.classname.setText(getItem(position).getClassName());
        }

        if (conversation.getUnreadMsgCount() > 0) {
            // show unread message count
            String unreadNumber = conversation.getUnreadMsgCount() > 99 ? "99+" : String.valueOf(conversation.getUnreadMsgCount());
            holder.unreadLabel.setText(unreadNumber);
            holder.unreadLabel.setVisibility(View.VISIBLE);
        } else {
            holder.unreadLabel.setVisibility(View.INVISIBLE);
        }

        if (conversation.getAllMsgCount() != 0) {
            // show the content of latest message
            EMMessage lastMessage = conversation.getLastMessage();
            String content = null;
            if (cvsListHelper != null) {
                content = cvsListHelper.onSetItemSecondaryText(lastMessage);
            }
            holder.message.setText(EaseSmileUtils.getSmiledText(getContext(), EaseCommonUtils.getMessageDigest(lastMessage, (this.getContext()))), BufferType.SPANNABLE);
            if (content != null) {
                holder.message.setText(content);
            }
            holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
            if (lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL) {
                holder.msgState.setVisibility(View.VISIBLE);
            } else {
                holder.msgState.setVisibility(View.GONE);
            }
        }

        //set property
        // holder.name.setTextColor(primaryColor);
        // holder.message.setTextColor(secondaryColor);
        // holder.time.setTextColor(timeColor);
        if (primarySize != 0)
            holder.name.setTextSize(TypedValue.COMPLEX_UNIT_PX, primarySize);
        if (secondarySize != 0)
            holder.message.setTextSize(TypedValue.COMPLEX_UNIT_PX, secondarySize);
        if (timeSize != 0)
            holder.time.setTextSize(TypedValue.COMPLEX_UNIT_PX, timeSize);

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (!notiyfyByFilter) {
            copyConversationList.clear();
            copyConversationList.addAll(conversationList);
            notiyfyByFilter = false;
        }
    }

    @Override
    public Filter getFilter() {
        if (conversationFilter == null) {
            conversationFilter = new ConversationFilter(conversationList);
        }
        return conversationFilter;
    }


    public void setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
    }

    public void setSecondaryColor(int secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public void setTimeColor(int timeColor) {
        this.timeColor = timeColor;
    }

    public void setPrimarySize(int primarySize) {
        this.primarySize = primarySize;
    }

    public void setSecondarySize(int secondarySize) {
        this.secondarySize = secondarySize;
    }

    public void setTimeSize(float timeSize) {
        this.timeSize = timeSize;
    }


    private class ConversationFilter extends Filter {
        List<EaseConversation> mOriginalValues = null;

        public ConversationFilter(List<EaseConversation> mList) {
            mOriginalValues = mList;
        }

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                mOriginalValues = new ArrayList<EaseConversation>();
            }
            if (prefix == null || prefix.length() == 0) {
                results.values = copyConversationList;
                results.count = copyConversationList.size();
            } else {
                String prefixString = prefix.toString();
                final int count = mOriginalValues.size();
                final ArrayList<EaseConversation> newValues = new ArrayList<EaseConversation>();

                for (int i = 0; i < count; i++) {
                    final EaseConversation value = mOriginalValues.get(i);
//                    String username = value.conversationId();

//                    EMGroup group = EMClient.getInstance().groupManager().getGroup(username);
//                    if (group != null) {
//                        username = group.getGroupName();
//                    } else {
//                        EaseUser user = EaseUserUtils.getUserInfo(username);
//                        // TODO: not support Nick anymore
//                        if (user != null && user.getNick() != null)
//                            username = user.getNick();
//                    }

                    // 只检索名字
                    String username = value.getUserNick();
                    // First match against the whole ,non-splitted value
                    if (username.contains(prefixString)) {
                        newValues.add(value);
                    } else {
                        final String[] words = username.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (String word : words) {
                            if (word.contains(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            conversationList.clear();
            if (results.values != null) {
                conversationList.addAll((List<EaseConversation>) results.values);
            }
            if (results.count > 0) {
                notiyfyByFilter = true;
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    private EaseConversationList.EaseConversationListHelper cvsListHelper;

    public void setCvsListHelper(EaseConversationList.EaseConversationListHelper cvsListHelper) {
        this.cvsListHelper = cvsListHelper;
    }

    private static class ViewHolder {
        /**
         * who you chat with
         */
        TextView name;
        /**
         * unread message count
         */
        TextView unreadLabel;
        /**
         * content of last message
         */
        TextView message;
        /**
         * time of last message
         */
        TextView time;
        TextView classname;
        /**
         * avatar
         */
        ImageView avatar;
        /**
         * status of last message
         */
        View msgState;
        /**
         * layout
         */
        RelativeLayout list_itease_layout;
        TextView motioned;
    }
}

