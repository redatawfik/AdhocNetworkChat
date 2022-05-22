package com.adhoc.mobile;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adhoc.mobile.core.application.AdhocManager;
import com.adhoc.mobile.core.application.MessageServer;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ChatActivity extends AppCompatActivity implements Observer {

    private final String TAG = this.getClass().getName();

    private String name;
    private String id;
    private ImageButton sendButton;
    private EditText textMessage;
    private TextView nameText;
    private RecyclerView messageRecyclerView;
    private MessagesAdapter messagesAdapter;
    private List<Message> messages;
    private AdhocManager adhocManager;
    private MessageServer messageServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Log.i(TAG, "Create new instance of ChantActivity");

        name = getIntent().getStringExtra("EXTRA_NAME");
        id = getIntent().getStringExtra("EXTRA_ID");

        adhocManager = MessageServer.adhocManager;

        messageServer = MessageServer.getInstance();
        messageServer.addObserver(this);

        messages = messageServer.getMessagesForId(id);

        messageRecyclerView = findViewById(R.id.rvChat);
        messageRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        messagesAdapter = new MessagesAdapter(messages);
        messageRecyclerView.setAdapter(messagesAdapter);

        nameText = findViewById(R.id.nameText);
        nameText.setText(name);
        sendButton = findViewById(R.id.sendButton);
        textMessage = findViewById(R.id.textMessage);

        sendButton.setOnClickListener(view -> {
            String messageText = textMessage.getText().toString();
            if (!messageText.isEmpty()) {
                Log.i(TAG, "Send message=" + messageText + " , to id=" + id);

                messageServer.addMessageForId(messageText, id, Message.MESSAGE_SENT_TYPE);

                int position = messages.size() - 1;

                messagesAdapter.notifyItemInserted(position);
                messageRecyclerView.scrollToPosition(position);

                textMessage.setText("");

                adhocManager.sendMessage(messageText, id);
            }
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        messagesAdapter.notifyItemInserted(messages.size() - 1);
        messageRecyclerView.scrollToPosition(messages.size() - 1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        messageServer.deleteObserver(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        messageServer.deleteObserver(this);
        this.finish();
    }
}

class WrapContentLinearLayoutManager extends LinearLayoutManager {
    public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            Log.e("TAG", "meet a IOOBE in RecyclerView");
        }
    }
}
