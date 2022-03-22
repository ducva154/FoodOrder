package fu.prm392.sampl.is1420_project;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;

import fu.prm392.sampl.is1420_project.dao.FoodDAO;
import fu.prm392.sampl.is1420_project.dao.RestaurantDAO;
import fu.prm392.sampl.is1420_project.dto.FoodDTO;
import fu.prm392.sampl.is1420_project.dto.RestaurantDTO;
import fu.prm392.sampl.is1420_project.utils.Utils;
import fu.prm392.sampl.is1420_project.utils.Validation;

public class AddFoodActivity extends AppCompatActivity {

    public static final int RC_GALLERY = 1000;
    private TextInputLayout etFoodName, etPrice, etDescription;
    private Button btnChooseImg;
    private ImageView imgPhoto;
    private Uri uriImg;
    private Utils utils;
    private Validation validation;
    private ProgressDialog prdWait;
    private FoodDTO foodDTO;
    String restaurantID;

    private MaterialToolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        utils = new Utils();
        validation = new Validation();
        etFoodName = findViewById(R.id.etFoodName);
        etPrice = findViewById(R.id.etFoodPrice);
        etDescription = findViewById(R.id.etDescription);
        btnChooseImg = findViewById(R.id.btnChooseImage);
        imgPhoto = findViewById(R.id.img_photo);
        topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = this.getIntent();
        restaurantID = intent.getStringExtra("restaurantID");
        if (restaurantID == null) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, RC_GALLERY);
            }
        });
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

    public void clickAdd(View view) {
        String name = etFoodName.getEditText().getText().toString();
        double price;
        try {
            price = Double.parseDouble(etPrice.getEditText().getText().toString());
        } catch (Exception e) {
            price = 0;
        }

        String description = etDescription.getEditText().getText().toString();
        if (isValid(name, price, description)) {
            foodDTO = new FoodDTO();
            foodDTO.setName(name);
            foodDTO.setPrice(price);
            foodDTO.setDescription(description);
            foodDTO.setStatus("available");
            prdWait = new ProgressDialog(this);
            utils.showProgressDialog(prdWait, "Create", "Please wait to add food");

            if (uriImg != null) {
                uploadImageToStorage();
            } else {
                addFood();
            }
        }
    }

    private void uploadImageToStorage() {
        FoodDAO foodDAO = new FoodDAO();
        foodDAO.uploadImgToFirebase(uriImg).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri uri = task.getResult();
                    foodDTO.setImage(uri.toString());
                    addFood();
                    System.out.println(("URL IMAGE " + foodDTO.getImage()));
                } else {
                    Toast.makeText(AddFoodActivity.this, "upload image fail",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addFood() {
        FoodDAO foodDAO = new FoodDAO();
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        restaurantDAO.getRestaurantByID(restaurantID).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                RestaurantDTO restaurantDTO = documentSnapshot.get("restaurantsInfo", RestaurantDTO.class);
                foodDAO.addFood(foodDTO, restaurantDTO).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        prdWait.cancel();
                        Toast.makeText(AddFoodActivity.this, "Add food successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddFoodActivity.this, "Add Fail" + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddFoodActivity.this,
                        "Fail to get restaurant on server", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //check valid
    private boolean isValid(String name, double price, String description) {
        boolean result = true;
        utils.clearError(etFoodName);
        utils.clearError(etPrice);
        utils.clearError(etDescription);

        if (validation.isEmpty(name)) {
            utils.showError(etFoodName, "Name must not be blank");
            result = false;
        }
        if (price == 0) {
            utils.showError(etPrice, "Price must be double");
            result = false;
        }
        if (validation.isEmpty(description)) {
            utils.showError(etDescription, "Description must not be blank");
            result = false;
        }

        return result;
    }
}