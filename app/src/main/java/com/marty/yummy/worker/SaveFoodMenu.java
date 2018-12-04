package com.marty.yummy.worker;

import android.os.AsyncTask;

import com.marty.yummy.dbutilities.AppDatabase;
import com.marty.yummy.model.FoodDetails;

import java.util.ArrayList;
import java.util.List;

public class SaveFoodMenu extends AsyncTask<Void,Void,Void> {
    private AppDatabase db;
    private List<FoodDetails> foodDetails;
    public SaveFoodMenu(AppDatabase db,List<FoodDetails> foodDetails) {
        this.db = db;
        this.foodDetails = foodDetails;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if(db!=null){
            if(foodDetails!=null && foodDetails.size()>0) {
                List<String> nameList = new ArrayList<>();
                for(int i=0;i<foodDetails.size();i++){
                    nameList.add(foodDetails.get(i).getName());
                    foodDetails.get(i).setQuantity(db.cartItemDao().getCartCount(foodDetails.get(i).getName()));
                }
                db.foodDetailsDao().save(foodDetails);
                db.foodDetailsDao().deleteOtherFoods(nameList);
            }else{
                db.foodDetailsDao().deleteAll();
            }
        }
        return null;
    }
}
