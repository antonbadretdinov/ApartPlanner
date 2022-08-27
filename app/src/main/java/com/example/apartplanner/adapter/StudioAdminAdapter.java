package com.example.apartplanner.adapter;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apartplanner.R;
import com.example.apartplanner.Utils;
import com.example.apartplanner.model.Studio;

public class StudioAdminAdapter extends ListAdapter<Studio, StudioAdminAdapter.StudioViewHolder> {
    private final StudioAdminAdapter.OnItemUpdateListener mListener;

    protected StudioAdminAdapter(OnItemUpdateListener mListener) {
        super(new DiffUtil.ItemCallback<Studio>() {
            @Override
            public boolean areItemsTheSame(@NonNull Studio oldItem, @NonNull Studio newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Studio oldItem, @NonNull Studio newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public StudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.studio_table_item, parent, false);
        return new StudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudioViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


    public class StudioViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener {

        private final EditText name, size, state;
        private final TableRow tableRow;

        public StudioViewHolder(@NonNull View itemView) {
            super(itemView);

            tableRow = itemView.findViewById(R.id.tableRowUser);
            name = itemView.findViewById(R.id.editNameStudio);
            size = itemView.findViewById(R.id.editSizeStudio);
            state = itemView.findViewById(R.id.editStateStudio);

            Utils.setTextWatcher(name, (text, start, before, count) ->
                    getItem(getBindingAdapterPosition()).setName(text.toString())
            );
            Utils.setTextWatcher(size, (text, start, before, count) ->
                    getItem(getBindingAdapterPosition()).setSize(text.toString())
            );
            Utils.setTextWatcher(state, (text, start, before, count) -> {
                getItem(getBindingAdapterPosition()).setState(text.toString());
                changeRowColor(text.toString());
            });

            Utils.OnDoneListener doneListener = text ->
                    mListener.onUpdate(getItem(getBindingAdapterPosition()));

            Utils.setOnDoneListener(name, doneListener);
            Utils.setOnDoneListener(size, doneListener);
            Utils.setOnDoneListener(state, doneListener);

            itemView.setOnCreateContextMenuListener(this);
        }


        public void bind(Studio studio) {
            name.setText(studio.getName());
            size.setText(studio.getSize());
            updateState(studio.getState());
        }

        private void updateState(String newState) {
            state.setText(newState);
            changeRowColor(newState);
        }

        private void changeRowColor(String state) {
            switch (state) {
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
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (mListener != null) {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Studio studio = getItem(position);
                    switch (menuItem.getItemId()) {
                        case 2:
                            studio.setState("бронь");
                            break;
                        case 3:
                            studio.setState("продано");
                            break;
                    }
                    updateState(studio.getState());
                    mListener.onUpdate(studio);
                    return true;
                }
            }
            Log.d("MyLog", "loh");
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem booked = contextMenu.add(Menu.NONE, 2, 2, "Бронь");
            MenuItem sold = contextMenu.add(Menu.NONE, 3, 3, "Продано");
            booked.setOnMenuItemClickListener(this);
            sold.setOnMenuItemClickListener(this);
        }
    }

    public interface OnItemUpdateListener {
        void onUpdate(Studio studio);
    }
}

