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
<<<<<<< HEAD
//        user = new User();
//        contacts = new ArrayList<>();
=======
>>>>>>> 9f1fdccd9461b477986c4d26a802c6f1e1608473

        return new ViewHolder(postView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.ViewHolder viewHolder, int position) {
        String n = contacts.get(position);
      //  Log.d("ContactsAdapter", n);

         viewHolder.contactImage.setImageResource(R.drawable.ic_launcher_background);

<<<<<<< HEAD
         viewHolder.tvUserName.setText(n);
=======
        for (int i = 0; i < contacts.size(); i++){
            viewHolder.tvUserName.setText(contacts.get(position));
        }


>>>>>>> 9f1fdccd9461b477986c4d26a802c6f1e1608473

    }

    @Override
    public int getItemCount() {
        return contacts.size();
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
