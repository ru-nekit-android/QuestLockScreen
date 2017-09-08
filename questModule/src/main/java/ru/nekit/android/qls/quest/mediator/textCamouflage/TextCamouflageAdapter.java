package ru.nekit.android.qls.quest.mediator.textCamouflage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.nekit.android.qls.R;

class TextCamouflageAdapter extends RecyclerView.Adapter<TextCamouflageAdapter.ViewHolder> {

    private List<String> mData;

    TextCamouflageAdapter(List<String> data) {
        mData = data;
    }

    @Override
    public TextCamouflageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ill_text_camouflage, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.tv_label);
        }
    }
}
