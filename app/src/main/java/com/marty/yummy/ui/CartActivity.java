package com.marty.yummy.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.marty.yummy.R;
import com.marty.yummy.model.CartItem;
import com.marty.yummy.ui.adapters.CartListAdapter;
import com.marty.yummy.viewmodel.CartViewModel;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView cartList;
    TextView tDiscount,hDiscount,tItemsCost,tDelivery,hDelivery,tGrandTotal;
    TextInputEditText eCoupon;
    TextInputLayout eCouponLayout;
    AppCompatButton bApply;
    AppCompatImageView iRemoveCoupon;
    CartViewModel cartViewModel;
    Observer<List<CartItem>> cartObserver;
    Observer<Double> costObserver;
    Observer<String> errorObserver;
    CartListAdapter cartListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        setTitle(R.string.your_cart);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        cartList = findViewById(R.id.cart_list);
        tDiscount = findViewById(R.id.t_discount);
        tItemsCost = findViewById(R.id.t_total);
        hDiscount = findViewById(R.id.h_discount);
        tDelivery = findViewById(R.id.t_delivery);
        hDelivery = findViewById(R.id.h_delivery);
        iRemoveCoupon = findViewById(R.id.i_remove);
        tGrandTotal = findViewById(R.id.t_grand_total);
        eCouponLayout = findViewById(R.id.coupon_layout);
        eCoupon = findViewById(R.id.e_coupon);
        bApply = findViewById(R.id.b_apply);
        bApply.setOnClickListener(this);
        iRemoveCoupon.setOnClickListener(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        cartList.setLayoutManager(mLayoutManager);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        cartObserver = new Observer<List<CartItem>>() {
            @Override
            public void onChanged(@Nullable List<CartItem> cartItems) {
                cartListAdapter.setData(cartItems);
                cartListAdapter.notifyDataSetChanged();
                if(cartItems!=null && cartItems.size()==0){
                    finish();
                }
            }
        };
        costObserver = new Observer<Double>() {
            @Override
            public void onChanged(@Nullable Double aDouble) {
                updateUI(aDouble);
            }
        };
        errorObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable String error) {
                if(error!=null && error.isEmpty()){
                    eCouponLayout.setError(null);
                    eCouponLayout.setErrorEnabled(false);
                }else{
                    eCouponLayout.setError(error);
                    eCouponLayout.setErrorEnabled(true);
                }
            }
        };
        cartViewModel = ViewModelProviders.of(this).get(CartViewModel.class);
        cartListAdapter = new CartListAdapter(new ArrayList<CartItem>(),cartViewModel);
        cartList.setAdapter(cartListAdapter);
        cartViewModel.getCartItemsLiveData().observe(this,cartObserver);
        cartViewModel.getGrandTotal().observe(this,costObserver);
        cartViewModel.getErrorString().observe(this,errorObserver);
        eCoupon.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i==EditorInfo.IME_ACTION_DONE){
                    applyCoupon();
                    return false;
                }
                return false;
            }
        });
    }

    private void updateUI(Double grandTotal) {
        tItemsCost.setText(getString(R.string.rupee_symbol)+" "+cartViewModel.getTotalCost());
        tGrandTotal.setText(getString(R.string.rupee_symbol)+" "+String.valueOf(grandTotal));
        if(cartViewModel.getDiscountAmt()>0){
            hDiscount.setVisibility(View.VISIBLE);
            tDiscount.setVisibility(View.VISIBLE);
            hDiscount.setText(getString(R.string.discount)+" ( 20% )");
            tDiscount.setText(" - "+getString(R.string.rupee_symbol)+" "+String.valueOf(cartViewModel.getDiscountAmt()));
        }else{
            hDiscount.setVisibility(View.GONE);
            tDiscount.setVisibility(View.GONE);
        }
        if(cartViewModel.getDeliveryCost()>0){
            hDelivery.setText(getString(R.string.delivery_charges));
            tDelivery.setText(" + "+getString(R.string.rupee_symbol)+" "+String.valueOf(cartViewModel.getDeliveryCost()));
            tDelivery.setPaintFlags(0);
        }else{
            hDelivery.setText(getString(R.string.delivery_charges)+" ( Free )");
            tDelivery.setText(" + "+getString(R.string.rupee_symbol)+" 30.00");
            tDelivery.setPaintFlags(tDelivery.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.b_apply){
            applyCoupon();
        }else if(view.getId() == R.id.i_remove){
            eCoupon.setFocusable(true);
            eCoupon.setFocusableInTouchMode(true);
            eCoupon.setText("");
            eCoupon.setLongClickable(true);
            eCouponLayout.setErrorEnabled(false);
            eCouponLayout.setError(null);
            iRemoveCoupon.setVisibility(View.INVISIBLE);
            bApply.setVisibility(View.VISIBLE);
            cartViewModel.applyCoupon("");
        }
    }

    private void applyCoupon() {
        if(eCoupon.getText()!=null) {
            String coupon = eCoupon.getText().toString().trim().toUpperCase();
            if (!coupon.isEmpty() && (coupon.equals("FREEDEL") || coupon.equals("F22LABS"))) {
                eCouponLayout.setErrorEnabled(false);
                eCouponLayout.setError(null);
                cartViewModel.applyCoupon(coupon);
                eCoupon.setFocusable(false);
                eCoupon.setFocusableInTouchMode(false);
                eCoupon.setLongClickable(false);
                iRemoveCoupon.setVisibility(View.VISIBLE);
                bApply.setVisibility(View.INVISIBLE);
            } else {
                eCouponLayout.setErrorEnabled(true);
                eCouponLayout.setError("coupon not valid");
            }
        }
    }

    @Override
    protected void onDestroy() {
        cartViewModel.getCartItemsLiveData().removeObserver(cartObserver);
        cartViewModel.getGrandTotal().removeObserver(costObserver);
        cartViewModel.getErrorString().removeObserver(errorObserver);
        super.onDestroy();
    }
}
