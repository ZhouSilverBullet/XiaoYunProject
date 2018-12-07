package com.sdxxtop.robotproject.control;

public class MainChatControl {

    public static MainChatControl getInstance() {
        return SingleHolder.INSTANCE;
    }

    private static class SingleHolder {
        private static final MainChatControl INSTANCE = new MainChatControl();
    }

    //控制是否可以 进行下一次的聊天
    private volatile boolean isChat;

    public boolean isChat() {
        return isChat;
    }

    public void setChat(boolean chat) {
        isChat = chat;
    }

    private volatile boolean messageIsChat = true;

    public boolean isMessageIsChat() {
        return messageIsChat;
    }

    public void setMessageIsChat(boolean messageIsChat) {
        this.messageIsChat = messageIsChat;
    }
}
