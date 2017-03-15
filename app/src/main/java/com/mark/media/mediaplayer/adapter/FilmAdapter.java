package com.mark.media.mediaplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mark.media.mediaplayer.R;
import com.mark.media.mediaplayer.widget.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import com.mark.media.mediaplayer.bean.FilmBean;

/**
 * Adapter
 * Created by mark on 2016/12/16.
 */

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.FilmViewHolder> {
    private Context mContext;
    private List<FilmBean.DataBean.MoviesBean> mData;
    private LayoutInflater mInflater;

    /**
     * 构造方法
     *
     * @param context    上下文
     * @param moviesData 电影信息数据
     *
     */
    public FilmAdapter(Context context, List<FilmBean.DataBean.MoviesBean> moviesData) {
        this.mContext = context;
        this.mData = moviesData;

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public FilmAdapter.FilmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.list_item, parent, false);
        FilmViewHolder holder = new FilmViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(FilmAdapter.FilmViewHolder holder, int position) {
        // 设置标题
        holder.getTitleTextView().setText(mData.get(position).nm);
        // 设置图片
        Picasso.with(mContext).load(mData.get(position).img).fit().priority(Picasso.Priority.HIGH).into(holder.getImageView());
        // 主演
        holder.getActorTextView().setText("主演:" + mData.get(position).star);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class FilmViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private ImageView mImageView;
        private TextView mActorTextView;
        private final LinearLayout mRootView;

        public FilmViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.card_title);
            mImageView = (ImageView) itemView.findViewById(R.id.item_recreation_iamge);
            mActorTextView = (TextView) itemView.findViewById(R.id.card_actor);

            mRootView = (LinearLayout) itemView.findViewById(R.id.film_ll);
            mRootView.setOnClickListener(this);
        }

        public TextView getTitleTextView() {
            return mTitleTextView;
        }

        public ImageView getImageView() {
            return mImageView;
        }

        public TextView getActorTextView() {
            return mActorTextView;
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
