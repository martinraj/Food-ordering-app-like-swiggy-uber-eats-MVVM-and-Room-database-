package com.marty.yummy.dbutilities;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.marty.yummy.model.CartItem;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


@Dao
public interface CartItemDao {
    @Query("SELECT * FROM cartitem")
    LiveData<List<CartItem>> getCartItems();

    @Insert(onConflict = REPLACE)
    void add(CartItem cartItem);

    @Query("DELETE FROM cartitem WHERE item_name = :name")
    void deleteCartItem(String name);

    @Query("SELECT quantity FROM cartitem WHERE item_name = :name")
    int getCartCount(String name);
}
