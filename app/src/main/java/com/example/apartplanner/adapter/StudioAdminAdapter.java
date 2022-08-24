package com.example.apartplanner.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apartplanner.R;
import com.example.apartplanner.model.Studio;

import java.util.ArrayList;
import java.util.Collections;

public class StudioAdminAdapter extends RecyclerView.Adapter<StudioAdminAdapter.StudioViewHolder> {

    private LayoutInflater inflater;
    public static ArrayList<Studio> studioList;
    Context context;
    StudioAdminAdapter.OnItemClickListener mListener;

    public StudioAdminAdapter(Context context) {
        this.context = context;
    }

    public void setStudioList(ArrayList<Studio> studioList) {
        StudioAdminAdapter.studioList = studioList;
        StudioAdminAdapter.studioList.removeAll(Collections.singleton(null));
    }

    public static ArrayList<Studio> getStudioList() {
        return studioList;
    }

    @NonNull
    @Override
    public StudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.studio_table_item,parent,false);
        return new StudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudioViewHolder holder, int position) {

        holder.name.setText(studioList.get(position).getName());
        holder.size.setText(studioList.get(position).getSize());
        holder.state.setText(studioList.get(position).getState());

        changeRowColor(holder.state.getText().toString(),holder.tableRow);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void changeRowColor(String state, TableRow tableRow){
        switch (state){
            case "продано":
                tableRow.setBackgroundColor(Color.GREEN);
                break;
            case "бронь":
                tableRow.setBackgroundColor(Color.YELLOW);
                break;
            default:
                tableRow.setBackgroundColor(Color.WHITE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        if(studioList!=null) {
            return studioList.size();
        }else{
            return 0;
        }
    }

    public class StudioViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener {

        EditText name,size,state;
        TableRow tableRow;

        public StudioViewHolder(@NonNull View itemView) {
            super(itemView);

            tableRow = itemView.findViewById(R.id.tableRowUser);
            name = itemView.findViewById(R.id.editNameStudio);
            size = itemView.findViewById(R.id.editSizeStudio);
            state = itemView.findViewById(R.id.editStateStudio);

            name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    studioList.get(getAdapterPosition()).setName(name.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            size.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    studioList.get(getAdapterPosition()).setSize(size.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            state.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    studioList.get(getAdapterPosition()).setState(state.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if(mListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    switch (menuItem.getItemId()){
                        case 2:
                            mListener.onBooked(position);
                            return true;
                        case 3:
                            mListener.onSold(position);
                            return true;
                    }
                }
            }
            Log.d("MyLog","loh");
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem booked = contextMenu.add(Menu.NONE, 2,2,"Бронь");
            MenuItem sold = contextMenu.add(Menu.NONE,3,3,"Продано");
            booked.setOnMenuItemClickListener(this);
            sold.setOnMenuItemClickListener(this);
        }
    }

    public interface OnItemClickListener {

        void onBooked(int position);
        void onSold(int position);
    }

        public void setOnItemClickListener(OnItemClickListener listener){
            mListener = listener;
        }
}

