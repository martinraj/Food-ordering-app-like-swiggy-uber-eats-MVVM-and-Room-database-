package com.marty.yummy.ui.adapters;


import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marty.yummy.R;
import com.marty.yummy.model.CartItem;
import com.marty.yummy.viewmodel.CartViewModel;

import java.util.ArrayList;
import java.util.List;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.RecyclerViewHolders> {

    private List<CartItem> cartList;
    private CartViewModel cartViewModel;

    public CartListAdapter(ArrayList<CartItem> cartItems, CartViewModel cartViewModel) {
        this.cartList = cartItems;
        this.cartViewModel = cartViewModel;
    }

    public void setData(List<CartItem> data) {
        this.cartList = data;
    }

    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        private AppCompatImageView iDelete;
        private TextView tName,tPrice,tTotalPrice,tQuantity;


        RecyclerViewHolders(View itemView) {
            super(itemView);

            iDelete = itemView.findViewById(R.id.i_delete);
            tName = itemView.findViewById(R.id.t_name);
            tPrice = itemView.findViewById(R.id.t_price);
            tTotalPrice = itemView.findViewById(R.id.t_total_price);
            tQuantity = itemView.findViewById(R.id.t_quantity);
            iDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view.getId()==R.id.i_delete){
                cartViewModel.removeItem(cartList.get(getAdapterPosition()).getName());
            }
        }
    }

    @NonNull
    @Override
    public RecyclerViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        @SuppressLint("InflateParams") View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cart_item, null);
        return new RecyclerViewHolders(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolders holder, int position) {
        CartItem cartItem = cartList.get(holder.getAdapterPosition());
        holder.tName.setText(cartItem.getName());
        holder.tPrice.setText("₹ "+cartItem.getPrice());
        holder.tQuantity.setText(String.valueOf(cartItem.getQuantity()));
        holder.tTotalPrice.setText("₹ "+String.valueOf(cartItem.getQuantity()*cartItem.getPrice()));
    }


    @Override
    public int getItemCount() {
        return this.cartList.size();
    }


    public long getItemId(int position) {
        return super.getItemId(position);
    }

}
