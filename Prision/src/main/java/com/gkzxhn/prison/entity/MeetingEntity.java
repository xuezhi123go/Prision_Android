package com.gkzxhn.prison.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Raleigh.Luo on 17/4/11.
 */

public class MeetingEntity {
    private String id;
    @SerializedName("prisoner_name")
    private String name;
    @SerializedName("meeting_time")
    private String time;
    private String area;
    @SerializedName("to")
    private String yxAccount;
    private String remarks;//取消原因
    private String status;//状态

    public String getRemarks() {
        return remarks == null ? "" : remarks;
    }

    public void setRemarks(String remarks) {
        if (remarks != null && !remarks.equals("null"))
            this.remarks = remarks;
    }

    public String getStatus() {
        return status == null ? "" : status;
    }

    public void setStatus(String status) {
        if (status != null && !status.equals("null"))
            this.status = status;
    }

    public String getId() {
        return id == null ? "" : id;
    }

    public void setId(String id) {
        if (id != null && !id.equals("null"))
            this.id = id;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        if (name != null && !name.equals("null"))
            this.name = name;
    }

    public String getTime() {
        return time == null ? "" : time;
    }

    public void setTime(String time) {
        if (time != null && !time.equals("null"))
            this.time = time;
    }

    public String getArea() {
        return area == null ? "" : area;
    }

    public void setArea(String area) {
        if (area != null && !area.equals("null"))
            this.area = area;
    }

    public String getYxAccount() {
        return yxAccount == null ? "" : yxAccount;
    }

    public void setYxAccount(String yxAccount) {
        if (yxAccount != null && !yxAccount.equals("null"))
            this.yxAccount = yxAccount;
    }
}
