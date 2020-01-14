package com.wjy.mytv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wjy.
 * Date: 2020/1/13
 * Time: 15:26
 * Describe: 电视台列表
 */
public class TVListActivity extends Activity {

    private ListView listView;
    private List<TVListEntity> tv_list = new ArrayList<>();
    private MyTVListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvlist);
        getTVListInfo();
        initView();
    }

    private void initView(){
        listView = findViewById(R.id.listView);
        adapter = new MyTVListAdapter(TVListActivity.this,tv_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TVListActivity.this,MainActivity.class);
                intent.putExtra("title",tv_list.get(position).getTitle());
                intent.putExtra("url",tv_list.get(position).getUrl());
                startActivity(intent);
            }
        });
    }

    // 获取电视台列表信息
    private void getTVListInfo() {
        String TVList_str = FileUtil.readAssets(TVListActivity.this, "livesource.json");
        if (null != tv_list && tv_list.size() == 0)
            tv_list = getJSONParserResult(TVList_str);
    }

    public List<TVListEntity> getJSONParserResult(String JSONString){
        List<TVListEntity> tvList = new ArrayList<TVListEntity>();
        try {
            JSONArray jsonArray = new JSONArray(JSONString);
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String title = jsonObject.optString("title");
                String url = jsonObject.optString("url");

                TVListEntity tvListEntity = new TVListEntity();
                tvListEntity.setTitle(title);
                tvListEntity.setUrl(url);
                tvList.add(tvListEntity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tvList;
    }
}
