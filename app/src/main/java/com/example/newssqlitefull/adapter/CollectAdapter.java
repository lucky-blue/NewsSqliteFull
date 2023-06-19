package com.example.newssqlitefull.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newssqlitefull.R;
import com.example.newssqlitefull.bean.Collect;
import com.example.newssqlitefull.bean.News;

import java.util.ArrayList;
import java.util.List;

public class CollectAdapter extends RecyclerView.Adapter<CollectAdapter.ViewHolder> {
    private List<News> list =new ArrayList<>();
    private Context mActivity;
    private ItemListener mItemListener;
    private LinearLayout llEmpty;
    private RecyclerView rvNewsList;
    public void setItemListener(ItemListener itemListener){
        this.mItemListener = itemListener;
    }
    public CollectAdapter(LinearLayout llEmpty, RecyclerView rvNewsList){
        this.llEmpty = llEmpty;
        this.rvNewsList =rvNewsList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mActivity = viewGroup.getContext();
        View view= LayoutInflater.from(mActivity).inflate(R.layout.item_rv_collect_list,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        News news = list.get(i);
        if (news != null) {
            viewHolder.title.setText(news.getTitle());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemListener!=null){
                        mItemListener.ItemClick(news);
                    }
                }
            });
        }
    }
    public void addItem(List<News> listAdd) {
        //如果是加载第一页，需要先清空数据列表
        this.list.clear();
        if (listAdd!=null){
            //添加数据
            this.list.addAll(listAdd);
        }
        //通知RecyclerView进行改变--整体
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
        }
    }
    public interface ItemListener{
        void ItemClick(News news);
    }
}
