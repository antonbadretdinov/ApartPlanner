package com.example.apartplanner.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apartplanner.R;
import com.example.apartplanner.model.Studio;

public class StudioAdapter extends ListAdapter<Studio, StudioAdapter.StudioViewHolder> {

    protected StudioAdapter() {
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
    }

    @NonNull
    @Override
    public StudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_studio, parent, false);
        return new StudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudioViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


    public static class StudioViewHolder extends RecyclerView.ViewHolder {

        private final EditText name, size, state;
        private final TableRow tableRow;

        public StudioViewHolder(@NonNull View itemView) {
            super(itemView);

            tableRow = itemView.findViewById(R.id.tableRowUser);
            name = itemView.findViewById(R.id.editNameStudio);
            size = itemView.findViewById(R.id.editSizeStudio);
            state = itemView.findViewById(R.id.editStateStudio);

            name.setEnabled(false);
            size.setEnabled(false);
            state.setEnabled(false);
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
                    tableRow.setBackgroundColor(Color.parseColor("#87AEE4"));
                    break;
                case "бронь":
                    tableRow.setBackgroundColor(Color.parseColor("#ffcf5c"));
                    break;
                default:
                    tableRow.setBackgroundColor(Color.WHITE);
                    break;
            }
        }
    }

}

