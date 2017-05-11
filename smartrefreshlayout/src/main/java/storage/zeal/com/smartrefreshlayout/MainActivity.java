package storage.zeal.com.smartrefreshlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<String> datas = new ArrayList<>();

    private RecyclerView rv;

    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addData();
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter(datas);
        rv.setAdapter(myAdapter);
    }

    private void addData() {

        for (int i = 0; i < 100; i++) {
            datas.add("hello world " + i);
        }
    }


}
