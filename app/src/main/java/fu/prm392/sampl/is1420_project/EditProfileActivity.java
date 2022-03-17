package fu.prm392.sampl.is1420_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import fu.prm392.sampl.is1420_project.dao.RestaurantDAO;
import fu.prm392.sampl.is1420_project.dao.UserDAO;
import fu.prm392.sampl.is1420_project.dto.RestaurantDTO;
import fu.prm392.sampl.is1420_project.dto.UserDTO;
import fu.prm392.sampl.is1420_project.dto.UserDocument;
import fu.prm392.sampl.is1420_project.utils.Utils;
import fu.prm392.sampl.is1420_project.utils.Validation;

public class EditProfileActivity extends AppCompatActivity {

    public static final int RC_IMAGE_PICKER = 1000;
    private TextInputLayout txtName, txtPhone, txtEmail;
    private Button btnUpdate;
    private ImageView imgUser;
    private UserDTO userDTO = null;
    private Validation validation;
    private Utils utils;
    private List<RestaurantDTO> restaurantDTOList;
    private MaterialToolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        txtEmail = findViewById(R.id.txtEmail);
        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        btnUpdate = findViewById(R.id.btnUpdateUser);
        imgUser = findViewById(R.id.imgUser);
        topAppBar = findViewById(R.id.topAppBar);

        validation = new Validation();
        utils = new Utils();

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, RC_IMAGE_PICKER);
            }
        });

        UserDAO userDAO = new UserDAO();
        userDAO.getUserById(user.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                try {
                    userDTO = documentSnapshot.get("userInfo", UserDTO.class);
                    txtEmail.getEditText().setText(userDTO.getEmail());
                    txtEmail.setEnabled(false);
                    txtPhone.getEditText().setText(userDTO.getPhone());
                    txtName.getEditText().setText(userDTO.getName());
                    if (userDTO.getPhotoUri() != null) {
                        Uri uri = Uri.parse(userDTO.getPhotoUri());
                        Glide.with(imgUser.getContext())
                                .load(uri)
                                .into(imgUser);
                    } else {
                        imgUser.setImageResource(R.drawable.ic_baseline_account_circle_24);
                    }
                    RestaurantDAO restaurantDAO = new RestaurantDAO();
                    if (userDTO.getRole().equals("owner")) {
                        restaurantDTOList = documentSnapshot.toObject(UserDocument.class).getRestaurantsInfo();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "Fail to get user on server", Toast.LENGTH_SHORT).show();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userDTO != null) {
                    String name = txtName.getEditText().getText().toString();
                    String phone = txtPhone.getEditText().getText().toString();
                    if (isValid(name, phone)) {
                        userDTO.setName(name);
                        userDTO.setPhone(phone);
                        userDAO.updateUser(userDTO, restaurantDTOList).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    finish();
                                    startActivity(EditProfileActivity.this.getIntent());
                                    Toast.makeText(EditProfileActivity.this, "Update user information successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(EditProfileActivity.this, "Fail to update user", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, "Something went wrong in update user", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_IMAGE_PICKER && resultCode == RESULT_OK) {
            try {
                Uri uri = data.getData();
                UserDAO userDAO = new UserDAO();
                ProgressDialog progressDialog = new ProgressDialog(EditProfileActivity.this);
                utils.showProgressDialog(progressDialog, "Uploading ....", "Please wait for uploading image");
                userDAO.uploadImgUserToFirebase(uri).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            userDTO.setPhotoUri(task.getResult().toString());
                            userDAO.updateUser(userDTO, restaurantDTOList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.cancel();
                                    if (task.isSuccessful()) {
                                        Toast.makeText(EditProfileActivity.this, "Update Success"
                                                , Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(EditProfileActivity.this.getIntent());
                                        overridePendingTransition(0, 0);
                                    } else {
                                        Toast.makeText(EditProfileActivity.this, "Update fail"
                                                , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(EditProfileActivity.this, "Update fail"
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isValid(String name, String phone) {
        utils.clearError(txtName);
        utils.clearError(txtPhone);

        boolean result = true;
        if (!validation.isEmpty(phone)) {
            if (!validation.isValidPhoneNumber(phone)) {
                utils.showError(txtPhone, "Phone must be between 8 and 11 number");
                result = false;
            }
        }
        if (validation.isEmpty(name)) {
            utils.showError(txtName, "Username must not be blank");
            result = false;
        }
        return result;
    }
}