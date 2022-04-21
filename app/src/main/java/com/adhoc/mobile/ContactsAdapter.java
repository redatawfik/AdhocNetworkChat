package com.adhoc.mobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private final List<Contact> contacts;
    RecyclerViewClickListener listener;


    public ContactsAdapter(List<Contact> contacts, RecyclerViewClickListener listener) {
        this.contacts = contacts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_contact, parent, false);

        return new ViewHolder(context, contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.position = position;

        TextView textView = holder.nameTextView;
        textView.setText(contact.getName());
        Button button = holder.messageButton;
        button.setText("Message");

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        public TextView nameTextView;
        public Button messageButton;
        public int position;

        public ViewHolder(Context context, View itemView) {

            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.contact_name);
            messageButton = (Button) itemView.findViewById(R.id.message_button);
            this.context = context;


            messageButton.setOnClickListener(view -> listener.onClick(position));

        }
    }
}

