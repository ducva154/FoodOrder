package fu.prm392.sampl.is1420_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fu.prm392.sampl.is1420_project.adapter.FoodAdapter;
import fu.prm392.sampl.is1420_project.dao.FoodDAO;
import fu.prm392.sampl.is1420_project.dao.RestaurantDAO;
import fu.prm392.sampl.is1420_project.dto.FoodDTO;
import fu.prm392.sampl.is1420_project.dto.RestaurantDTO;
import fu.prm392.sampl.is1420_project.dto.RestaurantDocument;
import fu.prm392.sampl.is1420_project.listener.OnItemFoodClickListener;

public class RestaurantMenuActivity extends AppCompatActivity {

    private TextView txtRestaurantName, txtLocation, txtRate;
    private RecyclerView recycleMenuView;
    private ImageView imgRestaurant;
    private String restaurantID;
    private FloatingActionButton btnBack;
    private RestaurantDTO restaurantDTO;
    private List<FoodDTO> foodDTOList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_menu);
        txtRestaurantName = findViewById(R.id.txtRestaurantName);
        txtLocation = findViewById(R.id.txtLocation);
        txtRate = findViewById(R.id.txtRate);
        recycleMenuView = findViewById(R.id.recycleMenuView);
        imgRestaurant = findViewById(R.id.imgRestaurant);
        btnBack = findViewById(R.id.btnBack);
        recycleMenuView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        restaurantID = intent.getStringExtra("restaurantID");
        if (restaurantID != null) {
            loadData(restaurantID);
        }
    }

    private void loadData(String restaurantID) {
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        restaurantDAO.getRestaurantByID(restaurantID).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                restaurantDTO = doc.get("restaurantsInfo", RestaurantDTO.class);
                Log.d("USER", "dto: " + restaurantDTO);
                txtRestaurantName.setText(restaurantDTO.getName());
                txtLocation.setText(restaurantDTO.getLocation());
                txtRate.setText(String.format("%s", restaurantDTO.getRate()));
                try {
                    Uri uri = Uri.parse(restaurantDTO.getImage());
                    Glide.with(imgRestaurant.getContext())
                            .load(uri)
                            .into(imgRestaurant);
                } catch (Exception e) {
                    imgRestaurant.setImageResource(R.drawable.image_1);
                }

                FoodDAO foodDAO = new FoodDAO();
                foodDTOList = new ArrayList<>();
                foodDAO.getAllFoodByRestaurantID(restaurantID).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            FoodDTO foodDTO = doc.get("foodsInfo", FoodDTO.class);
                            foodDTOList.add(foodDTO);
                        }
                        FoodAdapter foodAdapter = new FoodAdapter(foodDTOList,
                                getApplicationContext(), new OnItemFoodClickListener() {
                            @Override
                            public void onItemClick(FoodDTO item) {
                                try {
                                    Intent intent = new Intent(RestaurantMenuActivity.this
                                            , EditFoodActivity.class);
                                    intent.putExtra("foodID", item.getFoodID());
                                    intent.putExtra("restaurantID", restaurantID);
                                    startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        recycleMenuView.setAdapter(foodAdapter);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RestaurantMenuActivity.this, "Get data failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}