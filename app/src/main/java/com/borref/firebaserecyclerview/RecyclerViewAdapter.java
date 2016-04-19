package com.borref.firebaserecyclerview;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usuario on 19/04/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>  {
    private List<MainActivity.Item> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView id;
        public TextView name;
        public ImageButton mDeleteBtn;

        public ViewHolder(View v) {
            super(v);
            id = (TextView) v.findViewById(R.id.item_id);
            name = (TextView) v.findViewById(R.id.item_name);
            mDeleteBtn = (ImageButton) v.findViewById(R.id.delete_btn);
        }
    }

    public RecyclerViewAdapter(List<MainActivity.Item> items) { mDataset = items; }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, final int position) {
        holder.id.setText(mDataset.get(position).getId());
        holder.name.setText(mDataset.get(position).getName());
        holder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase myFirebaseRef = new Firebase(MainActivity.BASE_URL);
                String itemName = mDataset.get(position).getName();
                myFirebaseRef.child(mDataset.get(position).getId()).removeValue();
                Snackbar.make(v, itemName + " deleted from Firebase", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
