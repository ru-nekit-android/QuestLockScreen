package ru.nekit.android.qls.parentControl;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class TypedMessageViewHolder extends RecyclerView.ViewHolder {

    CardView container;
    TextView textView;

    TypedMessageViewHolder(View view) {
        super(view);
        container = (CardView) view.findViewById(R.id.container);
        textView = (TextView) view.findViewById(R.id.tv_view);
    }
}
