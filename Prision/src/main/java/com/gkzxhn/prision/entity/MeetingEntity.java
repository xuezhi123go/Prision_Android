package com.gkzxhn.prision.entity;

/**
 * Created by Raleigh.Luo on 17/4/11.
 */

public class MeetingEntity {
    private String id;
    private String name;
    private String time;
    private String area;
    private String yxAccount;

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
