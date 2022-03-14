package fu.prm392.sampl.is1420_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import fu.prm392.sampl.is1420_project.dao.RestaurantDAO;
import fu.prm392.sampl.is1420_project.dto.RestaurantDTO;
import fu.prm392.sampl.is1420_project.utils.Utils;
import fu.prm392.sampl.is1420_project.utils.Validation;

public class EditRestaurantActivity extends AppCompatActivity {

    public static final int RC_GALLERY = 1000;
    private TextInputLayout etRestaurantName, etLocation, etStatus;
    private Button btnChooseImg, btnLocation, btnUpdateRestaurant, btnDeleteRestaurant;
    private RecyclerView recycleMenu;
    private ImageView imgPhoto;
    private Uri imgUri;
    private Utils utils;
    private Validation validation;
    private ProgressDialog prdWait;
    private RestaurantDTO restaurantDTO, previousRestaurantDTO;
    private AutoCompleteTextView auComTxtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_restaurant);
        utils = new Utils();
        validation = new Validation();
        etRestaurantName = findViewById(R.id.etRestaurantName);
        etLocation = findViewById(R.id.etLocation);
        etStatus = findViewById(R.id.etStatus);
        btnChooseImg = findViewById(R.id.btnChooseImage);
        btnLocation = findViewById(R.id.btnLocation);
        btnUpdateRestaurant = findViewById(R.id.btnUpdateRestaurant);
        btnDeleteRestaurant = findViewById(R.id.btnDeleteRestaurant);
        recycleMenu = findViewById(R.id.recycleRestaurantView);
        imgPhoto = findViewById(R.id.img_photo);
        auComTxtStatus = findViewById(R.id.auComTxtStatus);

        Intent intent = this.getIntent();
        String restaurantID = intent.getStringExtra("restaurantID");
        if (restaurantID == null) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            finish();
        }

        List<String> status = new ArrayList<>();
        status.add("active");
        status.add("inactive");
        ArrayAdapter<String> adapterStatus = new ArrayAdapter<String>(EditRestaurantActivity.this,
                android.R.layout.simple_spinner_item, status);
        auComTxtStatus.setAdapter(adapterStatus);

        RestaurantDAO restaurantDAO = new RestaurantDAO();
        restaurantDAO.getRestaurantByID(restaurantID).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    restaurantDTO = doc.get("restaurantsInfo", RestaurantDTO.class);
                    previousRestaurantDTO = doc.get("restaurantsInfo", RestaurantDTO.class);
                    Log.d("USER", "dto: " + restaurantDTO);

                    etRestaurantName.getEditText().setText(restaurantDTO.getName());
                    etLocation.getEditText().setText(restaurantDTO.getLocation());
                    auComTxtStatus.setText(restaurantDTO.getStatus(),false);

                    try {
                        Uri uri = Uri.parse(restaurantDTO.getImage());
                        Glide.with(imgPhoto.getContext())
                                .load(uri)
                                .into(imgPhoto);
                    } catch (Exception e) {
                        imgPhoto.setImageResource(R.drawable.image_1);
                    }
                    if(restaurantDTO.getStatus().equals("inactive")){
                        btnDeleteRestaurant.setVisibility(View.GONE);
                    }
                }else{
                    Toast.makeText(EditRestaurantActivity.this, "Get data failed", Toast.LENGTH_SHORT).show();
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

    public void clickUpdate(View view){
        String name = etRestaurantName.getEditText().getText().toString();
        String location = etLocation.getEditText().getText().toString();
        String status = auComTxtStatus.getText().toString();

        if(isValid(name,location,status)){
            restaurantDTO.setName(name);
            restaurantDTO.setLocation(location);
            restaurantDTO.setStatus(status);

            ProgressDialog progressDialog = new ProgressDialog(EditRestaurantActivity.this);
            utils.showProgressDialog(progressDialog, "Updating ....", "Please wait for update field");
            updateRestaurant();
            progressDialog.cancel();
        }
    }

    private void updateRestaurant() {
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        restaurantDAO.updateRestaurant(restaurantDTO,previousRestaurantDTO,user.getUid())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent=new Intent(EditRestaurantActivity.this,OwnerMainActivity.class);
                            intent.putExtra("action","view_my_restaurant");
                            startActivity(intent);
                            Toast.makeText(EditRestaurantActivity.this, "Update restaurant success",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditRestaurantActivity.this, "Update restaurant fail",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void clickDelete(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(EditRestaurantActivity.this);
        alert.setTitle("Delete restaurant");
        alert.setMessage("Are you sure you want to delete?");
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                restaurantDTO.setStatus("inactive");
                updateRestaurant();
            }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
                dialog.cancel();
            }
        });
        alert.show();
    }

    private boolean isValid(String name, String location, String status) {
        utils.clearError(etRestaurantName);
        utils.clearError(etLocation);
        utils.clearError(etStatus);

        boolean result = true;

        if (validation.isEmpty(status)) {
            utils.showError(etStatus, "Status must not be blank");
            result = false;
        }
        if (validation.isEmpty(location)) {
            utils.showError(etLocation, "Location must not be blank");
            result = false;
        }
        if (validation.isEmpty(name)) {
            utils.showError(etRestaurantName, "Name must not be blank");
            result = false;
        }

        return result;
    }
}