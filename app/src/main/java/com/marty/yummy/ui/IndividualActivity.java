package com.marty.yummy.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.marty.yummy.R;
import com.marty.yummy.model.CartItem;
import com.marty.yummy.model.FoodDetails;
import com.marty.yummy.utility.GlideApp;
import com.marty.yummy.viewmodel.FoodDetailViewModel;

import java.util.List;

public class IndividualActivity extends AppCompatActivity implements View.OnClickListener {

    private FoodDetailViewModel foodDetailViewModel;
    Observer<FoodDetails> foodDetailObserver;
    private ImageView iFoodImage;
    private TextView tName,tCost,tQuantity,tTotalCost,tCartQuantity;
    private Toolbar cartView;
    Observer<List<CartItem>> cartObserver;
    private FoodDetails duplicateFoodDetails;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_layout);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        String foodId = "";
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            if(bundle.containsKey("name")) {
                foodId = bundle.getString("name");
            }
        }
        if(foodId !=null && !foodId.isEmpty()) {
            iFoodImage = findViewById(R.id.i_food_image);
            tName = findViewById(R.id.t_name);
            tCost = findViewById(R.id.t_cost);
            tQuantity = findViewById(R.id.t_quantity);
            AppCompatImageView iPlus = findViewById(R.id.i_plus);
            AppCompatImageView iMinus = findViewById(R.id.i_minus);
            iPlus.setOnClickListener(this);
            iMinus.setOnClickListener(this);
            cartView = findViewById(R.id.cart_view);
            tTotalCost = findViewById(R.id.t_total_price);
            tCartQuantity = findViewById(R.id.t_cart_count);
            AppCompatButton bCart = findViewById(R.id.b_cart);
            bCart.setOnClickListener(this);

            foodDetailViewModel = ViewModelProviders.of(this).get(FoodDetailViewModel.class);
            foodDetailViewModel.subscribeForFoodDetails(foodId);
            foodDetailObserver = new Observer<FoodDetails>() {
                @Override
                public void onChanged(@Nullable FoodDetails foodDetails) {
                    updateUI(foodDetails);
                }
            };
            cartObserver = new Observer<List<CartItem>>() {
                @Override
                public void onChanged(@Nullable List<CartItem> cartItems) {
                    updateCartUI(cartItems);
                }
            };
            foodDetailViewModel.getFoodDetailsLiveData().observe(this, foodDetailObserver);
            foodDetailViewModel.getCartItemsLiveData().observe(this, cartObserver);
        }
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.home){
            onBackPressed();
        }
        return true;
    }*/

    private void updateUI(FoodDetails foodDetails) {
        duplicateFoodDetails = foodDetails;
        if(foodDetails==null){
            return;
        }
        tName.setText(foodDetails.getName());
        tCost.setText(getString(R.string.rupee_symbol) + String.valueOf(foodDetails.getPrice()));
        GlideApp.with(this).load(foodDetails.getImageUrl())
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ic_food)
                .into(iFoodImage);
        tQuantity.setText(String.valueOf(foodDetails.getQuantity()));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.i_minus:
                if(duplicateFoodDetails.getQuantity()!=0) {
                    duplicateFoodDetails.setQuantity(duplicateFoodDetails.getQuantity()-1);
                    tQuantity.setText(String.valueOf(duplicateFoodDetails.getQuantity()));
                }
                foodDetailViewModel.updateCart(duplicateFoodDetails);
                break;
            case R.id.i_plus:
                duplicateFoodDetails.setQuantity(duplicateFoodDetails.getQuantity()+1);
                tQuantity.setText(String.valueOf(duplicateFoodDetails.getQuantity()));
                foodDetailViewModel.updateCart(duplicateFoodDetails);
                break;
            case R.id.b_cart:
                startActivity(new Intent(this,CartActivity.class));
                break;

        }
    }

    private void updateCartUI(List<CartItem> cartItems) {
        if(cartItems!=null && cartItems.size()>0){
            cartView.setVisibility(View.VISIBLE);
            Double cost = 0.0;
            int quantity = 0;
            for(CartItem cartItem:cartItems){
                cost = cost+(cartItem.getPrice()*cartItem.getQuantity());
                quantity = quantity+cartItem.getQuantity();
            }
            tCartQuantity.setText(String.valueOf(quantity));
            tTotalCost.setText(getString(R.string.rupee_symbol)+String.valueOf(cost));
        }else{
            cartView.setVisibility(View.GONE);
            tCartQuantity.setText("0");
            tTotalCost.setText(getString(R.string.rupee_symbol)+"0");
        }
    }

    @Override
    protected void onDestroy() {
        foodDetailViewModel.getFoodDetailsLiveData().removeObserver(foodDetailObserver);
        foodDetailViewModel.getCartItemsLiveData().removeObserver(cartObserver);
        super.onDestroy();
    }
}
