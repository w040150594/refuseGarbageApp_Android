package com.example.refuseclassification.mainfragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.refuseclassification.Activity.ErrorProneActivity;
import com.example.refuseclassification.Activity.SearchActivity;
import com.example.refuseclassification.Database.KnowledgeDatabase;
import com.example.refuseclassification.R;
import com.example.refuseclassification.WasteAdapter;
import com.example.refuseclassification.WasteItem;
import com.example.refuseclassification.setTitleCenter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

  private Toolbar toolbar;
  private ImageButton errorProne_button;
  private EditText search;
  private RecyclerView recyclerView;
  private WasteAdapter adapter;
  private Gson gson;
  private List<WasteItem> wasteItems;


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.frag_home, container, false);

    toolbar = view.findViewById(R.id.home_toolbar);
    toolbar.setTitle("首页");
    new setTitleCenter().setTitleCenter(toolbar);
    new KnowledgeDatabase().setKnowledgeDatabase();

    //易错练习
    errorProne_button = view.findViewById(R.id.errorProne_button);
    errorProne_button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getActivity(), ErrorProneActivity.class);
        startActivity(intent);
      }
    });
    //热搜列表
    recyclerView = view.findViewById(R.id.recycler_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    gson = new Gson();
    wasteItems = new ArrayList<>();
    adapter = new WasteAdapter(wasteItems);
    recyclerView.setAdapter(adapter);
    fetchWasteData();
    //搜索
    search = view.findViewById(R.id.searchHome);
    search.setFocusable(false);
    search.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        startActivity(intent);
      }
    });

    return view;
  }

  public void fetchWasteData() {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
            .url("https://apis.tianapi.com/hotlajifenlei/index?key=facd5f5a85d8c96d3d50a08127975813")
            .build();

    client.newCall(request).enqueue(new Callback() {
      @Override
      public void onResponse(Call call, Response response) throws IOException {
        if (response.isSuccessful()) {
          String responseBody = response.body().string();
          Gson gson = new Gson();
          Type type = new TypeToken<ResponseData>() {
          }.getType();
          ResponseData responseData = gson.fromJson(responseBody, type);
          if (responseData != null && responseData.code == 200) {
            final List<WasteItem> items = responseData.result.list;
            Collections.sort(items); // 按照热度排序
            getActivity().runOnUiThread(new Runnable() {
              @Override
              public void run() {
                if (adapter != null && recyclerView != null) {
                  // 更新adapter实例
                  adapter = new WasteAdapter(items);
                  recyclerView.setAdapter(adapter);
                }
              }
            });
          }
        }
      }

      @Override
      public void onFailure(Call call, IOException e) {
        e.printStackTrace();
      }
    });
  }

  class ResponseData {
    int code;
    String msg;
    ResultData result;
  }

  class ResultData {
    List<WasteItem> list;
  }
}








