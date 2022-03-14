package fu.prm392.sampl.is1420_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import fu.prm392.sampl.is1420_project.dao.UserDAO;
import fu.prm392.sampl.is1420_project.utils.Utils;
import fu.prm392.sampl.is1420_project.utils.Validation;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout etEmail,etPassword;
    private Utils utils;
    private Validation validation;
    private FirebaseAuth auth;
    private ProgressDialog prdLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        utils = new Utils();
        validation = new Validation();
        auth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        prdLogin = new ProgressDialog(LoginActivity.this);

    }

    public void login(View view){
        String email = etEmail.getEditText().getText().toString();
        String password = etPassword.getEditText().getText().toString();
        if (isValidLogin(email, password)) {
            utils.showProgressDialog(prdLogin, "Login", "Please wait for login");
            signInWithEmail(email, password);
        } else {
            prdLogin.cancel();
        }
    }

    private void signInWithEmail(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d("Email", "signInWithEmail:success");
                FirebaseUser user = auth.getCurrentUser();
                if(user.isEmailVerified()){
                    load(user);
                }else{
                    Toast.makeText(LoginActivity.this, "Please verify your email address",
                            Toast.LENGTH_SHORT).show();
                    prdLogin.cancel();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
                prdLogin.cancel();
            }
        });
    }

    private void load(FirebaseUser user) {
        prdLogin.cancel();
        UserDAO userDAO = new UserDAO();
        userDAO.getUserById(user.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String role = documentSnapshot.getString("userInfo.role");
                switch (role){
                    case "user": {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        LoginActivity.this.finish();
                        break;
                    }
                    case "owner": {
                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    Log.w("Fetch Token Error", "Fetching FCM registration token failed", task.getException());
                                    return;
                                }
                                String token = task.getResult();
                                UserDAO userDAO = new UserDAO();
                                try {
                                    userDAO.saveToken(token, user.getUid());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        Intent intent = new Intent(LoginActivity.this, OwnerMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        LoginActivity.this.finish();
                        break;
                    }
                    case "admin":
                        break;
                    default:
                        Toast.makeText(LoginActivity.this, "Your role is invalid",
                                Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });

    }

    private boolean isValidLogin(String email, String password) {
        utils.clearError(etEmail);
        utils.clearError(etPassword);
        boolean result = true;
        if (!validation.isValidPassword(password)) {
            utils.showError(etPassword, "Password must be more than 8 character");
            result = false;
        }
        if (!validation.isValidEmail(email)) {
            utils.showError(etEmail, "Email is invalid");
            result = false;
        }
        return result;
    }
}