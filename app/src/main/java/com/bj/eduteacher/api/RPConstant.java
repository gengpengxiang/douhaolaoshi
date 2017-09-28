package com.bj.eduteacher.api;

/**
 * Created by zz379 on 2017/5/15.
 */

public class RPConstant {
    public static final String EXTRA_CHAT_TYPE = "chat_type";
    public static final String EXTRA_GROUP_MEMBERS = "group_members";
    public static final String EXTRA_GROUP_USER = "group_member";
    public static final String EXTRA_GROUP_ID = "group_id";
    public static final String EXTRA_TRANSFER_AMOUNT = "money_transfer_amount";
    public static final String MESSAGE_ATTR_IS_TRANSFER_PACKET_MESSAGE = "money_is_transfer_message";
    public static final String EXTRA_SPONSOR_NAME = "money_sponsor_name";
    public static final String EXTRA_RED_PACKET_GREETING = "money_greeting";
    public static final String EXTRA_RED_PACKET_SENDER_ID = "money_sender_id";
    public static final String EXTRA_RED_PACKET_RECEIVER_ID = "money_receiver_id";
    public static final String MESSAGE_ATTR_IS_RED_PACKET_MESSAGE = "is_money_msg";
    public static final String MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE = "is_open_money_msg";
    public static final String MESSAGE_ATTR_RED_PACKET_TYPE = "money_type_special";
    public static final String MESSAGE_ATTR_SPECIAL_RECEIVER_ID = "special_money_receiver_id";
    public static final String EXTRA_RED_PACKET_SENDER_NAME = "money_sender";
    public static final String EXTRA_RED_PACKET_RECEIVER_NAME = "money_receiver";
    public static final String EXTRA_RED_PACKET_ID = "ID";
    public static final String REFRESH_GROUP_RED_PACKET_ACTION = "refresh_group_money_action";
    public static final String EXTRA_RED_PACKET_GROUP_ID = "money_from_group_id";
    public static final String EXTRA_RED_PACKET_TYPE = "red_packet_type";
    public static final String EXTRA_RED_PACKET_INFO = "red_packet_info";
    public static final String EXTRA_MESSAGE_DIRECT = "message_direct";
    public static final String HEADER_KEY_AUTH_TOKEN = "x-auth-token";
    public static final String HEADER_KEY_DEVICE_ID = "device-id";
    public static final String HEADER_KEY_VERSION_CODE = "version";
    public static final String HEADER_KEY_REQUEST_ID = "request-id";
    public static final String MESSAGE_DIRECT_SEND = "SEND";
    public static final String MESSAGE_DIRECT_RECEIVE = "RECEIVE";
    public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;
    public static final String REQUEST_CODE_SUCCESS = "0000";
    public static final String EXTRA_WEBVIEW_FROM = "webview_from";
    public static final String EXTRA_SWITCH_RECORD = "switch_record";
    public static final int FROM_QA = 1003;
    public static final int EVENT_REFRESH_DATA = 20;
    public static final int EVENT_LOAD_MORE_DATA = 30;
    public static final String GROUP_RED_PACKET_TYPE_RANDOM = "rand";
    public static final String GROUP_RED_PACKET_TYPE_AVERAGE = "avg";
    public static final String GROUP_RED_PACKET_TYPE_EXCLUSIVE = "member";
    public static final String GROUP_RED_PACKET_TYPE_PRI = "randpri";
    public static final String GROUP_RED_PACKET_TYPE = "group_red_packet_type";
    public static final String AD_RED_PACKET_TYPE = "advertisement";
    public static final String RED_PACKET_TYPE_RANDOM = "const";
    public static final String EM_META_KEY = "EASEMOB_APPKEY";
    public static final int RECORD_TAG_SEND = 0;
    public static final int RECORD_TAG_RECEIVED = 1;
    public static final int PAY_STATUS_OTHER_ERROR = 1;
    public static final int PAY_STATUS_ALI_AUTHORIZED = 2;
    public static final int PAY_STATUS_CHECK_ALI_ORDER_ERROR = 3;
    public static final int PAY_STATUS_ALI_PAY_FAIL = 4;
    public static final int PAY_STATUS_ALI_PAY_CANCEL = 5;
    public static final int PAY_STATUS_ALI_AUTH_SUCCESS = 6;
    public static final int PAY_STATUS_AD_SHARE_SUCCESS = 7;
    public static final int PAY_STATUS_UNBIND_ALI_ACCOUNT = 8;
    public static final String ALI_NO_AUTHORIZED = "60201";
    public static final String TIMEOUT_ERROR_CODE = "-100";
    public static final String AUTH_METHOD_EASEMOB = "AUTH_METHOD_EASEMOB";
    public static final String AUTH_METHOD_SIGN = "AUTH_METHOD_SIGN";
    public static final String AUTH_METHOD_YTX = "AUTH_METHOD_YTX";
    public static final String STATISTICS_TYPE_AD_OPEN = "rp.hb.ad.open_hb";
    public static final String STATISTICS_TYPE_VIEW_AD = "rp.hb.ad.view_ad";
    public static final String STATISTICS_TYPE_CLICK_AD = "rp.hb.ad.click_ad";
    public static final int RP_ITEM_TYPE_SINGLE = 1;
    public static final int RP_ITEM_TYPE_GROUP = 2;
    public static final int RP_ITEM_TYPE_RANDOM = 3;
    public static final String RP_TIP_DIALOG_TAG = "rp_tip_dialog_tag";
    public static final String RP_PACKET_DIALOG_TAG = "rp_packet_dialog_tag";
    public static final String RP_AD_PACKET_OUT = "3013";

    public RPConstant() {
    }
}
