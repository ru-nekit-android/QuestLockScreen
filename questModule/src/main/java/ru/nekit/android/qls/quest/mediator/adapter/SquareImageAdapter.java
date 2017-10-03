package ru.nekit.android.qls.quest.mediator.adapter;

import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.mediator.IContentContainerViewHolder;

public class SquareImageAdapter extends SquareItemAdapter<SquareImageAdapter.ImageViewHolder> {

    @LayoutRes
    private final int mImageLayoutResId;
    @NonNull
    private final List<?> mDataList;
    @DrawableRes
    private final List<Integer> mImageResourceIds;
    @NonNull
    private final View.OnClickListener mClickListener;

    public SquareImageAdapter(@LayoutRes int imageLayoutResId,
                              @NonNull List<?> dataList,
                              @NonNull List<Integer> imageResourceIds,
                              @NonNull View.OnClickListener clickListener) {
        mImageLayoutResId = imageLayoutResId;
        mImageResourceIds = imageResourceIds;
        mDataList = dataList;
        mClickListener = clickListener;
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        View view = holder.itemView;
        if (mClickListener != null) {
            view.setOnClickListener(mClickListener);
        }
        view.setTag(mDataList == null ? null : mDataList.get(position));
        holder.getImageView().setImageResource(mImageResourceIds.get(position));
    }

    @Override
    public void onViewDetachedFromWindow(ImageViewHolder holder) {
        holder.itemView.setOnClickListener(null);
        super.onViewDetachedFromWindow(holder);
    }


    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(mImageLayoutResId, parent, false));
    }

    @Override
    public int getItemCount() {
        return mImageResourceIds.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder
            implements IContentContainerViewHolder {

        private final View mView, mImageView, mContentContainer;

        ImageViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            mContentContainer = view.findViewById(R.id.container_content);
            mImageView = view.findViewById(R.id.view_image);
        }

        @NonNull
        public View getView() {
            return mView;
        }

        @NonNull
        @Override
        public View getContentContainer() {
            return mContentContainer;
        }

        @NonNull
        ImageView getImageView() {
            return (ImageView) mImageView;
        }
    }
}