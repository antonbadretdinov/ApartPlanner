package com.example.apartplanner.adapter;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apartplanner.R;
import com.example.apartplanner.model.Address;
import com.example.apartplanner.model.Studio;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdminAdapter extends FirebaseRecyclerAdapter<Address, AdminAdapter.ImageViewHolder> {
    private final AdminAdapterEventListener listener;

    public AdminAdapter(FirebaseRecyclerOptions<Address> options, AdminAdapterEventListener listener) {
        super(options);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ImageViewHolder holder, int position, @NonNull Address address) {
        holder.bind(
                address.getName(),
                address.getImageUrl(),
                address.getStudioList()
        );
    }

    @Override
    public void onDataChanged() {
        listener.onDataChanged();
    }

    @Override
    public void onError(@NonNull DatabaseError e) {
        listener.onError(e);
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener {

        private final TextView textViewName;
        private final ImageView imageView;
        private final StudioAdminAdapter studioAdminAdapter;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnCreateContextMenuListener(this);
            imageView = itemView.findViewById(R.id.imageUser);
            textViewName = itemView.findViewById(R.id.addressUserText);
            RecyclerView studioRecycler = itemView.findViewById(R.id.recyclerStudio);

            studioAdminAdapter = new StudioAdminAdapter(studio ->
                    listener.onStudioUpdate(getRef(getBindingAdapterPosition()), studio)
            );
            studioRecycler.setAdapter(studioAdminAdapter);
        }

        public void bind(String name, String imageUrl, ArrayList<Studio> studios) {
            textViewName.setText(name);
            Picasso.with(itemView.getContext())
                    .load(imageUrl)
//                    .placeholder(R.drawable.ic_launcher_background)
//                    .centerCrop()
                    .into(imageView);

            studioAdminAdapter.submitList(studios);
        }


        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem delete = contextMenu.add(Menu.NONE, 1, 1, "Удалить адрес");
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (listener != null) {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if (menuItem.getItemId() == 1) {
                        listener.onDeleteClick(getItem(position), getRef(position));
                        return true;
                    }
                }
            }
            Log.d("MyLog", "chto");
            return false;
        }
    }

    public interface AdminAdapterEventListener {
        void onDataChanged();

        void onError(DatabaseError e);

        void onDeleteClick(Address address, DatabaseReference ref);

        void onStudioUpdate(DatabaseReference addressRef, Studio studio);
    }

}
