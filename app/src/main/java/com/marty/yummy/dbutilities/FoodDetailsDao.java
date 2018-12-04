package com.marty.yummy.dbutilities;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.marty.yummy.model.FoodDetails;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


@Dao
public interface FoodDetailsDao {

    @Insert(onConflict = REPLACE)
    void save(List<FoodDetails> foodDetails);

    @Insert(onConflict = REPLACE)
    void save(FoodDetails foodDetails);

    @Query("DELETE FROM fooddetails WHERE name NOT IN (:nameList)")
    void deleteOtherFoods(List<String> nameList);

    @Query("DELETE FROM fooddetails")
    void deleteAll();

    @Query("SELECT * FROM fooddetails ORDER BY price ASC")
    LiveData<List<FoodDetails>> getFoodsByPrice();

    @Query("SELECT * FROM fooddetails ORDER BY rating DESC")
    LiveData<List<FoodDetails>> getFoodsByRating();

    @Query("SELECT * FROM fooddetails WHERE name = :name")
    LiveData<FoodDetails> getFood(String name);
}
