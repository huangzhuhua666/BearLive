package com.hzh.bearlive.bean;

/**
 * 直播房间信息
 */
public class RoomInfo {

    private int roomId;
    private String userId;
    private String userNickname;
    private String userAvatar;
    private String liveCover;
    private String liveTitle;
    private int watcherNum;

    public RoomInfo() {

    }

    public RoomInfo(int roomId, String userId, String userName, String userAvatar,
                    String liveCover, String liveTitle, int watcherNum) {
        this.roomId = roomId;
        this.userId = userId;
        this.userNickname = userName;
        this.userAvatar = userAvatar;
        this.liveCover = liveCover;
        this.liveTitle = liveTitle;
        this.watcherNum = watcherNum;

    }

    public int getRoomId() {
        return roomId;

    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;

    }

    public String getUserId() {
        return userId;

    }

    public void setUserId(String userId) {
        this.userId = userId;

    }

    public String getUserNickname() {
        return userNickname;

    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;

    }

    public String getUserAvatar() {
        return userAvatar;

    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;

    }

    public String getLiveCover() {
        return liveCover;

    }

    public void setLiveCover(String liveCover) {
        this.liveCover = liveCover;

    }

    public String getLiveTitle() {
        return liveTitle;

    }

    public void setLiveTitle(String liveTitle) {
        this.liveTitle = liveTitle;

    }

    public int getWatcherNum() {
        return watcherNum;

    }

    public void setWatcherNum(int watcherNum) {
        this.watcherNum = watcherNum;

    }
}
