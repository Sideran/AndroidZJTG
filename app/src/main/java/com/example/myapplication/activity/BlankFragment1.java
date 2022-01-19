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
import com.example.myapplication.MainActivity;
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
 * Use the {@link BlankFragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment1 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private final String TAG="getArticle";
    private String mParam1;
    private String mParam2;
    private List<Article> UchatMessageList,unRead,Read;
    private Datapp app;
    private ListViewAdapter adapter;
    public BlankFragment1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BlankFragment1 newInstance(String param1, String param2) {
        BlankFragment1 fragment = new BlankFragment1();
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
        UchatMessageList=app.getUnReadList();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_blank1, container, false);
        Bundle bundle=new Bundle();
        ListView listMessage=view.findViewById(R.id.UnReadList);
        adapter=new ListViewAdapter(getActivity(),UchatMessageList);
        listMessage.setAdapter(adapter);
        listMessage.setOnItemClickListener((parent, view0, position, id) -> {
    //            Toast.makeText(getActivity(),"点击位置"+id,Toast.LENGTH_SHORT).show();
            bundle.putInt("position",position);
            bundle.putInt("ArticleStatus",0);//0是未读
            Intent intent = new Intent(getActivity(),MessageActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
//        getString();
        UchatMessageList=app.getUnReadList();
        adapter.notifyDataSetChanged();



    }


}