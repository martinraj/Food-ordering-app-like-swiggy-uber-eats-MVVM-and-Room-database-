package com.marty.yummy.ui.adapters;


import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marty.yummy.R;
import com.marty.yummy.model.CartItem;
import com.marty.yummy.viewmodel.CartViewModel;

import java.util.ArrayList;
import java.util.List;

//Adapter for the recyclerview showing foods saved to cart.
public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.RecyclerViewHolders> {

    private List<CartItem> cartList;
    private final CartViewModel cartViewModel;

    //Parameterized constructor taking the cart item list.
    public CartListAdapter(ArrayList<CartItem> cartItems, CartViewModel cartViewModel) {
        this.cartList = cartItems;
        this.cartViewModel = cartViewModel;
    }

    //Set data method to provide the cart item list.
    public void setData(List<CartItem> data) {
        this.cartList = data;
    }

    //ViewHolder class.
    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView tName;
        private final TextView tPrice;
        private final TextView tTotalPrice;
        private final TextView tQuantity;


        RecyclerViewHolders(View itemView) {
            super(itemView);

            AppCompatImageView iDelete = itemView.findViewById(R.id.i_delete);
            tName = itemView.findViewById(R.id.t_name);
            tPrice = itemView.findViewById(R.id.t_price);
            tTotalPrice = itemView.findViewById(R.id.t_total_price);
            tQuantity = itemView.findViewById(R.id.t_quantity);
            iDelete.setOnClickListener(this);
        }

        //Deleting a cart item.
        @Override
        public void onClick(View view) {
            if(view.getId()==R.id.i_delete){
                cartViewModel.removeItem(cartList.get(getAdapterPosition()).getName());
            }
        }
    }

    //Create/recycle a view to be added to the recycler view.
    @NonNull
    @Override
    public RecyclerViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        @SuppressLint("InflateParams") View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cart_item, null);
        return new RecyclerViewHolders(layoutView);
    }

    //Populating fields of the views.
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolders holder, int position) {
        CartItem cartItem = cartList.get(holder.getAdapterPosition());
        holder.tName.setText(cartItem.getName());
        holder.tPrice.setText("₹ "+cartItem.getPrice());
        holder.tQuantity.setText(String.valueOf(cartItem.getQuantity()));
        holder.tTotalPrice.setText("₹ "+ cartItem.getQuantity() * cartItem.getPrice());
    }


    @Override
    public int getItemCount() {
        return this.cartList.size();
    }


    public long getItemId(int position) {
        return super.getItemId(position);
    }

}
