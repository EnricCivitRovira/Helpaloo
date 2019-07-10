package com.example.helpaloo;

public class Chat {

    public String chatID;
    public String chatFromID;
    public String chatToID;
    public String chatToName;
    public String chatFromName;
    public String chatTitle;
    public String chatPostID;

    public Chat(){}

    public Chat(String chatID, String chatFromID,String chatToID, String chatFromName,  String chatTitle, String chatPostID){
        this.chatID = chatID;
        this.chatFromID = chatFromID;
        this.chatToID = chatToID;
        this.chatFromName = chatFromName;
        this.chatTitle = chatTitle;
        this.chatPostID = chatPostID;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getChatFromID() {
        return chatFromID;
    }

    public void setChatFromID(String chatFromID) {
        this.chatFromID = chatFromID;
    }

    public String getChatToID() {
        return chatToID;
    }

    public void setChatToID(String chatToID) {
        this.chatToID = chatToID;
    }

    public String getChatToName() {
        return chatToName;
    }

    public void setChatToName(String chatToName) {
        this.chatToName = chatToName;
    }

    public String getChatFromName() {
        return chatFromName;
    }

    public void setChatFromName(String chatFromName) {
        this.chatFromName = chatFromName;
    }

    public String getChatTitle() {
        return chatTitle;
    }

    public void setChatTitle(String chatTitle) {
        this.chatTitle = chatTitle;
    }

    public String getChatPostID() {
        return chatPostID;
    }

    public void setChatPostID(String chatPostID) {
        this.chatPostID = chatPostID;
    }
}
