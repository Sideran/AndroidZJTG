package com.example.myapplication.Datatest;

import android.app.Application;

import com.example.myapplication.modle.Article;
import com.example.myapplication.modle.ChatMessage;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


public class Datapp extends Application {


    private List<Article> RecentReadList;
    private List<Article> UnReadList;
    private Article msg;
    private List<Article> chatMessageList;
    private List<Article> ReadList;
    private String URL,articleId,Token;
    private HashSet<Article> RecentSet;

    public HashSet<Article> getRecentSet() {
        return RecentSet;
    }

    public void setRecentSet(HashSet<Article> recentSet) {
        RecentSet = recentSet;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

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

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ReadList=new LinkedList<>();
        UnReadList=new LinkedList<>();
        RecentReadList = new LinkedList<>();
        chatMessageList = new LinkedList<>();
        RecentSet= new HashSet<>();
        URL="http://192.168.31.184:8081";
        Token="";
        articleId="";
    }

}
