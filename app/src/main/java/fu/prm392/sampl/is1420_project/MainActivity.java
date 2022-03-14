package fu.prm392.sampl.is1420_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import fu.prm392.sampl.is1420_project.dao.RestaurantDAO;
import fu.prm392.sampl.is1420_project.dao.UserDAO;
import fu.prm392.sampl.is1420_project.dto.RestaurantDTO;
import fu.prm392.sampl.is1420_project.fragments.UserHomeFragment;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        RestaurantDAO restaurantDAO = new RestaurantDAO();
//        RestaurantDTO restaurantDTO = new RestaurantDTO();
//        restaurantDTO.setName("My second restaurant");
//        restaurantDTO.setLocation("22 Hoan Kiem, Hanoi");
//        restaurantDTO.setRate(4.5);
//        restaurantDTO.setImage("https://images.unsplash.com/photo-1552566626-52f8b828add9?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8NXx8cmVzdGF1cmFudHN8ZW58MHx8MHx8&w=1000&q=80");
//        restaurantDAO.createRestaurent(restaurantDTO).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                Toast.makeText(MainActivity.this, "thanh cong", Toast.LENGTH_SHORT).show();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(MainActivity.this, "that bai", Toast.LENGTH_SHORT).show();
//            }
//        });

        setContentView(R.layout.activity_main);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        loadUI(user);
        
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch(item.getItemId()){
                    //check id
                    case R.id.pageHome:
                        selectedFragment = new UserHomeFragment();
                        break;
                    default:
                        return false;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();
                return true;
            }
        });

        //default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new UserHomeFragment()).commit();

    }

    private void loadUI(FirebaseUser user) {
        UserDAO userDAO = new UserDAO();
        userDAO.getUserById(user.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String role =documentSnapshot.getString("userInfo.role");
                Log.d("USER", "role: " + role);
                switch (role){
                    case "owner":
                        Intent intent = new Intent(MainActivity.this, OwnerMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        MainActivity.this.finish();
                        break;
                    case "admin":
                        break;
                }
            }
        });
    }
}