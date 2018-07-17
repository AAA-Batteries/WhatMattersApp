package com.example.angsala.whatmattersapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.angsala.whatmattersapp.model.User;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder>  {

    User user;


    Context context;
    List<String> contacts;

    public ContactsAdapter(List<String> contacts){
        this.contacts = contacts;

    }


    @NonNull
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.item_contact, parent, false);
        user = new User();
        contacts = user.getContacts();

        return new ViewHolder(postView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.ViewHolder viewHolder, int position) {
         contacts.get(position);
         viewHolder.tvUserName.setText((CharSequence) contacts);





    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView contactImage;
        TextView tvUserName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contactImage = itemView.findViewById(R.id.contactImage);
            tvUserName = itemView.findViewById(R.id.tvUserName);
        }
    }
}
