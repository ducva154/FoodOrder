package fu.prm392.sampl.is1420_project;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fu.prm392.sampl.is1420_project.dao.UserDAO;
import fu.prm392.sampl.is1420_project.utils.Utils;
import fu.prm392.sampl.is1420_project.utils.Validation;

public class ChangePasswordActivity extends AppCompatActivity {
    private MaterialToolbar topAppBar;
    private TextInputLayout etOldPassword, etNewPassword, etConfirm;
    private Button btnChangePassword;
    private ProgressDialog prdChangePassword;
    private Utils utils;
    private Validation validation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        topAppBar = findViewById(R.id.topAppBar);
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirm = findViewById(R.id.etConfirm);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        prdChangePassword = new ProgressDialog(ChangePasswordActivity.this);
        utils = new Utils();
        validation = new Validation();

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPassword = etOldPassword.getEditText().getText().toString();
                String newPassword = etNewPassword.getEditText().getText().toString();
                String confirm = etConfirm.getEditText().getText().toString();

                if (isValid(oldPassword, newPassword, confirm)) {
                    try {
                        utils.showProgressDialog(prdChangePassword, "Change password", "Please wait for changing password");
                        changePassword(oldPassword, newPassword);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void changePassword(String oldPassword, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        UserDAO userDAO = new UserDAO();
                        userDAO.updatePassword(newPassword, user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ChangePasswordActivity.this, "Change password successfully", Toast.LENGTH_SHORT).show();
                                    prdChangePassword.cancel();
                                    finish();
                                } else {
                                    Toast.makeText(ChangePasswordActivity.this, "Fail to change password", Toast.LENGTH_SHORT).show();
                                    prdChangePassword.cancel();
                                }

                            }
                        });
                    } else {
                        utils.showError(etOldPassword, "Wrong old password");
                        prdChangePassword.cancel();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Change password went wrong"
                    , Toast.LENGTH_SHORT).show();
            prdChangePassword.cancel();
        }
    }

    private boolean isValid(String oldPassword, String newPassword, String confirm) {
        utils.clearError(etOldPassword);
        utils.clearError(etNewPassword);
        utils.clearError(etConfirm);

        boolean result = true;
        if (!validation.isValidPassword(oldPassword)) {
            utils.showError(etOldPassword, "Old password must be more than 6 character");
            result = false;
        }

        if (!validation.isValidPassword(newPassword)) {
            utils.showError(etNewPassword, "New Password must be more than 6 character");
            result = false;
        }
        if (validation.isEmpty(confirm) || !confirm.equals(newPassword)) {
            utils.showError(etConfirm, "Confirm must be a match");
            result = false;
        }
        return result;
    }
}