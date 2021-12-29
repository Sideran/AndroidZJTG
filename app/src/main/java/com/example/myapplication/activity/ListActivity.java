package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.myapplication.Adapter.ListViewAdapter;
import com.example.myapplication.Datatest.Datapp;
import com.example.myapplication.R;

import com.example.myapplication.modle.ChatMessage;

import java.util.List;

public class ListActivity extends AppCompatActivity {

    private ListView listMessage;
    private List<ChatMessage> UchatMessageList;
    private Datapp app;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Bundle bundle= new Bundle();
        app=(Datapp) getApplication();
//        chatMessageList= JSON.parseArray(app.getUnReadList(),ChatMessage.class);
        UchatMessageList=app.getUnReadList();
        listMessage.setAdapter(new ListViewAdapter(ListActivity.this,UchatMessageList));
        listMessage.setOnItemClickListener((parent, view, position, id) -> {
            Toast.makeText(ListActivity.this,"点击位置"+id,Toast.LENGTH_SHORT).show();
//
            bundle.putInt("position",position);
            Intent intent = new Intent(ListActivity.this,MessageActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        listMessage.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //设置一个弹窗，显示是哪个被点击了
                Toast.makeText(ListActivity.this,"长按位置"+position,Toast.LENGTH_SHORT).show();
                if(position ==0) {
                    Intent intent = new Intent(ListActivity.this, GridViewActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
    }



}