package com.adhoc.mobile;

import static com.adhoc.mobile.RandomColor.getRandomColor;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsHolder> {

    private final List<Contact> contacts;
    RecyclerViewClickListener listener;


    public ContactsAdapter(List<Contact> contacts, RecyclerViewClickListener listener) {
        this.contacts = contacts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_contact, parent, false);

        return new ContactsHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.position = position;

        holder.nameTextView.setText(contact.getName());
        holder.phoneNumberTextView.setText(contact.getPhoneNumber());

        GradientDrawable draw = new GradientDrawable();
        draw.setShape(GradientDrawable.OVAL);
        draw.setColor(getRandomColor());
        holder.chatImgTextView.setBackground(draw);

        String txt = contact.getName().length() > 0 ? String.valueOf(contact.getName().charAt(0)) : "A";
        holder.chatImgTextView.setText(txt);
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
        return contacts.size();
    }

    public interface RecyclerViewClickListener {
        void onClick(int position);
    }

    class ContactsHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView phoneNumberTextView;
        public TextView chatImgTextView;
        public int position;

        public ContactsHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.contact_name);
            phoneNumberTextView = itemView.findViewById(R.id.phone_number);
            chatImgTextView = itemView.findViewById(R.id.chat_img);

            itemView.setOnClickListener(view -> listener.onClick(position));
        }
    }
}

