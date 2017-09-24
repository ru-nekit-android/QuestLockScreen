package ru.nekit.android.qls.quest.mediator.colored;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.mediator.shared.adapter.SquareItemAdapter;
import ru.nekit.android.qls.quest.resourceLibrary.ITripleContentQuestVisualResourceItem;
import ru.nekit.android.qls.quest.types.model.ContentAndBackgroundColoredModel;
import ru.nekit.android.qls.utils.IViewHolder;

public class ColoredVisualRepresentationQuestAdapter
        extends SquareItemAdapter<ColoredVisualRepresentationQuestAdapter.ViewHolder> {

    @NonNull
    private final QuestContext mQuestContext;
    @NonNull
    private final View.OnClickListener mClickListener;
    @NonNull
    private final List<Pair<ITripleContentQuestVisualResourceItem, ContentAndBackgroundColoredModel>> mListData;

    ColoredVisualRepresentationQuestAdapter(@NonNull QuestContext questContext,
                                            @NonNull List<Pair<ITripleContentQuestVisualResourceItem, ContentAndBackgroundColoredModel>> listData,
                                            @NonNull View.OnClickListener clickListener) {
        mQuestContext = questContext;
        mListData = listData;
        mClickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ill_colored_visual_representation, parent, false));
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        holder.getView().setOnClickListener(null);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Resources resources = mQuestContext.getResources();
        Pair<ITripleContentQuestVisualResourceItem, ContentAndBackgroundColoredModel> item
                = mListData.get(position);
        Drawable backgroundDrawable = resources.getDrawable(item.first.getBackgroundDrawableResourceId()).mutate();
        DrawableCompat.wrap(backgroundDrawable);
        DrawableCompat.setTint(backgroundDrawable, item.second.getContentColorModel().getColor(mQuestContext));
        Drawable contentDrawable = resources.getDrawable(item.first.getContentDrawableResourceId());
        holder.backgroundImageView.setImageDrawable(backgroundDrawable);
        holder.contentImageView.setImageDrawable(contentDrawable);
        holder.getView().setBackgroundColor(item.second.getBackgroundColorModel().getColor(mQuestContext));
        holder.getView().setOnClickListener(mClickListener);
        holder.getView().setTag(item.second.getContentColorModel().getId());
        if (item.first.getForegroundDrawableResourceId() != 0) {
            Drawable foregroundDrawable = resources.getDrawable(item.first.getForegroundDrawableResourceId()).mutate();
            DrawableCompat.wrap(foregroundDrawable);
            DrawableCompat.setTint(foregroundDrawable, item.second.getBackgroundColorModel().getColor(mQuestContext));
            holder.foregroundImageView.setImageDrawable(foregroundDrawable);
        }
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements IViewHolder {

        @NonNull
        private final View mView;
        private final ImageView contentImageView, backgroundImageView, foregroundImageView;

        ViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            contentImageView = (ImageView) view.findViewById(R.id.image_content);
            backgroundImageView = (ImageView) view.findViewById(R.id.image_background);
            foregroundImageView = (ImageView) view.findViewById(R.id.image_foreground);
        }

        @NonNull
        @Override
        public View getView() {
            return mView;
        }
    }
}