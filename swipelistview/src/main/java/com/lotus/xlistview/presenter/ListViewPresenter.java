package com.lotus.xlistview.presenter;

import android.view.View;

import com.lotus.xlistview.XListView;
import com.lotus.xlistview.entity.IListBean;
import com.lotus.xlistview.entity.IListViewListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by thl on 2017/11/3.
 */

public class ListViewPresenter<T extends IListBean> implements XListView.IXListViewListener {

    private List<T> listBeens;
    private XListView xListView;
    private View noDataView;
    private IListViewListener listViewListener;

    public int pageIndex=1;
    //小于10条不允许加载更多
    private int minSum=10;

    public ListViewPresenter(XListView listView){
        this(listView,null,null);
    }

    public ListViewPresenter(XListView listView,IListViewListener listViewListener){
        this(listView,null,listViewListener);
    }

    public ListViewPresenter(XListView listView,View noDataView,IListViewListener listViewListener){
        this.xListView=listView;
        this.noDataView=noDataView;
        this.listViewListener=listViewListener;
        listBeens=new ArrayList<>();
        initXListView(xListView);
    }

    public void  initXListView(XListView listView) {
        listView.setPullRefreshEnable(true);
        listView.setPullLoadEnable(false);
        listView.setAutoLoadEnable(true);
        listView.setXListViewListener(this);
        listView.setRefreshTime(getTime());
    }

    public void autoRefresh(){
        xListView.autoRefresh();
    }

    private void checkData() {
        if (noDataView!=null){
            boolean isNoData = listBeens.size() == 0;
            noDataView.setVisibility(isNoData ? View.VISIBLE : View.GONE);
        }
    }

    public void addListData(boolean isRefresh, List<T> result) {
        if (isRefresh) {
            this.listBeens.clear();
            this.listBeens.addAll(result);
            if (this.listBeens.size()<minSum){
                xListView.setPullLoadEnable(false);
            }else {
                xListView.setPullLoadEnable(true);
            }
        } else {
            this.listBeens.addAll(result);
        }
    }

    public void setLoadMoreMinSumEnable(int sum){
        this.minSum=sum;
    }

    public void onCompleteRefresh() {
        xListView.stopRefresh();
        xListView.stopLoadMore();
        xListView.setRefreshTime(getTime());
        checkData();
    }

    public void onCompleteFailure() {
        xListView.stopRefresh();
        xListView.stopLoadMore();
        pageIndex=pageIndex-1;
        if (pageIndex<=0){
            pageIndex=1;
        }
        checkData();
    }

    private String getTime() {  
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
    }

    @Override
    public void onRefresh() {
        pageIndex=1;
        if (listViewListener!=null){
            listViewListener.onRefreshData();
        }
    }

    @Override
    public void onLoadMore() {
        pageIndex=pageIndex+1;
        if (listViewListener!=null){
            listViewListener.onLoadMoreData();
        }
    }

    public List<T> getData(){
        return listBeens;
    }
}
