package com.marty.yummy.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.marty.yummy.dbutilities.AppDatabase;
import com.marty.yummy.model.CartItem;
import com.marty.yummy.model.FoodDetails;
import com.marty.yummy.services.repository.FoodRepository;

import java.util.List;

import static com.marty.yummy.ui.HomeScreenActivity.ACTION_SORT_BY_PRICE;
import static com.marty.yummy.ui.HomeScreenActivity.ACTION_SORT_BY_RATING;


public class FoodViewModel extends AndroidViewModel {

    private AppDatabase db;
    private MediatorLiveData<List<FoodDetails>> foodDetailsMediatorLiveData = new MediatorLiveData<>();
    private LiveData<List<FoodDetails>> foodDetailsLiveDataSortPrice;
    private LiveData<List<FoodDetails>> foodDetailsLiveDataSortRating;
    private LiveData<List<CartItem>> cartItemsLiveData;
    private MutableLiveData<Boolean> isFoodCallInProgress = new MutableLiveData<>();
    private static String DEFAULT_SORT = ACTION_SORT_BY_PRICE;

    public FoodViewModel(@NonNull Application application) {
        super(application);
        init();
    }

    private void init() {
        db = AppDatabase.getDatabase(getApplication().getApplicationContext());
        updateFoodMenu();
        subscribeToFoodChanges();
        subscribeToCartChanges();
    }

    private void subscribeToCartChanges() {
        cartItemsLiveData = db.cartItemDao().getCartItems();
    }

    private void updateFoodMenu() {
        isFoodCallInProgress = FoodRepository.getInstance().getFoodMenu(getApplication().getApplicationContext());
    }

    private void subscribeToFoodChanges() {
        if(DEFAULT_SORT.equals(ACTION_SORT_BY_PRICE)){
            foodDetailsLiveDataSortPrice = db.foodDetailsDao().getFoodsByPrice();
            foodDetailsMediatorLiveData.addSource(foodDetailsLiveDataSortPrice, new Observer<List<FoodDetails>>() {
                @Override
                public void onChanged(@Nullable List<FoodDetails> foodDetails) {
                    foodDetailsMediatorLiveData.setValue(foodDetails);
                }
            });
        }else if(DEFAULT_SORT.equals(ACTION_SORT_BY_RATING)){
            foodDetailsLiveDataSortRating = db.foodDetailsDao().getFoodsByRating();
            foodDetailsMediatorLiveData.addSource(foodDetailsLiveDataSortRating, new Observer<List<FoodDetails>>() {
                @Override
                public void onChanged(@Nullable List<FoodDetails> foodDetails) {
                    foodDetailsMediatorLiveData.setValue(foodDetails);
                }
            });
        }
    }

    public MediatorLiveData<List<FoodDetails>> getFoodDetailsMutableLiveData() {
        return foodDetailsMediatorLiveData;
    }

    public void sortFood(String action){
        removeSource(DEFAULT_SORT);
        DEFAULT_SORT = action;
        subscribeToFoodChanges();
    }

    private void removeSource(String default_sort) {
        switch (default_sort){
            case ACTION_SORT_BY_PRICE:
                foodDetailsMediatorLiveData.removeSource(foodDetailsLiveDataSortPrice);
                break;
            case ACTION_SORT_BY_RATING:
                foodDetailsMediatorLiveData.removeSource(foodDetailsLiveDataSortRating);
                break;
        }
    }

    public LiveData<Boolean> isFoodUpdateInProgress(){
        return isFoodCallInProgress;
    }

    public LiveData<List<CartItem>> getCartItemsLiveData() {
        return cartItemsLiveData;
    }

    public void updateCart(FoodDetails foodDetails){
        FoodRepository.getInstance().updateCart(db,foodDetails);
    }

}
