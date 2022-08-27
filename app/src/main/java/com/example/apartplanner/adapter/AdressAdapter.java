package com.example.apartplanner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apartplanner.R;
import com.example.apartplanner.model.Address;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdressAdapter extends RecyclerView.Adapter<AdressAdapter.ImageViewHolder> {//режим админа

    Context mContext;
    List<Address> mUploads;
    /*OnItemClickListener mListener;*/

    public AdressAdapter(Context context, List<Address> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_address, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Address addressCurrent = mUploads.get(position);
        holder.textViewName.setText(addressCurrent.getName());
        Picasso.with(mContext)
                .load(addressCurrent.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener, View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener */ {

        public TextView textViewName;
        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageUser);
            textViewName = itemView.findViewById(R.id.adressUserText);

/*            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);*/
        }
/*
        @Override
        public void onClick(View view) {
            if(mListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    mListener.onDeleteClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem delete = contextMenu.add(Menu.NONE, 1,1,"Delete");
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
            return false;
        }
    }

    public interface OnItemClickListener {

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }*/
    }
}
