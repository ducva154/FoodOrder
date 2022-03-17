package fu.prm392.sampl.is1420_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import fu.prm392.sampl.is1420_project.dao.UserDAO;
import fu.prm392.sampl.is1420_project.dto.UserDTO;
import fu.prm392.sampl.is1420_project.utils.Utils;
import fu.prm392.sampl.is1420_project.utils.Validation;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout etName, etEmail, etPassword, etRepassword, etPhone, etAddress;
    private Utils utils;
    private Validation validation;
    private ProgressDialog prdRegister;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        utils = new Utils();
        validation = new Validation();
        prdRegister = new ProgressDialog(RegisterActivity.this);
        auth = FirebaseAuth.getInstance();
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etRepassword = findViewById(R.id.etRePassword);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
    }

    public void createAccount(View view) {
        String name = etName.getEditText().getText().toString();
        String email = etEmail.getEditText().getText().toString();
        String password = etPassword.getEditText().getText().toString();
        String repassword = etRepassword.getEditText().getText().toString();
        String phone = etPhone.getEditText().getText().toString();
        String address = etAddress.getEditText().getText().toString();
        if (isValid(name, email, password, repassword, phone, address)) {
            utils.showProgressDialog(prdRegister, "Register", "Wait for register");
            signUpWithEmail(email, password);
        } else {

        }
    }

    private void signUpWithEmail(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d("Email", "createUserWithEmail:success");
                FirebaseUser user = auth.getCurrentUser();
                UserDAO userDAO = new UserDAO();
                String name = etName.getEditText().getText().toString();
                String phone = etPhone.getEditText().getText().toString();
                String address = etAddress.getEditText().getText().toString();
                UserDTO userDTO = new UserDTO(user.getUid(), user.getEmail(), name, "user", "active", phone, address);
                userDAO.createUser(userDTO);
                sendEmailVerification();
                load(user);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Email", "createUserWithEmail:failure", e.getCause());
                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
                utils.showError(etEmail, "Email is invalid");
                load(null);
            }
        });
    }

    private void load(FirebaseUser user) {
        prdRegister.cancel();
        if (user != null) {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void sendEmailVerification() {
        FirebaseUser user = auth.getCurrentUser();

        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                auth.signOut();
                Log.d("Email", "Email sent.");
                Toast.makeText(RegisterActivity.this, "Email send to verify!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean isValid(String name, String email, String password, String repassword, String phone, String address) {
        utils.clearError(etName);
        utils.clearError(etEmail);
        utils.clearError(etPassword);
        utils.clearError(etRepassword);
        utils.clearError(etPhone);
        utils.clearError(etAddress);

        boolean result = true;
        if (validation.isEmpty(name)) {
            utils.showError(etName, "Please enter your name");
            result = false;
        }
        if (!validation.isValidEmail(email)) {
            utils.showError(etEmail, "Email is invalid or empty");
            result = false;
        }
        if (!validation.isValidPassword(password)) {
            utils.showError(etPassword, "Password must be more than 8 characters");
            result = false;
        }
        if (validation.isEmpty(repassword) || !repassword.equals(password)) {
            utils.showError(etRepassword, "Password must be a match");
            result = false;
        }
        if (validation.isEmpty(phone)) {
            utils.showError(etPhone, "Please enter your phone number");
            result = false;
        }
        if (validation.isEmpty(address)) {
            utils.showError(etAddress, "Please enter your address");
            result = false;
        }

        return result;
    }
}