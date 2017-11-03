package com.lotus.customlistview;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.lotus.xlistview.presenter.ListViewPresenter;
import com.lotus.xlistview.XListView;
import com.lotus.xlistview.entity.IListViewListener;
import com.lotus.xlistview.swipe.SwipeListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends FragmentActivity implements IListViewListener{

    ListViewPresenter presenter;
    private SwipeListView xListView;
    private View notDataView;
    TestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xListView=(SwipeListView)findViewById(R.id.xlistview);
        notDataView=findViewById(R.id.no_data_view);

        presenter=new ListViewPresenter<TestBean>(xListView,notDataView,this);
//        adapter = new ArrayAdapter<TestBean>(this, android.R.layout.simple_list_item_1, presenter.getData());
        adapter=new TestAdapter(this,presenter.getData());
        xListView.setAdapter(adapter);
        presenter.autoRefresh();

    }

    @Override
    public void onLoadMoreData() {
        loadData(false);
    }

    @Override
    public void onRefreshData() {
        loadData(true);
    }

    public void loadData(final boolean isRefresh){
        List<TestBean> testBeens=new ArrayList<>();
        for (int i=0;i<10;i++){
            TestBean test=new TestBean();
            test.setName("Test"+ (new Random().nextInt()*100000));
            testBeens.add(test);
        }
        presenter.addListData(isRefresh,testBeens);
        presenter.onCompleteRefresh();
        adapter.notifyDataSetChanged();
    }
}
