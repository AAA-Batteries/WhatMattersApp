package com.example.angsala.whatmattersapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.angsala.whatmattersapp.model.Contacts;
import com.example.angsala.whatmattersapp.model.User;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    User user;

    Context context;
    List<Contacts> contacts;
    MenuView.ItemView itemView;

    public ContactsAdapter(List<Contacts> contacts) {
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.item_contact, parent, false);




        return new ViewHolder(postView);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Contacts contact = contacts.get(position);


        String relationship = contact.getRelationship();
        if (relationship.equals("Friends")){
            int color = context.getResources().getColor(R.color.Friends);
            viewHolder.contactColor.setColorFilter(color);
            viewHolder.relationship.setText("Friend");
        }
        else if (relationship.equals("Parents")){
            int color = context.getResources().getColor(R.color.Parents);
            viewHolder.contactColor.setColorFilter(color);
            viewHolder.relationship.setText("Parents");
        } else if (relationship.equals("Classmates")){
            int color = context.getResources().getColor(R.color.Classmates);
            viewHolder.contactColor.setColorFilter(color);
            viewHolder.relationship.setText("Classmates");
        } else  if (relationship.equals("Family")){
            int color = context.getResources().getColor(R.color.Family);
            viewHolder.contactColor.setColorFilter(color);
            viewHolder.relationship.setText("Family");
        } else {
            int color = context.getResources().getColor(R.color.colorAccent);
            viewHolder.contactColor.setColorFilter(color);
            viewHolder.relationship.setText("Professors");
        }

        //  Log.d("ContactsAdapter", n);
        Log.d("adapter", contacts.toString());

        viewHolder.contactImage.setImageResource(R.drawable.ic_launcher_background);

        //viewHolder.tvUserName.setText(n);
        viewHolder.tvUserName.setText(contact.getContactName());
        Log.d("adapter", viewHolder.tvUserName.toString());

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView contactImage;
        TextView tvUserName;
        ImageView contactColor;
        TextView relationship;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contactImage = itemView.findViewById(R.id.contactImage);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            contactColor = itemView.findViewById(R.id.contactColor);
            relationship = itemView.findViewById(R.id.relationship);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int viewPosition = getAdapterPosition();
            if (viewPosition != RecyclerView.NO_POSITION) {
                Contacts contact = contacts.get(viewPosition);
                String contactname = contact.getContactName();
                Intent chatIntent = new Intent(context, ChatActivity.class);
                chatIntent.putExtra("Recipient", contactname);
                context.startActivity(chatIntent);
            }
        }
    }
}
