package com.stayhealth.ui.consultant;

public class ConsultantModel {
    private final String uid;
    private final String name;
    private final String status; // active | past
    private final int avatarRes;

    public ConsultantModel(String uid, String name, String status, int avatarRes) {
        this.uid = uid;
        this.name = name;
        this.status = status;
        this.avatarRes = avatarRes;
    }

    public String getUid() { return uid; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public int getAvatarRes() { return avatarRes; }
}
