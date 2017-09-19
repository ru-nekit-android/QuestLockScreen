package ru.nekit.android.qls.parentControl;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import ru.nekit.android.qls.InternalCommand;
import ru.nekit.android.qls.quest.QuestContextEvent;
import ru.nekit.android.qls.quest.TypedMessage;
import ru.nekit.android.qls.quest.history.QuestHistoryItem;

public class TypedMessageListAdapter extends
        RecyclerView.Adapter<TypedMessageViewHolder> {

    @NonNull
    private final Context mContext;
    private List<TypedMessage> mMessageList;

    public TypedMessageListAdapter(@NonNull Context context,
                                   @NonNull List<TypedMessage> messageList) {
        mContext = context;
        mMessageList = messageList;
    }

    @Override
    public TypedMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TypedMessageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final TypedMessageViewHolder holder, int position) {
        TypedMessage message = mMessageList.get(position);
        String text = message.data.toString();
        @ColorRes int color = 0, textColor = 0;
        switch (message.messageType) {

            case QuestHistoryItem.NAME:

                QuestHistoryItem questHistoryItem = (QuestHistoryItem) message.data;
                color = questHistoryItem.isRightAnswer ? R.color.green : R.color.red;
                text = questHistoryItem.isRightAnswer ? "Правильный ответ" :
                        "Неправильный ответ";
                textColor = R.color.white;

                break;

            case InternalCommand.NAME:

                InternalCommand internalCommand = (InternalCommand) message.data;
                if (QuestContextEvent.EVENT_QUEST_CREATE.equals(internalCommand.command)) {
                    text = "Начат новый квест";
                } else {
                    text = "Другая операция";
                }
                color = R.color.white;
                textColor = R.color.black;

                break;

        }
        holder.container.setCardBackgroundColor(ContextCompat.getColor(mContext, color));
        holder.textView.setText(text);
        holder.textView.setTextColor(ContextCompat.getColor(mContext, textColor));
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

}