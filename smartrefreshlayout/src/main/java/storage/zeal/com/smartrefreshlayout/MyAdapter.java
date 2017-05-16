package storage.zeal.com.smartrefreshlayout;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * Created by liaowj on 2017/5/11.
 */

public class MyAdapter extends CommonAdapter<String> {
    public MyAdapter(Context context, int layoutId, List<String> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, String item, int position) {
        viewHolder.setText(R.id.name,item);
    }

//    private List<String> mDatas;
//
//    public MyAdapter(List<String> datas) {
//        this.mDatas = datas;
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//        View view = View.inflate(parent.getContext(), R.layout.item, null);
//        ViewHolder holder = new ViewHolder(view);
//        return holder;
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//
//        holder.name.setText(mDatas.get(position));
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return mDatas.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//
//        private TextView name;
//
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            name = (TextView) itemView.findViewById(R.id.name);
//
//        }
//    }
}
