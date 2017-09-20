package ru.nekit.android.qls.quest.mediator.colors;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.mediator.shared.adapter.SquareItemAdapter;
import ru.nekit.android.qls.quest.types.model.ColorModel;
import ru.nekit.android.qls.utils.IViewHolder;

class ColorsAdapter extends SquareItemAdapter<ColorsAdapter.ColorsViewHolder> {

    @NonNull
    private final QuestContext mQuestContext;
    @NonNull
    private final View.OnClickListener mClickListener;
    @NonNull
    private final List<ColorModel> mColorListData;

    ColorsAdapter(@NonNull QuestContext questContext,
                  @NonNull List<ColorModel> colorListData,
                  @NonNull View.OnClickListener clickListener) {
        mQuestContext = questContext;
        mColorListData = colorListData;
        mClickListener = clickListener;
    }

    @Override
    public ColorsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ColorsViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ill_color, parent, false));
    }

    @Override
    public void onViewDetachedFromWindow(ColorsViewHolder holder) {
        holder.getView().setOnClickListener(null);
    }

    @Override
    public void onBindViewHolder(final ColorsViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ColorModel colorModel = mColorListData.get(position);
        holder.getView().setBackgroundColor(colorModel.getColor(mQuestContext));
        holder.getView().setOnClickListener(mClickListener);
        holder.getView().setTag(colorModel.getId());
    }

    @Override
    public int getItemCount() {
        return mColorListData.size();
    }

    static class ColorsViewHolder extends RecyclerView.ViewHolder implements IViewHolder {

        @NonNull
        private final View mView;

        ColorsViewHolder(@NonNull View view) {
            super(view);
            mView = view;
        }

        @NonNull
        @Override
        public View getView() {
            return mView;
        }
    }
}