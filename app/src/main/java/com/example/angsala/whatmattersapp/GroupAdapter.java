package com.example.angsala.whatmattersapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.angsala.whatmattersapp.model.Contacts;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    Context context;
    List<Contacts> contacts;
    MenuView.ItemView itemView;


    public GroupAdapter(List<Contacts> contacts) {
        this.contacts = contacts;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.item_group, parent, false);




        return new ViewHolder(postView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Contacts contacts1 = contacts.get(position);

        String relationship = contacts1.getRelationship();
        if (relationship.equals("Friends")){
            int color = context.getResources().getColor(R.color.Friends);
            viewHolder.groupImage.setColorFilter(color);
            viewHolder.groupName.setText("Friend");
        }
        else if (relationship.equals("Parents")){
            int color = context.getResources().getColor(R.color.Parents);
            viewHolder.groupImage.setColorFilter(color);
            viewHolder.groupName.setText("Parents");
        } else if (relationship.equals("Classmates")){
            int color = context.getResources().getColor(R.color.Classmates);
            viewHolder.groupImage.setColorFilter(color);
            viewHolder.groupName.setText("Classmates");
        } else  if (relationship.equals("Family")){
            int color = context.getResources().getColor(R.color.Family);
            viewHolder.groupImage.setColorFilter(color);
            viewHolder.groupName.setText("Family");
        } else {
            int color = context.getResources().getColor(R.color.colorAccent);
            viewHolder.groupImage.setColorFilter(color);
            viewHolder.groupName.setText("Professors");
        }



    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;
        ImageView groupImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupImage = itemView.findViewById(R.id.groupImage);
            groupName = itemView.findViewById(R.id.groupName);
        }
    }
}
