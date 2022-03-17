package fu.prm392.sampl.is1420_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    //go to login activity
    public void login(View view) {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    //go to register activity
    public void register(View view) {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
    }
}