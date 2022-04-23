package com.adhoc.mobile;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adhoc.mobile.core.application.AdhocManager;
import com.adhoc.mobile.core.application.AdhocManagerCallbacks;
import com.adhoc.mobile.core.application.Endpoint;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private String name;
    private String id;
    private String myName;
    private ImageButton sendButton;
    private EditText textMessage;
    private TextView nameText;
    private RecyclerView messageRecyclerView;
    private MessagesAdapter messagesAdapter;
    private List<Message> messages;
    private AdhocManager adhocManager;
    AdhocManagerCallbacks callbacks = new AdhocManagerCallbacks() {
        @Override
        public void onConnectionSucceed(Endpoint endpoint) {

        }

        @Override
        public void onDisconnected(String endpointId) {

        }

        @Override
        public void onPayloadReceived(String endpointId, String messageText) {
            System.out.println("UI "+"Received : " +  endpointId + messageText);
            Message message = new Message(messageText, Message.MESSAGE_RECEIVED_TYPE);
            messages.add(message);
            System.out.println("Hello"+messageText+ endpointId);
            int position = messages.size()-1;

            messagesAdapter.notifyItemInserted(position);
            messageRecyclerView.scrollToPosition(position);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        name = getIntent().getStringExtra("EXTRA_NAME");
        id = getIntent().getStringExtra("EXTRA_ID");
        myName = getIntent().getStringExtra("EXTRA_USER_NAME");
        messages = new ArrayList<>();
//        messages.add(new Message("hello", Message.MESSAGE_RECEIVED_TYPE));

        adhocManager = new AdhocManager(this, myName, callbacks);

        messageRecyclerView = (RecyclerView) findViewById(R.id.rvChat);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesAdapter = new MessagesAdapter(messages);
        messageRecyclerView.setAdapter(messagesAdapter);

        nameText = (TextView) findViewById(R.id.nameText);
        nameText.setText(name);
        sendButton = (ImageButton) findViewById(R.id.sendButton);
        textMessage = (EditText) findViewById(R.id.textMessage);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = textMessage.getText().toString();
                if(!messageText.isEmpty()){

                    System.out.println(messageText+id);

                    Message message = new Message(messageText, Message.MESSAGE_SENT_TYPE);
                    messages.add(message);

                    int position = messages.size()-1;

                    messagesAdapter.notifyItemInserted(position);
                    messageRecyclerView.scrollToPosition(position);

                    textMessage.setText("");

                    adhocManager.sendMessage(messageText, id);
                }
            }
        });
    }
}
