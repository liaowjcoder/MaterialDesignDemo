package storage.zeal.com.smartrefreshlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import storage.zeal.com.smartrefreshlayout.view.SmartFreshLayout2;

public class MainActivity extends AppCompatActivity implements  SmartFreshLayout2.RefreshListener {
    private List<String> datas = new ArrayList<>();

    private RecyclerView rv;

    private MyAdapter myAdapter;

    private SmartFreshLayout2 mSmartLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addData();
        rv = (RecyclerView) findViewById(R.id.rv);
        mSmartLayout = (SmartFreshLayout2) findViewById(R.id.smart_refresh_layout);
        rv.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter(datas);
        rv.setAdapter(myAdapter);

        mSmartLayout.setmOnRefreshListener(this);
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
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mSmartLayout.setRefresh(false);
//            }
//        }, 3000);
        Toast.makeText(this, "开始刷新", Toast.LENGTH_SHORT).show();
    }
}
