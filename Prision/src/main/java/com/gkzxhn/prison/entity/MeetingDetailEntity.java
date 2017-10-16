package com.gkzxhn.prison.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Raleigh.Luo on 17/4/13.
 */

public class MeetingDetailEntity {
    String name;
    String uuid;
    String relationship;
    @SerializedName("image_url")
    String imageUrl;
    String accid;
    @SerializedName("prisoner_number")
    String prisonerNumber;//囚号

    public String getPrisonerNumber() {
        return prisonerNumber == null ? "" : prisonerNumber;
    }

    public void setPrisonerNumber(String prisonerNumber) {
        if (prisonerNumber != null && !prisonerNumber.equals("null"))
            this.prisonerNumber = prisonerNumber;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        if (name != null && !name.equals("null"))
            this.name = name;
    }

    public String getUuid() {
        return uuid == null ? "" : uuid;
    }

    public void setUuid(String uuid) {
        if (uuid != null && !uuid.equals("null"))
            this.uuid = uuid;
    }

    public String getRelationship() {
        return relationship == null ? "" : relationship;
    }

    public void setRelationship(String relationship) {
        if (relationship != null && !relationship.equals("null"))
            this.relationship = relationship;
    }

    public String getImageUrl() {
        return imageUrl == null ? "" : imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        if (imageUrl != null && !imageUrl.equals("null"))
            this.imageUrl = imageUrl;
    }

    public String getAccid() {
        return accid == null ? "" : accid;
    }

    public void setAccid(String accid) {
        if (accid != null && !accid.equals("null"))
            this.accid = accid;
    }
}
