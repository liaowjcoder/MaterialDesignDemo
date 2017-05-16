package storage.zeal.com.smartrefreshlayout;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.util.ArrayList;
import java.util.List;

import storage.zeal.com.smartrefreshlayout.view.SmartFreshLayout2;

public class MainActivity extends AppCompatActivity implements SmartFreshLayout2.RefreshListener, SmartFreshLayout2.LoadMoreListener, MultiItemTypeAdapter.OnItemClickListener {
    private List<String> datas = new ArrayList<>();

    private RecyclerView rv;

    private MyAdapter myAdapter;

    private SmartFreshLayout2 mSmartLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addData();
        rv = (RecyclerView) findViewById(R.id.rv);
        mSmartLayout = (SmartFreshLayout2) findViewById(R.id.smart_refresh_layout);
        rv.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter(this,R.layout.item,datas);
        rv.setAdapter(myAdapter);

        myAdapter.setOnItemClickListener(this);

        mSmartLayout.setmOnRefreshListener(this);
        mSmartLayout.setLoadMoreListener(this);
//        mSmartLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                mSmartLayout.setRefresh(true);
//            }
//        });


    }

    private void addData() {

        for (int i = 0; i < 100; i++) {
            datas.add("hello world " + i);
        }
    }


    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSmartLayout.setRefresh(false);
            }
        }, 2000);
        Toast.makeText(this, "开始刷新", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadMore() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSmartLayout.setLoadMoreEnd();
            }
        }, 3000);
        Toast.makeText(this, "开始加载更多", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
        Toast.makeText(this, datas.get(position)+"", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
        return false;
    }
}
