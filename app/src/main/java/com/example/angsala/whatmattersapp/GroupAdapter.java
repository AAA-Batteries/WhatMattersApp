package com.example.angsala.whatmattersapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    Context context;
    List<String> relationship;
    MenuView.ItemView itemView;
    GroupFragment fragment;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView rv = view.findViewById(R.id.rvContacts);
            int itemPosition = rv.getChildLayoutPosition(view);
            String item = relationship.get(itemPosition);

            openGroupContacts(item);

            Toast.makeText(view.getContext(), item, Toast.LENGTH_LONG).show();
        }
    };


    public GroupAdapter(List<String> relationship) {
        this.relationship = relationship;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.item_group, parent, false);

        postView.setOnClickListener(mOnClickListener);

        return new ViewHolder(postView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String myPosition = relationship.get(position);

        Log.d("MyAdapter", relationship.toString());


        if (myPosition.equals("Friends")) {
            int color = context.getResources().getColor(R.color.Friends);
            viewHolder.groupImage.setColorFilter(color);
            viewHolder.groupName.setText("Friend");
        } else if (myPosition.equals("Parents")) {
            int color = context.getResources().getColor(R.color.Parents);
            viewHolder.groupImage.setColorFilter(color);
            viewHolder.groupName.setText("Parents");
        } else if (myPosition.equals("Classmates")) {
            int color = context.getResources().getColor(R.color.Classmates);
            viewHolder.groupImage.setColorFilter(color);
            viewHolder.groupName.setText("Classmates");
        } else if (myPosition.equals("Family")) {
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
        return relationship.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView groupName;
        ImageView groupImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupImage = itemView.findViewById(R.id.groupImage);
            groupName = itemView.findViewById(R.id.groupName);
            itemView.setOnClickListener(this);


        }


        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                String myPosition = relationship.get(position);
                Toast.makeText(context, "it clicks", Toast.LENGTH_SHORT).show();

            }
        }

        public void bind(final ImageView item, final AdapterView.OnItemClickListener listener) {
            item.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    String group = (String) item.getTag();
                    openGroupContacts(group);
                }
            });
        }
    }

    public void openGroupContacts(String group) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("relationship", group);
        fragment.setArguments(bundle);
    }
}
