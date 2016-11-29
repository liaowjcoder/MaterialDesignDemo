package com.zeal.bottomsheetdemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zeal on 16/11/24.
 */

public class BottomSheetDilogDemo extends AppCompatActivity {
    private static final String TAG = "zeal";

    private List<Model> mDatas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_botton_sheet_dialog);


        initData();

        TextView textView = (TextView) findViewById(R.id.tv);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialg();
                //showBottomSheetDialogFragment();
            }

        });
    }

    private void initData() {
        mDatas = new ArrayList<>();
        Model model1 = new Model(R.drawable.a4, "Gmail");
        mDatas.add(model1);

        Model model2 = new Model(R.drawable.a7, "Facebook");
        mDatas.add(model2);

        Model model3 = new Model(R.drawable.a5, "Wechat");
        mDatas.add(model3);
        Model model4 = new Model(R.drawable.a7, "Wechat");
        mDatas.add(model4);
        Model model5 = new Model(R.drawable.a7, "Wechat");
        mDatas.add(model5);
        Model model6 = new Model(R.drawable.a7, "Wechat");
        mDatas.add(model6);
        Model model7 = new Model(R.drawable.a7, "Facebook");
        mDatas.add(model7);
    }

    private void showDialg() {

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = View.inflate(this, R.layout.layout_dialog, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setAdapter(new MyAdapter());
        recyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.bottomMargin = 200;
        dialog.setContentView(view,lp);
        dialog.show();


        //设置一些而外的属性
        dialog.setTitle("title");
        //dialog.setCanceledOnTouchOutside(false);
        //dialog.setCancelable(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Toast.makeText(BottomSheetDilogDemo.this, "onCancel", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Toast.makeText(BottomSheetDilogDemo.this, "onDismiss", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showBottomSheetDialogFragment() {
        new BottomSheetDilogFragmentDemo().show(getSupportFragmentManager(), "dialog");
    }


    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout view = (LinearLayout) View.inflate(parent.getContext(), R.layout.share_item2, null);
            MyViewHolder holder = new MyViewHolder(view);


            //Log.e(TAG, "onCreateViewHolder: "+parent.getContext().getResources().getDisplayMetrics().density);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.tv_desc.setText(mDatas.get(position).desc);
            holder.iv_icon.setImageResource(mDatas.get(position).resId);
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_desc;
        public ImageView iv_icon;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_desc = (TextView) itemView.findViewById(R.id.tv_desc);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
        }


    }

}
