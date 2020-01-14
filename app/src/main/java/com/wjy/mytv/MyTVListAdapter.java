package com.wjy.mytv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wjy.
 * Date: 2020/1/13
 * Time: 15:50
 * Describe: 电视台列表 adapter
 */
public class MyTVListAdapter extends BaseAdapter {

    private Context context;
    private List<TVListEntity> tv_list = new ArrayList<>();

    public MyTVListAdapter(Context context,List<TVListEntity> tv_list){
        this.context = context;
        this.tv_list = tv_list;
    }

    @Override
    public int getCount() {
        return tv_list.size();
    }

    @Override
    public Object getItem(int position) {
        return tv_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_tvlist,null);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_title.setText(tv_list.get(position).getTitle());
        return convertView;
    }

    public static final class ViewHolder {
        private TextView tv_title;//电视台名称
    }
}
