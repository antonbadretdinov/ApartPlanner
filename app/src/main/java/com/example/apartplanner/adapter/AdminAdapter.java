package com.example.apartplanner.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apartplanner.R;
import com.example.apartplanner.Address;
import com.example.apartplanner.model.Studio;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.ImageViewHolder> {//режим создания адреса
    List<Address> mUploads;
    OnItemClickListener mListener;
    DatabaseReference databaseReference;
    String mKey;
    String adressName;

//    ArrayList<Studio> currentStudioList;


    public void setKey(String key) {
        this.mKey = key;
    }


    public AdminAdapter(List<Address> uploads) {
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");
        mUploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new ImageViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Address addressCurrent = mUploads.get(position);

        holder.bind(
                addressCurrent.getName(),
                addressCurrent.getImageUrl(),
                addressCurrent.getStudioList()
        );
        adressName = holder.textViewName.getText().toString();
    }

    @Override
    public int getItemCount() {
        if (mUploads != null) {
            return mUploads.size();
        } else {
            return 0;
        }
    }

    private final StudioAdminAdapter.OnItemUpdateListener studioItemMenuListener = new StudioAdminAdapter.OnItemUpdateListener() {
        @Override
        public void onUpdate(Studio studio) {

        }

//        @Override
//        public void onBooked(int position) {
//
//            if (!currentStudioList.isEmpty()) {
//                currentStudioList.get(position).setState("бронь");
//                updateStudios(currentStudioList);
//                Log.d("MyLog", "booked " + ", positionStudio : " + position + ", adressKey : " + adressName + " , itemCount : " + getItemCount());
//            }
//
//        }
//
//        @Override
//        public void onSold(int position) {
//            if (!currentStudioList.isEmpty()) {
//                currentStudioList.get(position).setState("продано");
//                updateStudios(currentStudioList);
//                //Log.d("MyLog", "booked " + ", position : "+position + ", itemCount : "+getItemCount() + ", itemId : "+getItemId(position));
//            }
//        }
    };

    @SuppressLint("NotifyDataSetChanged")
    public void updateStudios(ArrayList<Studio> studios) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Address upload = dataSnapshot.getValue(Address.class);
                    if (upload != null && upload.getName().equals(adressName)) {
                        databaseReference.child(Objects.requireNonNull(dataSnapshot.getKey())).child("studioList").setValue(studios);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        notifyDataSetChanged();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements /*View.OnClickListener,*/ View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener {

        private final TextView textViewName;
        private final ImageView imageView;
        private final StudioAdminAdapter studioAdminAdapter;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageUser);
            textViewName = itemView.findViewById(R.id.adressUserText);
            RecyclerView studioRecycler = itemView.findViewById(R.id.recyclerStudio);
            itemView.setOnCreateContextMenuListener(this);

            studioAdminAdapter = new StudioAdminAdapter(studioItemMenuListener);
            studioRecycler.setAdapter(studioAdminAdapter);
        }

        public void bind(String name, String imageUrl, ArrayList<Studio> studios) {

            textViewName.setText(name);

            Picasso.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .fit()
                    .centerCrop()
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
            if (mListener != null) {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if (menuItem.getItemId() == 1) {
                        mListener.onDeleteClick(position);
                        return true;
                    }
                }
            }
            Log.d("MyLog", "chto");
            return false;
        }
    }

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

}
