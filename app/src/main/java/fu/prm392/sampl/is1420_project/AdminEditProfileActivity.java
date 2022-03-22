package fu.prm392.sampl.is1420_project;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import fu.prm392.sampl.is1420_project.dao.UserDAO;
import fu.prm392.sampl.is1420_project.dto.RestaurantDTO;
import fu.prm392.sampl.is1420_project.dto.UserDTO;
import fu.prm392.sampl.is1420_project.dto.UserDocument;
import fu.prm392.sampl.is1420_project.utils.Utils;
import fu.prm392.sampl.is1420_project.utils.Validation;

public class AdminEditProfileActivity extends AppCompatActivity {

    public static final int RC_IMAGE_PICKER = 1000;

    private TextInputLayout txtName, txtPhone, tilRole, tilStatus, txtEmail;
    private AutoCompleteTextView txtRole, txtStatus;
    private Button btnUpdate, btnDelete;
    private ImageView imgUser;
    private UserDTO userDTO = null;
    private List<String> roles, status;
    private List<RestaurantDTO> restaurantDTOList;
    private Utils utils;
    private Validation validation;

    private MaterialToolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_profile);
        txtEmail = findViewById(R.id.txtEmail);
        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        txtRole = findViewById(R.id.txtRole);
        txtStatus = findViewById(R.id.txtStatus);
        btnUpdate = findViewById(R.id.btnUpdateUser);
        tilRole = findViewById(R.id.tilRole);
        tilStatus = findViewById(R.id.tilStatus);
        btnDelete = findViewById(R.id.btnDeleteUser);
        imgUser = findViewById(R.id.imgUser);
        utils = new Utils();
        validation = new Validation();
        roles = new ArrayList<>();
        status = new ArrayList<>();
        topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Intent intent = this.getIntent();
        String userID = intent.getStringExtra("userID");
        if (userID == null) {
            Toast.makeText(this, "admin edit profile went wrong", Toast.LENGTH_SHORT).show();
            finish();
        }

        UserDAO userDAO = new UserDAO();
        roles.add("admin");
        roles.add("owner");
        roles.add("user");
        status.add("active");
        status.add("inactive");
        ArrayAdapter<String> adapterRoles = new ArrayAdapter<>(AdminEditProfileActivity.this, android.R.layout.simple_spinner_item, roles);
        ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(AdminEditProfileActivity.this, android.R.layout.simple_spinner_item, status);
        txtRole.setAdapter(adapterRoles);
        txtStatus.setAdapter(adapterStatus);

        userDAO.getUserById(userID).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    try {
                        userDTO = task.getResult().get("userInfo", UserDTO.class);

                        txtEmail.getEditText().setText(userDTO.getEmail());
                        txtEmail.setEnabled(false);
                        txtPhone.getEditText().setText(userDTO.getPhone());
                        txtName.getEditText().setText(userDTO.getName());
                        txtRole.setText(userDTO.getRole(), false);
                        txtStatus.setText(userDTO.getStatus(), false);

                        try {
                            Uri uri = Uri.parse(userDTO.getPhotoUri());
                            Glide.with(imgUser.getContext())
                                    .load(uri)
                                    .into(imgUser);
                        } catch (Exception e) {
                            imgUser.setImageResource(R.drawable.ic_baseline_account_circle_24);
                        }

                        if (userDTO.getStatus().equals("inactive")) {
                            btnDelete.setVisibility(View.GONE);
                        }

                        if (userDTO.getRole().equals("owner")) {
                            restaurantDTOList = task.getResult().toObject(UserDocument.class).getRestaurantsInfo();
                        }

                    } catch (Exception e) {
                        Log.d("DAO", e.toString());
                    }
                } else {
                    Toast.makeText(AdminEditProfileActivity.this,
                            "Fail to get user on server", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userDTO != null) {
                    String name = txtName.getEditText().getText().toString();
                    String phone = txtPhone.getEditText().getText().toString();
                    String role = txtRole.getText().toString();
                    String status = txtStatus.getText().toString();
                    if (isValid(name, phone, role, status)) {
                        userDTO.setName(name);
                        userDTO.setPhone(phone);
                        userDTO.setRole(role);
                        userDTO.setStatus(status);

                        userDAO.updateUser(userDTO, restaurantDTOList)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(AdminEditProfileActivity.this, "Update user successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                            startActivity(AdminEditProfileActivity.this.getIntent());

                                        } else {
                                            Toast.makeText(AdminEditProfileActivity.this,
                                                    "Fail to update User", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                } else {
                    Toast.makeText(AdminEditProfileActivity.this,
                            "admin update profile went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userDAO.deleteUser(userID)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AdminEditProfileActivity.this,
                                            "Delete successful", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(AdminEditProfileActivity.this.getIntent());
                                } else {
                                    Toast.makeText(AdminEditProfileActivity.this,
                                            "admin delete user went wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, RC_IMAGE_PICKER);
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
                ProgressDialog progressDialog = new ProgressDialog(AdminEditProfileActivity.this);
                utils.showProgressDialog(progressDialog, "Uploading ....", "Please wait for uploading image");
                userDAO.uploadImgUserToFirebase(uri)
                        .addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                try {
                                    if (task.isSuccessful()) {
                                        userDTO.setPhotoUri(task.getResult().toString());
                                        userDAO.updateUser(userDTO, restaurantDTOList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressDialog.cancel();
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(AdminEditProfileActivity.this, "Update Success"
                                                            , Toast.LENGTH_SHORT).show();
                                                    finish();
                                                    startActivity(AdminEditProfileActivity.this.getIntent());
                                                } else {
                                                    Toast.makeText(AdminEditProfileActivity.this, "Update fail"
                                                            , Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(AdminEditProfileActivity.this, "Update fail"
                                                , Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isValid(String fullName, String phone, String role, String status) {
        utils.clearError(txtName);
        utils.clearError(txtPhone);
        utils.clearError(tilRole);
        utils.clearError(tilStatus);

        boolean result = true;
        if (validation.isEmpty(status)) {
            utils.showError(tilStatus, "Status must not be blank");
            result = false;
        }
        if (validation.isEmpty(role)) {
            utils.showError(tilRole, "Role must not be blank");
            result = false;
        }
        if (!validation.isEmpty(phone)) {
            if (!validation.isValidPhoneNumber(phone)) {
                utils.showError(txtPhone, "Phone must be between 8 and 11 number");
                result = false;
            }
        }
        if (validation.isEmpty(fullName)) {
            utils.showError(txtName, "Username must not be blank");
            result = false;
        }
        return result;
    }
}