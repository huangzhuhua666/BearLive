package com.hzh.bearlive.bean;

import com.hzh.bearlive.activity.R;

public class GiftInfo {

    public enum Type {
        ContinueGift, FullScreenGift
    }

    public static GiftInfo Gift_Empty = new GiftInfo(R.drawable.gift_none, 0, "",
            Type.ContinueGift, 0);
    public static GiftInfo Gift_BingGun = new GiftInfo(R.drawable.gift_1, 1, "冰棍",
            Type.ContinueGift, 1);
    public static GiftInfo Gift_BingJiLing = new GiftInfo(R.drawable.gift_2, 5, "冰激凌",
            Type.ContinueGift, 2);
    public static GiftInfo Gift_MeiGui = new GiftInfo(R.drawable.gift_3, 10, "玫瑰花",
            Type.ContinueGift, 3);
    public static GiftInfo Gift_PiJiu = new GiftInfo(R.drawable.gift_4, 15, "啤酒",
            Type.ContinueGift, 4);
    public static GiftInfo Gift_HongJiu = new GiftInfo(R.drawable.gift_5, 20, "红酒",
            Type.ContinueGift, 5);
    public static GiftInfo Gift_Hongbao = new GiftInfo(R.drawable.gift_6, 50, "红包",
            Type.ContinueGift, 6);
    public static GiftInfo Gift_ZuanShi = new GiftInfo(R.drawable.gift_7, 100, "钻石",
            Type.ContinueGift, 7);
    public static GiftInfo Gift_BaoXiang = new GiftInfo(R.drawable.gift_8, 200, "宝箱",
            Type.ContinueGift, 8);
    public static GiftInfo Gift_BaoShiJie = new GiftInfo(R.drawable.gift_9, 1000, "保时捷",
            Type.FullScreenGift, 9);

    private int giftResId;
    private int exp;
    private String name;
    private Type type;
    private int giftId;

    private GiftInfo(int giftResId, int exp, String name, Type type, int giftId) {
        this.giftResId = giftResId;
        this.exp = exp;
        this.name = name;
        this.type = type;
        this.giftId = giftId;

    }

    public static GiftInfo getGiftById(int id) {
        switch (id) {
            case 1:
                return Gift_BingGun;
            case 2:
                return Gift_BingJiLing;
            case 3:
                return Gift_MeiGui;
            case 4:
                return Gift_PiJiu;
            case 5:
                return Gift_HongJiu;
            case 6:
                return Gift_Hongbao;
            case 7:
                return Gift_ZuanShi;
            case 8:
                return Gift_BaoXiang;
            case 9:
                return Gift_BaoShiJie;
        }
        return null;

    }

    public int getGiftResId() {
        return giftResId;

    }

    public int getExp() {
        return exp;

    }

    public String getName() {
        return name;

    }

    public Type getType() {
        return type;

    }

    public int getGiftId() {
        return giftId;

    }
}
