package com.marty.yummy.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.marty.yummy.R;
import com.marty.yummy.dbutilities.AppDatabase;
import com.marty.yummy.model.FoodDetails;
import com.marty.yummy.ui.IndividualActivity;
import com.marty.yummy.ui.RatingTextView;
import com.marty.yummy.utility.GlideApp;
import com.marty.yummy.utility.ObservableObject;

import java.util.List;

import static com.marty.yummy.ui.HomeScreenActivity.INTENT_UPDATE_FOOD;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.RecyclerViewHolders> {

    private List<FoodDetails> foodList;
    private Handler handler;

    public void setData(List<FoodDetails> data) {
        this.foodList = data;
    }

    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tName,tPrice,tCount;
        private ImageView iFood;
        private AppCompatImageView iPlus,iMinus;
        private RelativeLayout foodCard;
        private RatingTextView tRating;


        RecyclerViewHolders(View itemView) {
            super(itemView);
            foodCard = itemView.findViewById(R.id.food_card);
            tName = foodCard.findViewById(R.id.t_food_name);
            tPrice = foodCard.findViewById(R.id.t_price);
            tCount = foodCard.findViewById(R.id.t_count);
            tRating = foodCard.findViewById(R.id.t_rating);
            iPlus = foodCard.findViewById(R.id.i_plus);
            iMinus = foodCard.findViewById(R.id.i_minus);
            iFood = foodCard.findViewById(R.id.i_food_image);
            foodCard.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view.getId()==R.id.food_card){
                AppDatabase.getDatabase(view.getContext()).foodDetailsDao().save(foodList.get(getAdapterPosition()));
                Intent i = new Intent(view.getContext(),IndividualActivity.class);
                i.putExtra("name",foodList.get(getAdapterPosition()).getName());
                view.getContext().startActivity(i);
            }
        }
    }

    private Intent getUpdateIntent(int position) {
        Intent i = new Intent(INTENT_UPDATE_FOOD);
        i.putExtra("position",position);
        return i;
    }

    public FoodListAdapter(List<FoodDetails> listDetails) {
        this.foodList = listDetails;
        this.handler = new Handler();
    }

    @NonNull
    @Override
    public RecyclerViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        @SuppressLint("InflateParams") View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_food_item, null);
        return new RecyclerViewHolders(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolders holder, int position) {
        final FoodDetails foodDetails = foodList.get(holder.getAdapterPosition());
        holder.tName.setText(foodDetails.getName());
        holder.tPrice.setText("₹ " + foodDetails.getPrice());
        holder.tRating.setRating(foodDetails.getRating().floatValue());
        LoadImage li = new LoadImage(holder.iFood,holder.iMinus,holder.iPlus,holder.tCount,holder.getAdapterPosition());
        handler.post(li);
    }


    @Override
    public int getItemCount() {
        return this.foodList.size();
    }


    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public FoodDetails getItem(int position){
        if(position!= -1) {
            return foodList.get(position);
        }else {
            return null;
        }
    }

    private class LoadImage implements Runnable {
        ImageView imageView;
        TextView tCount;
        AppCompatImageView iPlus,iMinus;
        int position;

        LoadImage(ImageView imageView, AppCompatImageView iMinus, AppCompatImageView iPlus, TextView tCount, int adapterPosition) {
            this.imageView = imageView;
            this.iMinus = iMinus;
            this.iPlus = iPlus;
            this.tCount = tCount;
            this.position = adapterPosition;
        }

        @Override
        public void run() {
            GlideApp.with(imageView.getContext()).load(foodList.get(position).getImageUrl())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(R.drawable.ic_food)
                        .into(imageView);
            int quantity = AppDatabase.getDatabase(imageView.getContext()).cartItemDao().getCartCount(foodList.get(position).getName());
            foodList.get(position).setQuantity(quantity);
            tCount.setText(String.valueOf(quantity));
            iMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(foodList.get(position).getQuantity()!=0) {
                        foodList.get(position).setQuantity(foodList.get(position).getQuantity()-1);
                        tCount.setText(String.valueOf(foodList.get(position).getQuantity()));
                        ObservableObject.getInstance().updateValue(getUpdateIntent(position));
                    }
                }
            });
            iPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    foodList.get(position).setQuantity(foodList.get(position).getQuantity()+1);
                    tCount.setText(String.valueOf(foodList.get(position).getQuantity()));
                    ObservableObject.getInstance().updateValue(getUpdateIntent(position));
                }
            });
        }
    }
}
