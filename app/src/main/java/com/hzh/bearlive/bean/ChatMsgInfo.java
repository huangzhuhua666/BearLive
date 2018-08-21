package com.hzh.bearlive.bean;

/**
 * 聊天信息
 */
public class ChatMsgInfo {

    private static final String TYPE_LIST = "list";
    private static final String TYPE_DANMU = "danmu";

    private String msgType = TYPE_LIST;
    private String content;
    private String id;
    private String avatar;
    private String name;

    private ChatMsgInfo(String content, String id, String avatar, String name) {
        this.content = content;
        this.id = id;
        this.avatar = avatar;
        this.name = name;

    }

    public static ChatMsgInfo createListInfo(String content, String id, String avatar) {
        ChatMsgInfo msg = new ChatMsgInfo(content, id, avatar, "");
        msg.msgType = TYPE_LIST;
        return msg;

    }

    public static ChatMsgInfo createDanmuInfo(String content, String id, String avatar, String name) {
        ChatMsgInfo msg = new ChatMsgInfo(content, id, avatar, name);
        msg.msgType = TYPE_DANMU;
        return msg;

    }

    public String getMsgType() {
        return msgType;

    }

    public String getContent() {
        return content;

    }

    public String getId() {
        return id;

    }

    public String getAvatar() {
        return avatar;

    }

    public String getName() {
        return name;

    }
}
