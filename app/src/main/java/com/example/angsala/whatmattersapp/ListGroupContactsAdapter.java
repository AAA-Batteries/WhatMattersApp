package com.example.angsala.whatmattersapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.angsala.whatmattersapp.model.Contacts;

import java.util.List;

public class ListGroupContactsAdapter extends RecyclerView.Adapter<ListGroupContactsAdapter.ViewHolder>{
    List<Contacts> contactsList;
    Context context;

    public ListGroupContactsAdapter(List<Contacts> contactsList){
        this.contactsList = contactsList;

    }
    @NonNull
    @Override
    public ListGroupContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.item_grouplistcontact, parent, false);
        return new ViewHolder(postView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListGroupContactsAdapter.ViewHolder viewHolder, int position) {
        Contacts contacts = contactsList.get(position);
        String relationship = contacts.getRelationship();
        if (relationship.equals("Friends")){
            int color = context.getResources().getColor(R.color.Friends);
            viewHolder.contactGroupRelationship.setColorFilter(color);
            viewHolder.groupRelationship.setText("Friend");
        }
        else if (relationship.equals("Parents")){
            int color = context.getResources().getColor(R.color.Parents);
            viewHolder.contactGroupRelationship.setColorFilter(color);
            viewHolder.groupRelationship.setText("Parents");
        } else if (relationship.equals("Classmates")){
            int color = context.getResources().getColor(R.color.Classmates);
            viewHolder.contactGroupRelationship.setColorFilter(color);
            viewHolder.groupRelationship.setText("Classmates");
        } else  if (relationship.equals("Family")){
            int color = context.getResources().getColor(R.color.Family);
            viewHolder.contactGroupRelationship.setColorFilter(color);
            viewHolder.groupRelationship.setText("Family");
        } else {
            int color = context.getResources().getColor(R.color.colorAccent);
            viewHolder.contactGroupRelationship.setColorFilter(color);
            viewHolder.groupRelationship.setText("Professors");
        }



        Log.d("adapter", contacts.toString());
        viewHolder.contactGroupImage.setImageResource(R.drawable.ic_launcher_background);
        viewHolder.myContactName.setText(contacts.getContactName());
        Log.d("adapter", viewHolder.myContactName.toString());
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView contactGroupImage;
        ImageView contactGroupRelationship;
        TextView groupRelationship;
        TextView myContactName;
        TextView alertMessage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contactGroupImage = itemView.findViewById(R.id.contactGroupImage);
            contactGroupRelationship = itemView.findViewById(R.id.ivContactGroupRelationship);
            groupRelationship = itemView.findViewById(R.id.tvGroupRelationship);
            myContactName = itemView.findViewById(R.id.myContactName);
            alertMessage = itemView.findViewById(R.id.alertMessage);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int viewPosition = getAdapterPosition();
            if (viewPosition != RecyclerView.NO_POSITION) {
                Contacts contact = contactsList.get(viewPosition);
                String contactname = contact.getContactName();
                Intent chatIntent = new Intent(context, ChatActivity.class);
                chatIntent.putExtra("Recipient", contactname);
                context.startActivity(chatIntent);
            }


        }
    }
}
