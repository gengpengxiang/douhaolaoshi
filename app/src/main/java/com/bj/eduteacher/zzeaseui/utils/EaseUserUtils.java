package com.bj.eduteacher.zzeaseui.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bj.eduteacher.R;
import com.bj.eduteacher.zzeaseui.controller.EaseUI;
import com.bj.eduteacher.zzeaseui.domain.EaseUser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class EaseUserUtils {

    static EaseUI.EaseUserProfileProvider userProvider;

    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }

    /**
     * get EaseUser according username
     *
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username) {
        if (userProvider != null)
            return userProvider.getUser(username);

        return null;
    }

    /**
     * set user avatar
     *
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView) {
        int defAvatarID;
        if (context.getPackageName().contains("teacher")) {
            defAvatarID = R.drawable.ease_default_avatar_teacher;
        } else {
            defAvatarID = R.drawable.ease_default_avatar_parent;
        }
        if (username != null && !"".equals(username)) {
            Glide.with(context).load(username).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(defAvatarID).into(imageView);
        } else {
            Glide.with(context).load(defAvatarID).into(imageView);
        }

//        EaseUser user = getUserInfo(username);
//        if (user != null && user.getAvatar() != null) {
//            try {
//                int avatarResId = Integer.parseInt(user.getAvatar());
//                Glide.with(context).load(avatarResId).into(imageView);
//            } catch (Exception e) {
//                //use default avatar
//                Glide.with(context).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ease_default_avatar).into(imageView);
//            }
//        } else {
//            Glide.with(context).load(R.drawable.ease_default_avatar).into(imageView);
//        }
    }

    /**
     * set user's nickname
     */
    public static void setUserNick(String username, TextView textView) {
        if (textView != null) {
            textView.setText(username);
        }
//        if (textView != null) {
//            EaseUser user = getUserInfo(username);
//            if (user != null && user.getNick() != null) {
//                textView.setText(user.getNick());
//            } else {
//                textView.setText(username);
//            }
//        }
    }

}
