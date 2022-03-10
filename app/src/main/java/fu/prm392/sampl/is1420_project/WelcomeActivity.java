package fu.prm392.sampl.is1420_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void login(View view){
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    public void register(View view){
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
    }
}