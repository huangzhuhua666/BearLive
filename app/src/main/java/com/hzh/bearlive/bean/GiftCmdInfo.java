package com.hzh.bearlive.bean;

public class GiftCmdInfo {

    private int giftId;
    private String repeatId;

    public GiftCmdInfo(int giftId, String repeatId) {
        this.giftId = giftId;
        this.repeatId = repeatId;

    }

    public int getGiftId() {
        return giftId;

    }

    public String getRepeatId() {
        return repeatId;

    }
}
