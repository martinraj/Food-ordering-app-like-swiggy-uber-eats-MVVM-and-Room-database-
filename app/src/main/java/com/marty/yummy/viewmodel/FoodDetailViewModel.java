package com.marty.yummy.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.marty.yummy.dbutilities.AppDatabase;
import com.marty.yummy.model.CartItem;
import com.marty.yummy.model.FoodDetails;
import com.marty.yummy.services.repository.FoodRepository;

import java.util.List;

public class FoodDetailViewModel extends AndroidViewModel {

    private AppDatabase db;
    private LiveData<List<CartItem>> cartItemsLiveData;
    private LiveData<FoodDetails> foodDetailsLiveData;

    public FoodDetailViewModel(@NonNull Application application) {
        super(application);
        init();
    }

    private void init() {
        db = AppDatabase.getDatabase(getApplication().getApplicationContext());
        subscribeToCartChanges();
    }

    private void subscribeToCartChanges() {
        cartItemsLiveData = db.cartItemDao().getCartItems();
    }

    public void subscribeForFoodDetails(String name){
        foodDetailsLiveData = db.foodDetailsDao().getFood(name);
    }

    public LiveData<FoodDetails> getFoodDetailsLiveData(){
        return foodDetailsLiveData;
    }

    public LiveData<List<CartItem>> getCartItemsLiveData() {
        return cartItemsLiveData;
    }

    public void updateCart(FoodDetails foodDetails){
        FoodRepository.getInstance().updateCart(db,foodDetails);
        db.foodDetailsDao().save(foodDetails);
    }
}
