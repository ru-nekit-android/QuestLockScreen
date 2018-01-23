package ru.nekit.android.qls.quest.mediator.types.colored;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
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
import ru.nekit.android.qls.quest.mediator.adapter.SquareItemAdapter;
import ru.nekit.android.qls.quest.resources.common.IColorfullVisualResourceHolder;
import ru.nekit.android.qls.quest.resources.struct.ColorfullQuestVisualResourceStruct;
import ru.nekit.android.qls.quest.resources.struct.PairColorStruct;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static ru.nekit.android.qls.quest.resources.struct.ColorfullQuestVisualResourceStruct.ColorType.NONE;

public class ColoredVisualRepresentationQuestAdapter
        extends SquareItemAdapter<ColoredVisualRepresentationQuestAdapter.ViewHolder> {

    @NonNull
    private final QuestContext mQuestContext;
    @NonNull
    private final View.OnClickListener mClickListener;
    @NonNull
    private final List<Pair<IColorfullVisualResourceHolder, PairColorStruct>> mListData;

    ColoredVisualRepresentationQuestAdapter(@NonNull QuestContext questContext,
                                            @NonNull List<Pair<IColorfullVisualResourceHolder, PairColorStruct>> listData,
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
        holder.view.setOnClickListener(null);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Resources resources = mQuestContext.getResources();
        Pair<IColorfullVisualResourceHolder, PairColorStruct> item
                = mListData.get(position);
        for (ColorfullQuestVisualResourceStruct colorfullQuestVisualResourceStruct : item.first.getColoredVisualResourceList()) {
            int drawableResourceId = colorfullQuestVisualResourceStruct.drawableResourceId;
            if (drawableResourceId != 0) {
                AppCompatImageView imageView = new AppCompatImageView(mQuestContext);
                Drawable drawable = resources.getDrawable(colorfullQuestVisualResourceStruct.drawableResourceId);
                if (colorfullQuestVisualResourceStruct.colorType != NONE) {
                    drawable = drawable.mutate();
                    DrawableCompat.wrap(drawable);
                    PairColorStruct coloredItem = item.second;
                    @ColorInt
                    int color = 0;
                    switch (colorfullQuestVisualResourceStruct.colorType) {

                        case PRIMARY:

                            color = coloredItem.getPrimaryColorModel().getColor(mQuestContext);

                            break;

                        case SECONDARY:

                            color = coloredItem.getSecondaryColorModel().getColor(mQuestContext);

                            break;

                        case PRIMARY_INVERSE:

                            color = (0xFFFFFF - coloredItem.getPrimaryColorModel().getColor(mQuestContext)) | 0xFF000000;

                            break;

                        case SECONDARY_INVERSE:

                            color = (0xFFFFFF - coloredItem.getSecondaryColorModel().getColor(mQuestContext)) | 0xFF000000;

                            break;

                    }
                    DrawableCompat.setTint(drawable, color);
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
        holder.view.setBackgroundColor(item.second.getSecondaryColorModel().getColor(mQuestContext));
        holder.view.setOnClickListener(mClickListener);
        holder.view.setTag(item.second.getPrimaryColorModel().getId());
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @NonNull
        public final View view;

        ViewHolder(@NonNull View view) {
            super(view);
            this.view = view;
        }


        public ViewGroup getContainer() {
            return (ViewGroup) view;
        }
    }
}