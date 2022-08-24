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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apartplanner.AdminActivity;
import com.example.apartplanner.R;
import com.example.apartplanner.UploadImageActivity;
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

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.ImageViewHolder> implements StudioAdminAdapter.OnItemClickListener{//режим создания адреса

    Context mContext;
    List<UploadImageActivity> mUploads;
    OnItemClickListener mListener;
    DatabaseReference databaseReference;
    String mKey;
    String adressName;

    NpaLinearLayoutManager npaLinearLayoutManager;

    ArrayList<Studio> mStudios;
    ArrayList<Studio> currentStudioList;

    public ArrayList<Studio> getStudios() {
        return mStudios;
    }

    public void setKey(String key) {
        this.mKey = key;
    }

    public void setStudios(ArrayList<Studio> mStudios) {
        this.mStudios = mStudios;
    }

    public AdminAdapter(Context context, List<UploadImageActivity> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new ImageViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        UploadImageActivity uploadImageActivityCurrent = mUploads.get(position);
        holder.textViewName.setText(uploadImageActivityCurrent.getName());
        Picasso.with(mContext)
                .load(uploadImageActivityCurrent.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .fit()
                .centerCrop()
                .into(holder.imageView);
        holder.studioRecycler.setHasFixedSize(true);
        npaLinearLayoutManager = new NpaLinearLayoutManager(mContext);
        holder.studioRecycler.setLayoutManager(npaLinearLayoutManager);
        StudioAdminAdapter studioAdminAdapter = new StudioAdminAdapter(mContext);
        studioAdminAdapter.setOnItemClickListener(AdminAdapter.this);
        studioAdminAdapter.setStudioList(uploadImageActivityCurrent.getStudioList());
        holder.studioRecycler.setAdapter(studioAdminAdapter);
        studioAdminAdapter.notifyDataSetChanged();
        adressName = holder.textViewName.getText().toString();
        currentStudioList = StudioAdminAdapter.getStudioList();
    }

    @Override
    public int getItemCount() {
        if(mUploads != null){
            return mUploads.size();
        }else{
            return 0;
        }
    }

    @Override
    public void onBooked(int position) {

        if (!currentStudioList.isEmpty()) {
            currentStudioList.get(position).setState("бронь");
            updateStudios(currentStudioList);
            Log.d("MyLog", "booked " + ", positionStudio : "+position +", adressKey : "+ adressName+ " , itemCount : "+getItemCount());
        }

    }


    @Override
    public void onSold(int position) {
        if (!currentStudioList.isEmpty()) {
            currentStudioList.get(position).setState("продано");
            updateStudios(currentStudioList);
            //Log.d("MyLog", "booked " + ", position : "+position + ", itemCount : "+getItemCount() + ", itemId : "+getItemId(position));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateStudios(ArrayList<Studio> studios){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    UploadImageActivity upload = dataSnapshot.getValue(UploadImageActivity.class);
                    if(upload.getName().equals(adressName)){
                        databaseReference.child(Objects.requireNonNull(dataSnapshot.getKey())).child("studioList").setValue(studios);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        npaLinearLayoutManager.removeAllViews();
        notifyDataSetChanged();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements /*View.OnClickListener,*/ View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener {

        public TextView textViewName;
        public ImageView imageView;
        public RecyclerView studioRecycler;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageUser);
            textViewName = itemView.findViewById(R.id.adressUserText);
            studioRecycler = itemView.findViewById(R.id.recyclerStudio);
            itemView.setOnCreateContextMenuListener(this);

            databaseReference = FirebaseDatabase.getInstance().getReference("uploads");
        }


        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem delete = contextMenu.add(Menu.NONE, 1,1,"Удалить адрес");
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if(mListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    if (menuItem.getItemId() == 1) {
                        mListener.onDeleteClick(position);
                        return true;
                    }
                }
            }
            Log.d("MyLog","chto");
            return false;
        }
    }

    public interface OnItemClickListener {

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    private static class NpaLinearLayoutManager extends LinearLayoutManager {
        public NpaLinearLayoutManager(Context context) {
            super(context);
        }

        public NpaLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public NpaLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }
        /**
         * Disable predictive animations. There is a bug in RecyclerView which causes views that
         * are being reloaded to pull invalid ViewHolders from the internal recycler stack if the
         * adapter size has decreased since the ViewHolder was recycled.
         */
        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }

    }
}
