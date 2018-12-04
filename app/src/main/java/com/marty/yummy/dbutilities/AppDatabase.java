package com.marty.yummy.dbutilities;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.marty.yummy.model.CartItem;
import com.marty.yummy.model.FoodDetails;



/**
 * Created by Marty on 12/29/2017.
 */
@Database(entities = {FoodDetails.class, CartItem.class}, version = 2,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract FoodDetailsDao foodDetailsDao();
    public abstract CartItemDao cartItemDao();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"yummy").fallbackToDestructiveMigration().allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }
}
