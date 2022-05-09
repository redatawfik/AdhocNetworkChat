package com.adhoc.mobile;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesHolder> {

    private final String TAG = this.getClass().getName();
    private List<Message> messages;

    public MessagesAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.item_message, parent, false);

        return new MessagesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesHolder holder, int position) {
        Message message = messages.get(position);

        Log.i(TAG, "Bind message=" + message.getMessageContent() + " , Int position=" + position);

        if (message.getMessageType().equals(Message.MESSAGE_RECEIVED_TYPE)) {
            holder.leftMessage.setVisibility(TextView.VISIBLE);
            holder.leftMessage.setText(message.getMessageContent());
        } else {
            holder.rightMessage.setVisibility(TextView.VISIBLE);
            holder.rightMessage.setText(message.getMessageContent());
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        if (messages == null)
            messages = new ArrayList<>();

        return messages.size();
    }

    static class MessagesHolder extends RecyclerView.ViewHolder {

        TextView leftMessage;
        TextView rightMessage;

        public MessagesHolder(@NonNull View itemView) {
            super(itemView);

            leftMessage = (TextView) itemView.findViewById(R.id.messageReceived);
            rightMessage = (TextView) itemView.findViewById(R.id.messageSent);
        }
    }
}
