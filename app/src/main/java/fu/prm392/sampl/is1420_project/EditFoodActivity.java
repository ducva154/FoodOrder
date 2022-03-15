package fu.prm392.sampl.is1420_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import fu.prm392.sampl.is1420_project.dao.FoodDAO;
import fu.prm392.sampl.is1420_project.dao.RestaurantDAO;
import fu.prm392.sampl.is1420_project.dto.FoodDTO;
import fu.prm392.sampl.is1420_project.utils.Utils;
import fu.prm392.sampl.is1420_project.utils.Validation;

public class EditFoodActivity extends AppCompatActivity {

    public static final int RC_GALLERY = 1000;
    private TextInputLayout etFoodName, etPrice, etDescription, etStatus;
    private Button btnChooseImg, btnUpdateFood, btnDeleteFood;
    private ImageView imgPhoto;
    private Uri uriImg;
    private Utils utils;
    private Validation validation;
    private ProgressDialog prdWait;
    private FoodDTO foodDTO, previousFoodDTO;
    private String foodID, restaurantID;
    private AutoCompleteTextView auComTxtStatus;

    private MaterialToolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food);
        utils = new Utils();
        validation = new Validation();
        etFoodName = findViewById(R.id.etFoodName);
        etPrice = findViewById(R.id.etFoodPrice);
        etDescription = findViewById(R.id.etDescription);
        etStatus = findViewById(R.id.etStatus);
        btnChooseImg = findViewById(R.id.btnChooseImage);
        imgPhoto = findViewById(R.id.img_photo);
        auComTxtStatus = findViewById(R.id.auComTxtStatus);
        btnUpdateFood = findViewById(R.id.btnUpdateFood);
        btnDeleteFood = findViewById(R.id.btnDeleteFood);
        topAppBar=findViewById(R.id.topAppBar);

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = this.getIntent();
        foodID = intent.getStringExtra("foodID");
        if (foodID == null) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            finish();
        }
        restaurantID = intent.getStringExtra("restaurantID");
        if (restaurantID == null) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            finish();
        }

        List<String> status = new ArrayList<>();
        status.add("available");
        status.add("unavailable");
        ArrayAdapter<String> adapterStatus = new ArrayAdapter<String>(EditFoodActivity.this,
                android.R.layout.simple_spinner_item, status);
        auComTxtStatus.setAdapter(adapterStatus);

        FoodDAO foodDAO = new FoodDAO();
        foodDAO.getFoodByID(foodID).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    foodDTO = doc.get("foodsInfo",FoodDTO.class);
                    previousFoodDTO = doc.get("foodsInfo",FoodDTO.class);
                    Log.d("USER", "dto: " + foodDTO);

                    etFoodName.getEditText().setText(foodDTO.getName());
                    etPrice.getEditText().setText(String.format("%s", foodDTO.getPrice()));
                    etDescription.getEditText().setText(foodDTO.getDescription());
                    auComTxtStatus.setText(foodDTO.getStatus(),false);

                    try {
                        Uri uri = Uri.parse(foodDTO.getImage());
                        Glide.with(imgPhoto.getContext())
                                .load(uri)
                                .into(imgPhoto);
                    } catch (Exception e) {
                        imgPhoto.setImageResource(R.drawable.image_1);
                    }
                    if(foodDTO.getStatus().equals("unavailable")){
                        btnDeleteFood.setVisibility(View.GONE);
                    }

                }else{
                    Toast.makeText(EditFoodActivity.this, "Get data failed", Toast.LENGTH_SHORT).show();
                }
            }
        });


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
                    ProgressDialog progressDialog = new ProgressDialog(EditFoodActivity.this);
                    utils.showProgressDialog(progressDialog, "Uploading ....", "Please wait for uploading image");
                    uploadImgFood(uriImg);
                    progressDialog.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void uploadImgFood(Uri uriImg) {
        try {
            FoodDAO foodDAO = new FoodDAO();
            foodDAO.uploadImgToFirebase(uriImg).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri uriRes = task.getResult();
                        foodDTO.setImage(uriRes.toString());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clickUpdate(View view){
        String name = etFoodName.getEditText().getText().toString();
        Double price = Double.valueOf(etPrice.getEditText().getText().toString());
        String description = etDescription.getEditText().getText().toString();
        String status = etStatus.getEditText().getText().toString();
        if (isValid(name,price,description,status)){
            foodDTO.setName(name);
            foodDTO.setPrice(price);
            foodDTO.setDescription(description);
            foodDTO.setStatus(status);

            ProgressDialog progressDialog = new ProgressDialog(EditFoodActivity.this);
            utils.showProgressDialog(progressDialog, "Updating ....", "Please wait for update food");
            updateFood();
            progressDialog.cancel();
        }
    }

    private void updateFood() {
        FoodDAO foodDAO = new FoodDAO();
        foodDAO.updateFood(foodDTO,previousFoodDTO,restaurantID)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
//                            Intent intent=new Intent(EditFoodActivity.this,OwnerMenuDetailActivity.class);
//                            intent.putExtra("action","view_my_food");
//                            startActivity(intent);
                            finish();
                            Toast.makeText(EditFoodActivity.this, "Update food success",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditFoodActivity.this, "Update food fail",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isValid(String name, Double price, String description, String status) {
        boolean result = true;
        utils.clearError(etFoodName);
        utils.clearError(etPrice);
        utils.clearError(etDescription);
        utils.clearError(etStatus);

        if(validation.isEmpty(name)) {
            utils.showError(etFoodName, "Name must not be blank");
            result = false;
        }
        if(validation.isEmpty(String.valueOf(price))) {
            utils.showError(etPrice, "Price must not be blank");
            result = false;
        }
        if(validation.isEmpty(description)) {
            utils.showError(etDescription, "Description must not be blank");
            result = false;
        }
        if (validation.isEmpty(status)) {
            utils.showError(etStatus, "Status must not be blank");
            result = false;
        }

        return result;
    }
}