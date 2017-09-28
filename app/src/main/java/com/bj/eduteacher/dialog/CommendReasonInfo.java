package com.bj.eduteacher.dialog;

/**
 * Created by zz379 on 2017/2/13.
 */

public class CommendReasonInfo {

    private String reasonName;
    private String reasonID;

    public String getReasonName() {
        return reasonName;
    }

    public void setReasonName(String reasonName) {
        this.reasonName = reasonName;
    }

    public String getReasonID() {
        return reasonID;
    }

    public void setReasonID(String reasonID) {
        this.reasonID = reasonID;
    }

    public CommendReasonInfo(String reasonID, String reasonName) {
        this.reasonName = reasonName;
        this.reasonID = reasonID;
    }
}
