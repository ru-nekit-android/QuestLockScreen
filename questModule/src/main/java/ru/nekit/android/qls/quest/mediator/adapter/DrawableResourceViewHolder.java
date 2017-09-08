package ru.nekit.android.qls.quest.mediator.adapter;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import ru.nekit.android.qls.R;

public class DrawableResourceViewHolder extends RecyclerView.ViewHolder implements IContentContainerViewHolder {

    private final View mView, mImageView, mContentContainer;

    DrawableResourceViewHolder(@NonNull View view) {
        super(view);
        mView = view;
        mContentContainer = view.findViewById(R.id.container_content);
        mImageView = view.findViewById(R.id.view_image);
    }

    @NonNull
    public View getView() {
        return mView;
    }

    @Override
    public View getContentContainer() {
        return mContentContainer;
    }

    void setImageResource(@DrawableRes int imageResource) {
        ((AppCompatImageView) mImageView).setImageResource(imageResource);
    }
}