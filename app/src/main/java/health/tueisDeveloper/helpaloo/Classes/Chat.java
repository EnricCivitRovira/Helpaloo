package health.tueisDeveloper.helpaloo.Classes;

import androidx.annotation.NonNull;

public class Chat {

    private String chatID;
    private String chatFromID;
    private String chatToID;
    private String chatPostID;
    private String nameFrom;
    private String nameTo;
    private String chatTitle;

    public Chat(){}

    public Chat(String chatFromID, String chatToID, String chatPostID, String nameFrom, String nameTo, String chatTitle){
        this.chatID = idChatOrganizer(chatToID, chatFromID)+ chatPostID;
        this.chatFromID = chatFromID;
        this.chatToID = chatToID;
        this.chatPostID = chatPostID;
        this.nameFrom = nameFrom;
        this.nameTo = nameTo;
        this.chatTitle = chatTitle;
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

    public String getChatPostID() {
        return chatPostID;
    }

    public void setChatPostID(String chatPostID) {
        this.chatPostID = chatPostID;
    }

    private String idChatOrganizer(String id1, String id2) {
        if(id1.compareTo(id2)>0){
            return id1+id2;
        }else{
            return id2+id1;
        }
    }

    public String getNameFrom() {
        return nameFrom;
    }

    public void setNameFrom(String nameFrom) {
        this.nameFrom = nameFrom;
    }

    public String getNameTo() {
        return nameTo;
    }

    public void setNameTo(String nameTo) {
        this.nameTo = nameTo;
    }

    public String getChatTitle() {
        return chatTitle;
    }

    public void setChatTitle(String chatTitle) {
        this.chatTitle = chatTitle;
    }

    @NonNull
    @Override
    public String toString() {
        return "Chat{" +
                "chatID='" + chatID + '\'' +
                ", chatFromID='" + chatFromID + '\'' +
                ", chatToID='" + chatToID + '\'' +
                ", chatPostID='" + chatPostID + '\'' +
                ", nameFrom='" + nameFrom + '\'' +
                ", nameTo='" + nameTo + '\'' +
                ", chatTitle='" + chatTitle + '\'' +
                '}';
    }
}
