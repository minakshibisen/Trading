package com.abmtech.trading.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abmtech.trading.databinding.ItemPricesLayBinding;
import com.abmtech.trading.model.PriceModel;

import java.util.List;

public class LivePricesAdapter extends RecyclerView.Adapter<LivePricesAdapter.ViewHolder> {
    private final Context context;
    private final List<PriceModel> data;

    public LivePricesAdapter(Context context, List<PriceModel> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemPricesLayBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PriceModel current = data.get(position);

        holder.binding.textHeading.setText(current.getHeading());
        holder.binding.textSubHeading.setText(current.getSubHeading());
        holder.binding.textAskPrice.setText(current.getAskPrice());
        holder.binding.textBidPrice.setText(current.getBidPrice());
        holder.binding.textHeadPrice.setText(current.getHeadPrice());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemPricesLayBinding binding;
        public ViewHolder(@NonNull ItemPricesLayBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
