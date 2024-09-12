package com.abmtech.trading.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abmtech.trading.databinding.ItemServiceLayBinding;
import com.abmtech.trading.databinding.ItemTransactionListBinding;
import com.abmtech.trading.model.ServiceModel;

import java.util.List;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ViewHolder> {
    private final List<ServiceModel> data;
    private final Context context;

    public ServicesAdapter(List<ServiceModel> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemServiceLayBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceModel current = data.get(position);

        holder.binding.textDescription.setText(current.getDescription());
        holder.binding.textHeading.setText(current.getHeading());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemServiceLayBinding binding;
        public ViewHolder(@NonNull ItemServiceLayBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
