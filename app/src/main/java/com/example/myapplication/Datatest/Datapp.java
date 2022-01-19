package com.example.myapplication.Datatest;

import android.app.Application;
import android.content.SharedPreferences;

import com.example.myapplication.modle.Article;
import com.example.myapplication.modle.ChatMessage;
import com.example.myapplication.modle.UserInfo;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


public class Datapp extends Application {


    private List<Article> UnReadList;
    private Article msg;
    private List<Article> chatMessageList;
    private List<Article> ReadList;
    private String URL,articleId,Token;
    private HashSet<Long> RecentSet;
    private UserInfo user;
    private SharedPreferences sp;

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public HashSet<Long> getRecentSet() {
        return RecentSet;
    }

    public void setRecentSet(HashSet<Long> recentSet) {
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


    public List<Article> getReadList() {
        return ReadList;
    }

    public void clearReadList(){
        this.ReadList.clear();
    }
    public void clearUnReadList(){
        this.UnReadList.clear();
    }
    public void clearRecentSet(){
        this.RecentSet.clear();
    }
    public void ClearAll(){
        clearReadList();
        clearRecentSet();
        clearUnReadList();
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

    public void addRecentSet(Long articleId){
        RecentSet.add(articleId);
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

    public void setURL(String url) {
        this.URL = "http://"+url+":8081";
        try {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("URL",this.URL);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences("ZJTG", MODE_PRIVATE);
        ReadList=new LinkedList<>();
        UnReadList=new LinkedList<>();

        chatMessageList = new LinkedList<>();
        RecentSet= new HashSet<>();
        user=new UserInfo();
        URL=sp.getString("URL","http://172.17.33.30:8081");//"http://172.17.33.30:8081";
        Token="";
        articleId="";
    }

}
