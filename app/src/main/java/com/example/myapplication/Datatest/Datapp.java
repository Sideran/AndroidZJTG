package com.example.myapplication.Datatest;

import android.app.Application;

import com.example.myapplication.modle.Article;
import com.example.myapplication.modle.ChatMessage;

import java.util.List;


public class Datapp extends Application {


    private List<Article> RecentReadList;
    private List<Article> UnReadList;
    private Article msg;
    private List<Article> chatMessageList;
    private List<Article> ReadList;


    public List<Article> getRecentReadList() {
        return RecentReadList;
    }

    public void setRecentReadList(List<Article> recentReadList) {
        RecentReadList = recentReadList;
    }

    public List<Article> getReadList() {
        return ReadList;
    }

    public void setReadList(List<Article> readList) {
        ReadList = readList;
    }

    public List<Article> getUnReadList() {
        return UnReadList;
    }

    public void setUnReadList(List<Article> unReadList) {
        UnReadList = unReadList;
    }



    public List<Article> getChatMessageList() {
        return chatMessageList;
    }

    public void setChatMessageList(List<Article> chatMessageList) {
        this.chatMessageList = chatMessageList;
    }

    public Article getMsg() {
        return msg;
    }

    public void setMsg(Article msg) {
        this.msg = msg;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

}
