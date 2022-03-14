package fu.prm392.sampl.is1420_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import fu.prm392.sampl.is1420_project.dao.RestaurantDAO;
import fu.prm392.sampl.is1420_project.dao.UserDAO;
import fu.prm392.sampl.is1420_project.dto.RestaurantDTO;
import fu.prm392.sampl.is1420_project.dto.UserDTO;
import fu.prm392.sampl.is1420_project.utils.Utils;
import fu.prm392.sampl.is1420_project.utils.Validation;

public class CreateRestaurantActivity extends AppCompatActivity {

    public static final int RC_GALLERY = 1000;
    private TextInputLayout etRestaurantName, etLocation;
    private Button btnChooseImg, btnLocation;
    private RecyclerView recycleMenu;
    private ImageView imgPhoto;
    private Uri uriImg;
    private Utils utils;
    private Validation validation;
    private ProgressDialog prdWait;
    private RestaurantDTO restaurantDTO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_restaurant);
        utils = new Utils();
        validation = new Validation();
        etRestaurantName = findViewById(R.id.etRestaurantName);
        etLocation = findViewById(R.id.etLocation);
        btnChooseImg = findViewById(R.id.btnChooseImage);
        btnLocation = findViewById(R.id.btnLocation);
        recycleMenu = findViewById(R.id.recycleRestaurantView);
        imgPhoto = findViewById(R.id.img_photo);

        btnChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, RC_GALLERY);
            }
        });

        //location
        //menu
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GALLERY) {
            if (resultCode == RESULT_OK) {
                try {
                    uriImg = data.getData();
                    imgPhoto.setImageURI(uriImg);
                    Log.d("USER", "Success");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void clickCreate(View view){
        String name = etRestaurantName.getEditText().getText().toString();
        String location = etLocation.getEditText().getText().toString();
        if(isValid(name,location)){
            restaurantDTO = new RestaurantDTO();
            restaurantDTO.setName(name);
            restaurantDTO.setLocation(location);
            restaurantDTO.setStatus("active");

            prdWait=new ProgressDialog(this);
            utils.showProgressDialog(prdWait,"Create","Please wait to create restaurant");

            if (uriImg != null) {
                uploadImageToStorage();
            } else {
                createRestaurant();
            }
        }
    }

    private void uploadImageToStorage() {
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        restaurantDAO.uploadImgToFirebase(uriImg).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri uri = task.getResult();
                    restaurantDTO.setImage(uri.toString());

                    createRestaurant();
                    System.out.println(("URL IMAGE " + restaurantDTO.getImage()));
                } else {
                    Toast.makeText(CreateRestaurantActivity.this, "upload image fail",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createRestaurant(){
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserDAO userDAO = new UserDAO();
        userDAO.getUserById(user.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserDTO owner = documentSnapshot.get("userInfo",UserDTO.class);
                restaurantDAO.createRestaurent(restaurantDTO,owner).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        prdWait.cancel();
                        Intent intent=new Intent(CreateRestaurantActivity.this,OwnerMainActivity.class);
                        intent.putExtra("action","view_my_restaurant");
                        startActivity(intent);
                        Toast.makeText(CreateRestaurantActivity.this, "Create restaurant successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateRestaurantActivity.this, "Create Fail" + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateRestaurantActivity.this,
                        "Fail to get user on server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValid(String name, String location){
        boolean result = true;
        utils.clearError(etRestaurantName);
        utils.clearError(etLocation);

        if(validation.isEmpty(location)) {
            utils.showError(etLocation, "Location must not be blank");
            result = false;
        }
        if(validation.isEmpty(name)) {
            utils.showError(etRestaurantName, "Name must not be blank");
            result = false;
        }

        return result;
    }
}