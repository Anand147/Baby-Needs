package com.example.babyneeds.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babyneeds.R;
import com.example.babyneeds.data.DatabaseHandler;
import com.example.babyneeds.model.Item;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.text.MessageFormat;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Item> itemList;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater inflater;

    public RecyclerViewAdapter(Context context, List<Item> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row, viewGroup, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder viewHolder, int position) {

        Item item = itemList.get(position);  //Object Item
        viewHolder.babyItemName.setText(MessageFormat.format("Item Name: {0}",item.getItemName()));
        viewHolder.babyItemColor.setText(MessageFormat.format("Color: {0}",item.getItemColor()));
        viewHolder.babyItemQuantity.setText(MessageFormat.format("Quantity: {0}",Integer.toString(item.getItemQuantity())));  //Since Quantity is INTEGER type so passing the value we need to convert it to STRING TYPE
        viewHolder.babyItemSize.setText(MessageFormat.format("Size: {0}",Integer.toString(item.getItemSize())));          //Since SIZE is INTEGER type so passing the value we need to convert it to STRING TYPE
        viewHolder.babyItemDate.setText(MessageFormat.format("Date : {0}",item.getDateItemAdded()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView babyItemName;
        public TextView babyItemQuantity;
        public TextView babyItemColor;
        public TextView babyItemSize;
        public TextView babyItemDate;
        public Button editButton;
        public Button deleteButton;
        public int id;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            babyItemName = itemView.findViewById(R.id.baby_item_name);
            babyItemColor = itemView.findViewById(R.id.baby_item_color);
            babyItemQuantity = itemView.findViewById(R.id.babyItemQuantity);
            babyItemSize = itemView.findViewById(R.id.babyItemSize);
            babyItemDate = itemView.findViewById(R.id.babyItemDate);

            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position;
            switch (v.getId()){
                case R.id.editButton:
                    //edit Item
                    position= getAdapterPosition();
                    Item items = itemList.get(position);
                    editItem(items);
                    break;

                case R.id.deleteButton:
                    //delete Item
                    position= getAdapterPosition();
                    Item item = itemList.get(position);
                    deleteItem(item.getId());
                    break;
            }

        }

        private void deleteItem(int id) {

            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);

            View view = inflater.inflate(R.layout.confirmation_pop, null);

            Button noButton = view.findViewById(R.id.conf_no_button);
            Button yesButton = view.findViewById(R.id.conf_yes_button);

            builder.setView(view);
            dialog = builder.create();
            dialog.show();

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatabaseHandler db = new DatabaseHandler(context);
                    db.deleteItem(id);
                    itemList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());

                    dialog.dismiss();
                }
            });

            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

        }

        private void editItem(Item newItem) {

            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.popup, null);

            Button saveButton;
            EditText babyItem;
            EditText itemQuantity;
            EditText itemColor;
            EditText itemSize;
            TextView title;

            babyItem = view.findViewById(R.id.babyItem);
            itemQuantity = view.findViewById(R.id.itemQuantity);
            itemColor =  view.findViewById(R.id.itemColor);
            itemSize = view.findViewById(R.id.itemSize);

            title = view.findViewById(R.id.title);
            title.setText(R.string.updateItem);

            saveButton = view.findViewById(R.id.saveButton);
            saveButton.setText("UPDATE");

            babyItem.setText(newItem.getItemName());
            itemColor.setText(newItem.getItemColor());
            itemQuantity.setText(Integer.toString(newItem.getItemQuantity()));  //Since Quantity is INTEGER type so passing the value we need to convert it to STRING TYPE
            itemSize.setText(Integer.toString(newItem.getItemSize()));          //Since SIZE is INTEGER type so passing the value we need to convert it to STRING TYPE

            builder.setView(view);
            dialog = builder.create();
            dialog.show();
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //update our item
                    DatabaseHandler databaseHandler = new DatabaseHandler(context);
                    newItem.setItemName(babyItem.getText().toString());
                    newItem.setItemColor(itemColor.getText().toString());
                    newItem.setItemQuantity(Integer.parseInt(itemQuantity.getText().toString()));
                    newItem.setItemSize(Integer.parseInt(itemSize.getText().toString()));

                    if(!babyItem.getText().toString().isEmpty()
                            && !itemColor.getText().toString().isEmpty()
                            && !itemQuantity.getText().toString().isEmpty()
                            && !itemSize.getText().toString().isEmpty()) {
                        databaseHandler.updateItem(newItem);
                        notifyItemChanged(getAdapterPosition(), newItem);  //Important
                        dialog.dismiss();
                    }else{
                        Snackbar.make(v, "Empty fields not Allowed", BaseTransientBottomBar.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
