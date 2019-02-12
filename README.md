# Food ordering app like Swiggy and Uber eats
Food ordering app using MVVM architecture patterns, Architecture Lifecycle components, Retrofit2 and Room database.

In this demo, I have covered **Mediator Live data, Mutable live data, Observable, Observers, Retrofit and Room using same POJO.**

## APP UI
![App UI](https://github.com/martinraj/MVVM-architecture-lifecycle-components-and-Room-database-project/blob/master/screenshots%20and%20demo/Screenshot_20181205-024745.png)   ![](https://github.com/martinraj/MVVM-architecture-lifecycle-components-and-Room-database-project/blob/master/screenshots%20and%20demo/Screenshot_20181205-024753.png)   ![](https://github.com/martinraj/MVVM-architecture-lifecycle-components-and-Room-database-project/blob/master/screenshots%20and%20demo/Screenshot_20181205-024829.png)

## Room and Retrofit using same Model

I have used same models for Room and Retrofit2 library as both are configured using annotaions.

```
@Entity
public class FoodDetails {

    @PrimaryKey                   //Room annotation
    @SerializedName("item_name")  //Retrofit annotation
    @Expose
    @NonNull
    private String name;

    @SerializedName("item_price")
    @Expose
    private Double price;

    @SerializedName("average_rating")
    @Expose
    private Double rating;

    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    @SerializedName("item_quantity")
    @Expose
    private Integer quantity = 0;

    // For Retrofit
    public FoodDetails(@NonNull String name, Double price, Double rating, String imageUrl,Integer quantity) {
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
    }
    
    // Getters and Setters for Room
    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
```

## Sorting list elements in Room Database and observing using Mediator Live Data

Sorting of food items is done based on Pricing and rating. I have used Mediator Live data to observe same types of Live Data from different db queries. We are going to expose Mediator Live Data to UI which will observe data changes from other two Live Data.

```
...\
public class FoodViewModel extends AndroidViewModel {
    MediatorLiveData<List<FoodDetails>> foodDetailsMediatorLiveData = new MediatorLiveData<>();

    private LiveData<List<FoodDetails>> foodDetailsLiveDataSortPrice;
    private LiveData<List<FoodDetails>> foodDetailsLiveDataSortRating;
    private AppDatabase db;
    private static String DEFAULT_SORT = ACTION_SORT_BY_PRICE;
    
    public FoodViewModel(@NonNull Application application) {
        super(application);
        init();
    }

    private void init() {
        db = AppDatabase.getDatabase(getApplication().getApplicationContext());
        subscribeToFoodChanges();
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

...\
```

Manually we are triggering observers by using **foodDetailsMediatorLiveData.setValue(foodDetails);**

## Room DB query for Sorting food items
```
@Dao
public interface FoodDetailsDao {

    ...\

    @Query("SELECT * FROM fooddetails ORDER BY price ASC")
    LiveData<List<FoodDetails>> getFoodsByPrice();

    @Query("SELECT * FROM fooddetails ORDER BY rating DESC")
    LiveData<List<FoodDetails>> getFoodsByRating();

    ...\
}
```

## Observing data changes in Activity/Fragment

We have to create observers for live data. Only then if any data is changed, observers will be notified with updated data by ViewModel.

```
...\

@Override
protected void onCreate(Bundle savedInstanceState) {
    .../
    
    // Define viewmodel
    FoodViewModel foodViewModel = ViewModelProviders.of(this).get(FoodViewModel.class);
    
    // Define Observer with actions to be done after update
    Observer<List<FoodDetails>> foodMenuObserver = new Observer<List<FoodDetails>>() {
            @Override
            public void onChanged(@Nullable List<FoodDetails> foodDetails) {
                if(foodDetails!=null){
                    foodListAdapter.setData(foodDetails);
                    foodListAdapter.notifyDataSetChanged();
                    runLayoutAnimation(foodList);
                }else{
                    Log.e("Food details","null");
                }
            }
        };
        
    // set observer to watch for data changes
    foodViewModel.getFoodDetailsMutableLiveData().observe(this, foodMenuObserver);
    
    .../
}

...\

```
## Retrofit to get data from server

We have used Retrofit to get data from Rest API and updating db using Room in Intent Service

```
  public interface YummyAPIServices {

    @GET("/data.json")
    Call<List<FoodDetails>> getFoodData();
}

```

```
public class FoodRepository {

    private static FoodRepository instance;
    private static final String TAG = "FoodRepository";

    private YummyAPIServices yummyAPIServices = APIClient.getClient().create(YummyAPIServices.class);

    public MutableLiveData<Boolean> getFoodMenu(final Context context){

        final MutableLiveData<Boolean> isFoodCallOngoing = new MutableLiveData<>();
        isFoodCallOngoing.setValue(true);

        yummyAPIServices.getFoodData().enqueue(new Callback<List<FoodDetails>>() {
            @Override
            public void onResponse(Call<List<FoodDetails>> call, Response<List<FoodDetails>> response) {
                if(response.isSuccessful()) {
                    new SaveFoodMenu(AppDatabase.getDatabase(context), response.body()).execute();
                    isFoodCallOngoing.setValue(false); // on success we are updating empty mutable live data with new food menus
                }else{
                    Log.e(TAG,"response not successful");
                }
            }

            @Override
            public void onFailure(Call<List<FoodDetails>> call, Throwable t) {
                Log.e(TAG,t.toString());
            }
        });
        return isFoodCallOngoing; // returns empty mutable live data 
    }

    public static FoodRepository getInstance() {
        if(instance == null){
            synchronized (FoodRepository.class){
                if(instance == null){
                    instance = new FoodRepository();
                }
            }
        }
        return instance;
    }
}
```
## API Client for Retrofit

We are using singleton to get Retrofit client.
```
public class APIClient {

    private static final String BASE_URL = "YOUR_SERVER_BASE_URL";
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
```

## Project Structure

This is the overall project structure of this project.


![Project Structure](https://github.com/martinraj/MVVM-architecture-lifecycle-components-and-Room-database-project/blob/master/screenshots%20and%20demo/Screenshot%20(114).png)
