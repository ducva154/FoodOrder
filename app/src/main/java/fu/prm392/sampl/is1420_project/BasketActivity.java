package fu.prm392.sampl.is1420_project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fu.prm392.sampl.is1420_project.adapter.BasketItemAdapter;
import fu.prm392.sampl.is1420_project.dao.BasketDAO;
import fu.prm392.sampl.is1420_project.dao.BasketItemDAO;
import fu.prm392.sampl.is1420_project.dto.BasketDocument;
import fu.prm392.sampl.is1420_project.dto.BasketItemDocument;
import fu.prm392.sampl.is1420_project.listener.OnItemBasketItemClickListener;

public class BasketActivity extends AppCompatActivity {
    public static final int RC_LOCATION = 1000;
    public static final int RC_PERMISSTION_LOCATION = 2500;
    public static final double RADIUS_IN_M = 10 * 1000;
    private TextView txtLocation, txtBasketPrice, txtDistance;
    private Button btnChangeLocation, btnOrder;
    private RecyclerView recycleBasketItemView;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GeoPoint currentGeo;
    private String locationName;
    private String basketID;
    private BasketDocument basketDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);
        txtLocation = findViewById(R.id.txtLocation);
        txtBasketPrice = findViewById(R.id.txtBasketPrice);
        txtDistance = findViewById(R.id.txtDistance);
        btnChangeLocation = findViewById(R.id.btnChangeLocation);
        btnOrder = findViewById(R.id.btnOrder);
        recycleBasketItemView = findViewById(R.id.recycleBasketItemView);
        recycleBasketItemView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Intent intent = getIntent();
        basketID = intent.getStringExtra("basketID");

        btnChangeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GoogleMapActivity.class);
                intent.putExtra("action", "pickLocation");
                startActivityForResult(intent, RC_LOCATION);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadBasket();
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, RC_PERMISSTION_LOCATION);
        } else {
            loadLocation();
        }
    }

    private void loadBasket() {
        BasketDAO basketDAO = new BasketDAO();
        basketDAO.getBasketByBasketID(basketID).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                    basketDocument = doc.toObject(BasketDocument.class);
                    txtBasketPrice.setText(basketDocument.getBasketsInfo().getBasketPrice() + "");
                    BasketItemDAO basketItemDAO = new BasketItemDAO();
                    basketItemDAO.getBasketItemByBasketID(basketDocument.getBasketsInfo().getBasketID()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                List<BasketItemDocument> list = new ArrayList<>();
                                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                    BasketItemDocument item = doc.toObject(BasketItemDocument.class);
                                    list.add(item);
                                }
                                BasketItemAdapter basketItemAdapter = new BasketItemAdapter(list, getApplicationContext(), new OnItemBasketItemClickListener() {
                                    @Override
                                    public void onItemClick(BasketItemDocument item) {

                                    }
                                });
                                recycleBasketItemView.setAdapter(basketItemAdapter);
                            }
                        }
                    });
                }
            }
        });
    }

    private void loadLocation() {
        //if user reject permission
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //get current location of user
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
        };

        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        if (currentGeo == null) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        try {
                            double lat = location.getLatitude();
                            double lng = location.getLongitude();
                            setTxtAddress(lat, lng);
                            GeoLocation geoLocation = new GeoLocation(lat, lng);
                            setDistance(geoLocation);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            setTxtAddress(currentGeo.getLatitude(), currentGeo.getLongitude());
            GeoLocation geoLocation = new GeoLocation(currentGeo.getLatitude(), currentGeo.getLongitude());
            setDistance(geoLocation);
        }
    }

    private void setDistance(GeoLocation geoLocation) {
        BasketDAO basketDAO = new BasketDAO();
        basketDAO.getBasketByBasketID(basketID).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                    basketDocument = doc.toObject(BasketDocument.class);
                    GeoPoint geoPoint = basketDocument.getBasketsInfo().getRestaurantsInfo().getGeoPoint();
                    double lat = geoPoint.getLatitude();
                    double lng = geoPoint.getLongitude();

                    GeoLocation docLocation = new GeoLocation(lat, lng);
                    double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, geoLocation);
                    if (distanceInM <= RADIUS_IN_M) {
                        //covert to km
                        distanceInM = distanceInM / 1000;
                        distanceInM = Math.round(distanceInM * 10.0) / 10.0;
                        txtDistance.setText(distanceInM + " km");
                        Log.d("distance", distanceInM + "");
                    }
                }
            }
        });

    }

    private void setTxtAddress(double latitude, double longitude) {
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address address = addresses.get(0);
            locationName = address.getAddressLine(0);
            Log.d("USER", "location: " + locationName);
            txtLocation.setText(locationName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_LOCATION && resultCode == RESULT_OK) {
            try {
                double lat = data.getDoubleExtra("lat", 0);
                double lng = data.getDoubleExtra("lng", 0);
                if (lat != 0 && lng != 0) {
                    currentGeo = new GeoPoint(lat, lng);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}