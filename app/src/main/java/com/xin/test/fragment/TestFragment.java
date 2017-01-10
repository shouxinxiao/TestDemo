package com.xin.test.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xin.test.R;
import com.xin.test.bean.TestBean;
import com.xin.test.bean.TestBean.row;
import com.xin.test.utils.ImageLoader;
import com.xin.test.utils.OkHttpUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by Sean on 2017/1/9.
 */
public class TestFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    RefreshAdapter refreshAdapter;
    LinearLayoutManager mLayoutManager;
    int lastVisibleItem;
    private List<row> mList;

    private TestBean mTestBean;

    public static Fragment newIntener() {
        return new TestFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                OkHttpUtils.get("http://thoughtworks-ios.herokuapp.com/facts.json", new OkHttpUtils.ResultCallback<TestBean>() {
                    @Override
                    public void onSuccess(TestBean response) {
                        mTestBean = response;
                        Log.d("TAG","从网上获取到数据立刻:"+mTestBean.getTitle());
                        updataUI();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();

    }

    private void updataUI() {
        if (mTestBean != null) {
            mList = mTestBean.getRows();
            refreshAdapter = new RefreshAdapter(mList);
            mRecyclerView.setAdapter(refreshAdapter);
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.getSupportActionBar().setTitle(mTestBean.getTitle());
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.test_fragment, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.red,
                R.color.green,
                R.color.blue,
                R.color.yellow);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mSwipeRefreshLayout.setOnRefreshListener(this);


        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView,
                                             int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == refreshAdapter.getItemCount()) {
                    // 此处在现实项目中，请换成网络请求数据代码，sendRequest .....
                    simulateLoadMoreData();

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return view;
    }


    private void simulateLoadMoreData() {
        Observable
                .timer(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .map(new Func1<Long, Object>() {
                    @Override
                    public Object call(Long aLong) {
                        loadMoreData();
                        refreshAdapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "Load Finished!", Toast.LENGTH_SHORT).show();
                        return null;
                    }
                }).subscribe();
    }

    private void loadMoreData() {   //上拉加载更多

    }


    @Override
    public void onRefresh() {
        Observable
                .timer(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .map(new Func1<Long, Object>() {
                    @Override
                    public Object call(Long aLong) {
                        fetchingNewData();
                        mSwipeRefreshLayout.setRefreshing(false);
                        refreshAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "Refresh Finished!", Toast.LENGTH_SHORT).show();
                        return null;
                    }
                }).subscribe();
    }

    private void fetchingNewData() {    //下拉刷新
//        mList.add(0, "下拉刷新出来的数据");
    }

    public class RefreshAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<row> list;

        private static final int TYPE_ITEM = 0;
        private static final int TYPE_FOOTER = 1;


        public RefreshAdapter(List<row> data) {
            list = data;
        }

        // RecyclerView的count设置为数据总条数+ 1（footerView）
        @Override
        public int getItemCount() {
            return list.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            // 最后一个item设置为footerView
            if (position + 1 == getItemCount()) {
                return TYPE_FOOTER;
            } else {
                return TYPE_ITEM;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof ItemViewHolder) {
                ((ItemViewHolder) holder).bindData(list.get(position));
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM) {
                View view = LayoutInflater.from(parent.getContext()).inflate(
                       R.layout.list_item_recycler_view, null);
                return new ItemViewHolder(view);
            }
            // type == TYPE_FOOTER 返回footerView
            else if (viewType == TYPE_FOOTER) {
                View view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.footer_view, null);
                view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT));
                return new FooterViewHolder(view);
            }

            return null;
        }

        class FooterViewHolder extends RecyclerView.ViewHolder {

            public FooterViewHolder(View view) {
                super(view);
            }

        }

        class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView tv_title;
            TextView tv_info;
            ImageView icon;
            private row data;

            public ItemViewHolder(View view) {
                super(view);
                tv_title = (TextView) view.findViewById(R.id.tv_title);
                tv_info = (TextView) view.findViewById(R.id.tv_info);
                icon = (ImageView) view.findViewById(R.id.icon);
            }

            public void bindData(row s) {
                this.data = s;
                tv_title.setText(data.getTitle());
                tv_info.setText(data.getDescription());
                //图片加载
                ImageLoader.loadImage(getActivity(),data.getImageHref(),icon);
            }

            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "你点击了:" + data, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
