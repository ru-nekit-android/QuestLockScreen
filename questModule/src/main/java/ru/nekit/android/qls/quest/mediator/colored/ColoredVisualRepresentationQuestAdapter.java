package ru.nekit.android.qls.quest.mediator.colored;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.mediator.shared.adapter.SquareItemAdapter;
import ru.nekit.android.qls.quest.resourceLibrary.ColoredVisualResourceItem;
import ru.nekit.android.qls.quest.resourceLibrary.IColoredVisualResourceItemList;
import ru.nekit.android.qls.quest.types.model.ContentAndBackgroundColoredModel;
import ru.nekit.android.qls.utils.IViewHolder;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static ru.nekit.android.qls.quest.resourceLibrary.ColoredVisualResourceItem.ColorType.COLOR_AS_BACKGROUND;
import static ru.nekit.android.qls.quest.resourceLibrary.ColoredVisualResourceItem.ColorType.COLOR_AS_CONTENT;
import static ru.nekit.android.qls.quest.resourceLibrary.ColoredVisualResourceItem.ColorType.NONE;

public class ColoredVisualRepresentationQuestAdapter
        extends SquareItemAdapter<ColoredVisualRepresentationQuestAdapter.ViewHolder> {

    @NonNull
    private final QuestContext mQuestContext;
    @NonNull
    private final View.OnClickListener mClickListener;
    @NonNull
    private final List<Pair<IColoredVisualResourceItemList, ContentAndBackgroundColoredModel>> mListData;

    ColoredVisualRepresentationQuestAdapter(@NonNull QuestContext questContext,
                                            @NonNull List<Pair<IColoredVisualResourceItemList, ContentAndBackgroundColoredModel>> listData,
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
        Pair<IColoredVisualResourceItemList, ContentAndBackgroundColoredModel> item
                = mListData.get(position);
        for (ColoredVisualResourceItem coloredVisualResourceItem : item.first.getColoredVisualResourceItemList()) {
            int drawableResourceId = coloredVisualResourceItem.drawableResourceId;
            if (drawableResourceId != 0) {
                AppCompatImageView imageView = new AppCompatImageView(mQuestContext);
                ColoredVisualResourceItem.ColorType colorType = coloredVisualResourceItem.colorType;
                Drawable drawable = resources.getDrawable(coloredVisualResourceItem.drawableResourceId);
                if (colorType != NONE) {
                    drawable = drawable.mutate();
                    DrawableCompat.wrap(drawable);
                    ContentAndBackgroundColoredModel coloredModel = item.second;
                    if (colorType == COLOR_AS_CONTENT) {
                        DrawableCompat.setTint(drawable,
                                coloredModel.getContentColorModel().getColor(mQuestContext));
                    } else if (colorType == COLOR_AS_BACKGROUND) {
                        DrawableCompat.setTint(drawable,
                                coloredModel.getBackgroundColorModel().getColor(mQuestContext));
                    }
                }
                imageView.setImageDrawable(drawable);
                RelativeLayout.LayoutParams layoutParams =
                        new RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                imageView.setLayoutParams(layoutParams);
                imageView.requestFocus();
                holder.getContainer().addView(imageView);
            }
        }
        holder.getView().setBackgroundColor(item.second.getBackgroundColorModel().getColor(mQuestContext));
        holder.getView().setOnClickListener(mClickListener);
        holder.getView().setTag(item.second.getContentColorModel().getId());
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements IViewHolder {

        @NonNull
        private final View mView;

        ViewHolder(@NonNull View view) {
            super(view);
            mView = view;
        }

        @NonNull
        @Override
        public View getView() {
            return mView;
        }

        public ViewGroup getContainer() {
            return (ViewGroup) getView();
        }
    }
}