package fu.prm392.sampl.is1420_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import fu.prm392.sampl.is1420_project.fragments.PickLocationFragment;

public class GoogleMapActivity extends AppCompatActivity {

    public static final int RC_PERMISSTION_LOCATION = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                            , Manifest.permission.ACCESS_COARSE_LOCATION}
                    , RC_PERMISSTION_LOCATION);
        } else {
            loadData();
        }
    }

    private void loadData() {
        Intent intent = this.getIntent();
        String action = intent.getStringExtra("action");

        if (action.equals("pickLocation")){
            PickLocationFragment fragment = new PickLocationFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.layoutGoogleMap, fragment).commit();
        }else {
            Toast.makeText(this, "Location went wrong", Toast.LENGTH_SHORT).show();
            this.setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == RC_PERMISSTION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadData();
            } else {
                Toast.makeText(this, "Cannot open Google Map", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}