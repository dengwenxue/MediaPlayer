package com.mark.media.mediaplayer.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mark.media.mediaplayer.R;
import com.mark.media.mediaplayer.bean.NewsBean;
import com.mark.media.mediaplayer.widget.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by mark on 2016/12/17.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<NewsBean.ResultBean> mData;

    public NewsAdapter(Context context, List<NewsBean.ResultBean> data) {
        this.mContext = context;
        this.mData = data;

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public NewsAdapter.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_news, parent, false);
        NewsViewHolder holder = new NewsViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(NewsAdapter.NewsViewHolder holder, int position) {
        // 设置头像
        Picasso.with(mContext).load(mData.get(position).picUrl).fit().priority(Picasso.Priority.HIGH).into(holder.getImageView());
        // 设置标题
        holder.getTitleTextView().setText(mData.get(position).title);
        // 设置来源
        holder.getDesTextView().setText(mData.get(position).description);
        // 设置时间
        holder.getCtimeTextView().setText(mData.get(position).ctime);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mImg;// 头像
        private final TextView mTitle;// 标题
        private final TextView mDescription;//来源
        private final TextView mCtime;// 时间
        private final CardView mRootView;

        public NewsViewHolder(View itemView) {
            super(itemView);

            mImg = (ImageView) itemView.findViewById(R.id.item_news_img);
            mTitle = (TextView) itemView.findViewById(R.id.item_news_title);
            mDescription = (TextView) itemView.findViewById(R.id.item_news_description);
            mCtime = (TextView) itemView.findViewById(R.id.item_news_time);

            mRootView = (CardView) itemView.findViewById(R.id.cardlist_item);

            mRootView.setOnClickListener(this);
        }

        public ImageView getImageView() {
            return mImg;
        }

        public TextView getTitleTextView() {
            return mTitle;
        }

        public TextView getDesTextView() {
            return mDescription;
        }

        public TextView getCtimeTextView() {
            return mCtime;
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    // 声明一个接口
    private OnItemClickListener clickListener;

    public void setOnClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

}
