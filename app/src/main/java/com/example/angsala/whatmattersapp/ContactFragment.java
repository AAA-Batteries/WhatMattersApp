package com.example.angsala.whatmattersapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ContactFragment extends DialogFragment {

  EditText newUsername;
  Spinner relationshipSpinner;
  private ContactFragmentListener listener;
  Context cont;

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    try {
      listener = (ContactFragmentListener) context;

    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString() + "must implement ContactFragmentListener");
    }
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    // Get the layout inflater
    LayoutInflater inflater = getActivity().getLayoutInflater();
    View view = inflater.inflate(R.layout.addcontact, null);

    // Inflate and set the layout for the dialog
    // Pass null as the parent view because its going in the dialog layout
    builder
        .setView(view)
        // Add action buttons
        .setPositiveButton(
            R.string.addUser,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int id) {

                String userText = newUsername.getText().toString();
                String relationshipText = relationshipSpinner
                        .getSelectedItem()
                        .toString();

                if (relationshipText
                    .equalsIgnoreCase("Relationship…")) {
                  Toast.makeText(getContext(), "Select relationship", Toast.LENGTH_SHORT).show();
                } else {

                  listener.applyTexts(userText, relationshipText);
                  //   String spinnerText = relationshipSpinner.get
                }
              }
            })
        .setNegativeButton(
            R.string.cancel,
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                ContactFragment.this.getDialog().cancel();
              }
            });
    newUsername = view.findViewById(R.id.newUsername);
    relationshipSpinner = view.findViewById(R.id.spinner);
    // for some reason needs a CharSequence instead of String
    ArrayAdapter<CharSequence> adapter =
        ArrayAdapter.createFromResource(
            getContext(), R.array.relationships, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    relationshipSpinner.setAdapter(adapter);

    return builder.create();
  }

  public interface ContactFragmentListener {
    void applyTexts(String userName, String relationship);
    // void ite
  }
}
