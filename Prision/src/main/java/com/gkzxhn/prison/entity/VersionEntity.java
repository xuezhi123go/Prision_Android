package com.gkzxhn.prison.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Raleigh.Luo on 17/4/20.
 */

public class VersionEntity {
    @SerializedName("version_number")
    private int versionCode;
    @SerializedName("version_code")
    private String versionName;
    @SerializedName("download")
    private String downloadUrl;
    @SerializedName("is_force")
    private boolean isForce;

    public int getVersionCode() {
        return versionCode ;
    }

    public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName == null ? "" : versionName;
    }

    public void setVersionName(String versionName) {
        if (versionName != null && !versionName.equals("null"))
            this.versionName = versionName;
    }

    public String getDownloadUrl() {
        return downloadUrl == null ? "" : downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        if (downloadUrl != null && !downloadUrl.equals("null"))
            this.downloadUrl = downloadUrl;
    }

    public boolean isForce() {
        return isForce;
    }

    public void setForce(boolean force) {
        isForce = force;
    }
}
