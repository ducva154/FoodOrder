package fu.prm392.sampl.is1420_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import fu.prm392.sampl.is1420_project.adapter.FoodAdapter;
import fu.prm392.sampl.is1420_project.dao.RestaurantDAO;
import fu.prm392.sampl.is1420_project.dto.FoodDTO;
import fu.prm392.sampl.is1420_project.dto.RestaurantDocument;
import fu.prm392.sampl.is1420_project.listener.OnItemFoodClickListener;

public class OwnerMenuDetailActivity extends AppCompatActivity {

    private ExtendedFloatingActionButton btnAdd;
    private RecyclerView recycleMenuView;
    private String restaurantID;

    private MaterialToolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_menu_detail);
        btnAdd = findViewById(R.id.btnAdd);
        recycleMenuView = findViewById(R.id.recycleMenuView);
        recycleMenuView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        topAppBar=findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = this.getIntent();
        restaurantID = intent.getStringExtra("restaurantID");
//        if (restaurantID == null) {
//            Toast.makeText(this, "Something went wrong res" + restaurantID, Toast.LENGTH_SHORT).show();
//            finish();
//        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(OwnerMenuDetailActivity.this,AddFoodActivity.class);
                intent1.putExtra("restaurantID",restaurantID);
                startActivity(intent1);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        restaurantDAO.getRestaurantByID(restaurantID).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<FoodDTO> foodDTOList = documentSnapshot
                        .toObject(RestaurantDocument.class).getFoodsInfo();
                Log.d("USER", "dto: " + foodDTOList);
                if (foodDTOList != null){
                    FoodAdapter foodAdapter = new FoodAdapter(foodDTOList,
                            getApplicationContext(), new OnItemFoodClickListener() {
                        @Override
                        public void onItemClick(FoodDTO item) {
                            try {
                                Intent intent = new Intent(OwnerMenuDetailActivity.this, EditFoodActivity.class);
                                intent.putExtra("foodID", item.getFoodID());
                                intent.putExtra("restaurantID",restaurantID);
                                startActivity(intent);
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    recycleMenuView.setAdapter(foodAdapter);
                }

            }
        });
    }
}