package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.Adapter.ListViewAdapter;
import com.example.myapplication.Datatest.Datapp;
import com.example.myapplication.R;
import com.example.myapplication.modle.Article;
import com.example.myapplication.modle.ChatMessage;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BlankFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Datapp app;
    private final String TAG="getArticle";
    private List<Article> chatMessageList,unRead,Read;
    private ListViewAdapter adapter;
    public BlankFragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static BlankFragment2 newInstance(String param1, String param2) {
        BlankFragment2 fragment = new BlankFragment2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app=(Datapp)getActivity().getApplication();
        chatMessageList=app.getReadList();//此处获取未读列表测试，应该为已读列表
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_blank2, container, false);
        Bundle bundle=new Bundle();
        ListView listMessage=view.findViewById(R.id.ReadList);
        adapter=new ListViewAdapter(getActivity(),chatMessageList);
        listMessage.setAdapter(adapter);
        listMessage.setOnItemClickListener((parent, view0, position, id) -> {
    //            Toast.makeText(getActivity(),"点击位置"+id,Toast.LENGTH_SHORT).show();

            bundle.putInt("position",position);
            Intent intent = new Intent(getActivity(),MessageActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        return  view;
    }

    @Override
    public void onResume(){
        super.onResume();
        getString();


    }
    private void getString() {
        //String url = "https://wwww.baidu.com";
        String Token=app.getToken();
        String url = app.getURL()+"/learn/article/userId?userId=1";
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization","Bearer " + Token)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
                Log.e(TAG, "onFailure: "+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Intent intent = null;
                String text=response.body().string();
                Log.e(TAG, "onResponse: " + text);
                unRead=new LinkedList<>();
                Read=new LinkedList<>();
                try {//Util.decrypt(users)
                    JSONObject js= (JSONObject) JSON.parse(text);
                    JSONArray ja = js.getJSONArray("data");//JSON.parseArray(users,User.class);
                    List<Article> retList=ja.toJavaList(Article.class);
                    for(int i=0;i<retList.size();i++){
                        if(retList.get(i).getStatus().equals("1")){
                            Read.add(retList.get(i));
                        }else if(retList.get(i).getStatus().equals("0")){
                            unRead.add(retList.get(i));
                        }

                    }
                    app.setUnReadList(unRead);
                    app.setReadList(Read);
//                    Log.e(TAG, "onResponse: " +":" + articleTitle);
                    chatMessageList=app.getReadList();

                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        });
    }
}