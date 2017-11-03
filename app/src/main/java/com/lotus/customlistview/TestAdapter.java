package com.lotus.customlistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lotus.xlistview.swipe.SwipeView;

import java.util.List;

/**
 * Created by thl on 2017/11/3.
 */

public class TestAdapter extends BaseAdapter {

    private List<TestBean>  testBeens;
    private Context mContext;

    public TestAdapter(Context context,List<TestBean>  testBeens){
        this.testBeens=testBeens;
        this.mContext=context;

    }
    @Override
    public int getCount() {
        return testBeens.size();
    }

    @Override
    public Object getItem(int position) {
        return testBeens.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.item_swipe,null,false);
            holder=new ViewHolder();
            holder.swipeView=(SwipeView)convertView.findViewById(R.id.swipeview);
            holder.text11=(TextView) convertView.findViewById(R.id.text11);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }

        TestBean testBean=testBeens.get(position);
        holder.text11.setText(testBean.getName());
        holder.swipeView.reset();
        if (position%2==0){
            holder.swipeView.enableSlide(true);
        }else {
            holder.swipeView.enableSlide(false);
        }

        return convertView;
    }

    static class ViewHolder{
        private SwipeView swipeView;
        private TextView text11;
    }
}
