package fu.prm392.sampl.is1420_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import fu.prm392.sampl.is1420_project.dao.UserDAO;
import fu.prm392.sampl.is1420_project.dto.UserDTO;
import fu.prm392.sampl.is1420_project.utils.Utils;
import fu.prm392.sampl.is1420_project.utils.Validation;

public class CreateUserActivity extends AppCompatActivity {

    public static final int RC_IMAGE_PICKER = 1001;

    private TextInputLayout txtName, txtPhone, txtEmail, txtPassword, txtConfirm, tilRole, tilStatus;
    private AutoCompleteTextView txtRole, txtStatus;
    private Button btnCreate, btnChooseImage;
    private ImageView imgUser;
    private List<String> roles, status;
    private Utils utils;
    private Uri imgUri = null;
    private FirebaseAuth mAuth, authCreate;
    private ProgressDialog progressDialog;
    private UserDTO userDTO = null;
    private Validation validation;
    private String password;
    private MaterialToolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        txtEmail = findViewById(R.id.txtEmail);
        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        txtRole = findViewById(R.id.txtRole);
        txtStatus = findViewById(R.id.txtStatus);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirm = findViewById(R.id.txtConfirm);
        btnCreate = findViewById(R.id.btnCreateUser);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        tilRole = findViewById(R.id.tilRole);
        tilStatus = findViewById(R.id.tilStatus);
        imgUser = findViewById(R.id.imgUser);
        progressDialog = new ProgressDialog(this);
        roles = new ArrayList<>();
        status = new ArrayList<>();
        topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        utils = new Utils();
        validation = new Validation();
        mAuth = FirebaseAuth.getInstance();
        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl("https://is1420-project.firebaseio.com")
                .setApiKey("AIzaSyBJ4ziy4kcYvbdYTczsWCY4EG3bT5jHT7c")
                .setApplicationId("is1420-project").build();
        try {
            FirebaseApp myApp = FirebaseApp.initializeApp(getApplicationContext(), firebaseOptions, "uniqueName");
            authCreate = FirebaseAuth.getInstance(myApp);
        } catch (IllegalStateException e) {
            authCreate = FirebaseAuth.getInstance(FirebaseApp.getInstance("uniqueName"));
        }

        UserDAO userDAO = new UserDAO();

        roles.add("admin");
        roles.add("owner");
        roles.add("user");
        status.add("active");
        status.add("inactive");
        ArrayAdapter<String> adapterRoles = new ArrayAdapter<>(CreateUserActivity.this, android.R.layout.simple_spinner_item, roles);
        ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(CreateUserActivity.this, android.R.layout.simple_spinner_item, status);
        txtRole.setAdapter(adapterRoles);
        txtStatus.setAdapter(adapterStatus);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmail.getEditText().getText().toString();
                String fullName = txtName.getEditText().getText().toString();
                String phone = txtPhone.getEditText().getText().toString();
                String role = txtRole.getText().toString();
                String status = txtStatus.getText().toString();
                password = txtPassword.getEditText().getText().toString();
                String confirm = txtConfirm.getEditText().getText().toString();
                if (isValid(email, password, confirm, fullName, phone, role, status)) {
                    userDTO = new UserDTO(null, email, fullName, phone, role, status, null);
                    utils.showProgressDialog(progressDialog, "Create user", "Please wait for create");
                    if (imgUri != null) {
                        uploadImageToStorage();
                    } else {
                        createUser(userDTO, password);
                    }

                }
            }
        });
    }

    private void uploadImageToStorage() {
        try {

            UserDAO userDAO = new UserDAO();
            userDAO.uploadImgUserToFirebase(imgUri)
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            try {
                                if (task.isSuccessful()) {
                                    Log.d("USER", task.getResult().toString());
                                    Uri uri = task.getResult();
                                    userDTO.setPhotoUri(uri.toString());
                                    createUser(userDTO, password);
                                } else {
                                    Toast.makeText(CreateUserActivity.this, "Update fail"
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

    private void createUser(UserDTO userDTO, String password) {
        authCreate.createUserWithEmailAndPassword(userDTO.getEmail(), password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            try {
                                Log.d("EMAIL", "createUserWithEmail:success");
                                FirebaseUser userCreate = authCreate.getCurrentUser();
                                UserDAO dao = new UserDAO();
                                userDTO.setUserID(userCreate.getUid());

                                dao.createUser(userDTO).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(CreateUserActivity.this,
                                                    "Create Success", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                                sendEmailVerification(userCreate);
                                progressDialog.cancel();
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("EMAIL", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateUserActivity.this, "Create failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                        }
                    }
                });
    }

    private void sendEmailVerification(FirebaseUser userCreate) {
        userCreate.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("EMAIL", "Email sent.");
                            Toast.makeText(CreateUserActivity.this, "Email send to verify!",
                                    Toast.LENGTH_LONG).show();
                            authCreate.signOut();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_IMAGE_PICKER && resultCode == RESULT_OK) {
            try {
                imgUri = data.getData();
                imgUser.setImageURI(imgUri);
                Log.d("USER", "Success");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isValid(String email, String password, String confirm, String fullName, String phone, String role, String status) {
        utils.clearError(txtEmail);
        utils.clearError(txtPassword);
        utils.clearError(txtConfirm);
        utils.clearError(txtName);
        utils.clearError(txtPhone);
        utils.clearError(tilRole);
        utils.clearError(tilStatus);

        Log.d("USER", "role: " + role);
        Log.d("USER", "status: " + status);

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
        if (!validation.isValidPassword(password)) {
            utils.showError(txtPassword, "Password must be more than 8 character");
            result = false;
        }
        if (validation.isEmpty(confirm) || !confirm.equals(password)) {
            utils.showError(txtConfirm, "Confirm must be a match");
            result = false;
        }
        if (!validation.isValidEmail(email)) {
            utils.showError(txtEmail, "Email is invalid");
            result = false;
        }
        return result;
    }
}