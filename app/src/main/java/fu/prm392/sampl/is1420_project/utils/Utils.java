package fu.prm392.sampl.is1420_project.utils;

import android.app.ProgressDialog;
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputLayout;

public class Utils {
    public Utils() {
    }

    public void showError(TextInputLayout input, String textError) {
        input.setErrorEnabled(true);
        input.setError(textError);
        input.requestFocus();
    }

    public void clearError(TextInputLayout input){
        input.setErrorEnabled(false);
        input.setError(null);
    }

    public void showProgressDialog(ProgressDialog dialog, String title, String message){
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
