package fu.prm392.sampl.is1420_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fu.prm392.sampl.is1420_project.adapter.FoodAdapter;
import fu.prm392.sampl.is1420_project.dao.CartDAO;
import fu.prm392.sampl.is1420_project.dao.FoodDAO;
import fu.prm392.sampl.is1420_project.dao.RestaurantDAO;
import fu.prm392.sampl.is1420_project.dao.UserDAO;
import fu.prm392.sampl.is1420_project.dto.BasketDTO;
import fu.prm392.sampl.is1420_project.dto.CartDocument;
import fu.prm392.sampl.is1420_project.dto.FoodDTO;
import fu.prm392.sampl.is1420_project.dto.RestaurantDTO;
import fu.prm392.sampl.is1420_project.listener.OnItemFoodClickListener;

public class RestaurantMenuActivity extends AppCompatActivity {

    private TextView txtRestaurantName, txtLocation, txtRate;
    private RecyclerView recycleMenuView;
    private ImageView imgRestaurant;
    private String restaurantID;
    private FloatingActionButton btnBack;
    private RestaurantDTO restaurantDTO;
    private BasketDTO basketDTO;
    private List<FoodDTO> foodDTOList;
    private Button btnCart;


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
        btnCart = findViewById(R.id.btnCart);
        recycleMenuView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RestaurantMenuActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RestaurantMenuActivity.this, BasketActivity.class);
                intent.putExtra("basketID", basketDTO.getBasketID());
                startActivity(intent);
                finish();
            }
        });

        Intent intent = getIntent();
        restaurantID = intent.getStringExtra("restaurantID");
        if (restaurantID != null) {
            loadData(restaurantID);
            loadCart();
        }

    }

    private void loadCart() {
        UserDAO userDAO = new UserDAO();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        CartDAO cartDAO = new CartDAO();
        cartDAO.getCartByUserID(user.getUid()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                    CartDocument cartDocument = doc.toObject(CartDocument.class);
                    for (BasketDTO b : cartDocument.getBasketsInfo()) {
                        if (b.getRestaurantsInfo().getRestaurantID().equals(restaurantID)) {
                            basketDTO = b;
                            btnCart.setVisibility(View.VISIBLE);
                            btnCart.setText("Basket - " + basketDTO.getBasketQuantity() + " items - " + basketDTO.getBasketPrice());
                        }
                    }
                }
            }
        });
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
                                            , FoodDetailActivity.class);
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