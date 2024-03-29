package com.example.apartplanner.adapter;

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
import com.stfalcon.imageviewer.StfalconImageViewer;

import java.util.ArrayList;

public class AddressAdapter extends FirebaseRecyclerAdapter<Address, AddressAdapter.AddressViewHolder> {
    private final AddressAdapterEventListener listener;

    public AddressAdapter(FirebaseRecyclerOptions<Address> options, AddressAdapterEventListener listener) {
        super(options);
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull AddressAdapter.AddressViewHolder holder, int position, @NonNull Address address) {
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

    public class AddressViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener{

        private final TextView textViewName;
        private final ImageView imageView;
        private final StudioAdapter studioAdapter;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnCreateContextMenuListener(this);
            imageView = itemView.findViewById(R.id.imageUser);
            textViewName = itemView.findViewById(R.id.addressUserText);
            RecyclerView studioRecycler = itemView.findViewById(R.id.recyclerStudio);

            studioAdapter = new StudioAdapter();
            studioRecycler.setAdapter(studioAdapter);

            imageView.setOnClickListener(v -> new StfalconImageViewer.Builder<>(v.getContext(),
                    new String[]{getItem(getBindingAdapterPosition()).getImageUrl()},
                    (view, imageUrl) ->
                            Picasso.with(view.getContext()).load(imageUrl).placeholder(imageView.getDrawable()).into(view))
                    .withTransitionFrom(imageView)
                    .withBackgroundColorResource(R.color.fullscreen_image_background)
                    .withHiddenStatusBar(false)
                    .show()
            );

        }


        public void bind(String name, String imageUrl, ArrayList<Studio> studios) {
            textViewName.setText(name);
            Picasso.with(itemView.getContext())
                    .load(imageUrl)
//                    .placeholder(R.drawable.ic_launcher_background)
//                    .centerCrop()
                    .into(imageView);

            studioAdapter.submitList(studios);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if(listener!=null){
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if (menuItem.getItemId() == 1) {
                        listener.onGeneratePdf(getItem(position), getRef(position));
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem generatePdf = contextMenu.add(Menu.NONE, 1, 1, "Создать презентацию");
            generatePdf.setOnMenuItemClickListener(this);
        }
    }

    public interface AddressAdapterEventListener {
        void onDataChanged();

        void onError(DatabaseError e);

        void onGeneratePdf(Address item, DatabaseReference ref);
    }

}
