package fu.prm392.sampl.is1420_project.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fu.prm392.sampl.is1420_project.GoogleMapActivity;
import fu.prm392.sampl.is1420_project.R;
import fu.prm392.sampl.is1420_project.RestaurantMenuActivity;
import fu.prm392.sampl.is1420_project.SearchRestaurantActivity;
import fu.prm392.sampl.is1420_project.adapter.RestaurantAdapter;
import fu.prm392.sampl.is1420_project.dao.RestaurantDAO;
import fu.prm392.sampl.is1420_project.dto.RestaurantDTO;
import fu.prm392.sampl.is1420_project.listener.OnItemClickListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserHomeFragment extends Fragment {

    public static final int RC_LOCATION = 1000;
    public static final int RC_PERMISSTION_LOCATION = 2500;
    public static final double RADIUS_IN_M = 10 * 1000;
    private RecyclerView recyclerRestaurantView, recycleNearRestaurantView;
    private TextInputLayout etSearch;
    private Button btnChangeLocation;
    private TextView txtLocation, txtError;
    private List<RestaurantDTO> restaurantsList, restaurantsNearList;
    private RestaurantAdapter restaurantAdapter, restaurantNearAdapter;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GeoPoint currentGeo;
    private CardView cardNearMe;
    private String locationName;
    private LinearLayout lnlSearch;
    private TextInputEditText tetSearch;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserHomeFragment newInstance(String param1, String param2) {
        UserHomeFragment fragment = new UserHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);
        restaurantsNearList = new ArrayList<>();

        recyclerRestaurantView = view.findViewById(R.id.recycleRestaurantView);
        etSearch = view.findViewById(R.id.etSearch);
        btnChangeLocation = view.findViewById(R.id.btnChangeLocation);
        txtLocation = view.findViewById(R.id.txtLocation);
        txtError = view.findViewById(R.id.txtError);
        cardNearMe = view.findViewById(R.id.cardNearMe);
        recycleNearRestaurantView = view.findViewById(R.id.recycleNearRestaurantView);
        tetSearch = view.findViewById(R.id.tetSeach);
        recyclerRestaurantView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleNearRestaurantView.setLayoutManager(new LinearLayoutManager(getContext()));
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        btnChangeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), GoogleMapActivity.class);
                intent.putExtra("action", "pickLocation");
                startActivityForResult(intent, RC_LOCATION);
            }
        });

        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchRestaurantActivity.class);
                startActivity(intent);

            }
        });

        tetSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchRestaurantActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_LOCATION && resultCode == getActivity().RESULT_OK) {
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

    @Override
    public void onStart() {
        super.onStart();
        loadAllRestaurants();
        //Ask user permission for location
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, RC_PERMISSTION_LOCATION);
        } else {
            loadNearRestaurants();
        }
    }

    private void loadNearRestaurants() {
        //if user reject permission
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                            searchNearRestaurant(geoLocation);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        txtError.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            setTxtAddress(currentGeo.getLatitude(), currentGeo.getLongitude());
            GeoLocation geoLocation = new GeoLocation(currentGeo.getLatitude(), currentGeo.getLongitude());
            searchNearRestaurant(geoLocation);
        }
    }

    private void setTxtAddress(double latitude, double longitude) {
        try {
            Geocoder geocoder = new Geocoder(getContext());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address address = addresses.get(0);
            locationName = address.getAddressLine(0);
            Log.d("USER", "location: " + locationName);
            txtLocation.setText(locationName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchNearRestaurant(GeoLocation geoLocation) {
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        // Find restaurants within 10km of user's location
        List<Task<QuerySnapshot>> tasks = restaurantDAO.searchNearRestaurant(geoLocation, RADIUS_IN_M);
        restaurantsNearList.clear();
        Tasks.whenAllComplete(tasks).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
            @Override
            public void onComplete(@NonNull Task<List<Task<?>>> task) {
                try {
                    for (Task<QuerySnapshot> t : tasks) {
                        QuerySnapshot snap = t.getResult();
                        for (DocumentSnapshot doc : snap.getDocuments()) {
                            RestaurantDTO restaurantDTO = doc.get("restaurantsInfo", RestaurantDTO.class);
                            if (restaurantDTO.getStatus().equals("active")) {
                                GeoPoint geoPoint = restaurantDTO.getGeoPoint();
                                double lat = geoPoint.getLatitude();
                                double lng = geoPoint.getLongitude();

                                GeoLocation docLocation = new GeoLocation(lat, lng);
                                double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, geoLocation);
                                if (distanceInM <= RADIUS_IN_M) {
                                    //covert to km
                                    distanceInM = distanceInM / 1000;
                                    distanceInM = Math.round(distanceInM * 10.0) / 10.0;
                                    restaurantDTO.setDistance(String.format("%s", distanceInM));
                                    restaurantsNearList.add(restaurantDTO);
                                    Log.d("distance", distanceInM + "");
                                }
                            }
                        }
                    }
                    loadRestaurantsToRecycleView();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadRestaurantsToRecycleView() {
        if (restaurantsNearList.isEmpty()) {
            txtError.setVisibility(View.VISIBLE);
        } else {
            txtError.setVisibility(View.GONE);
            cardNearMe.setVisibility(View.VISIBLE);
            restaurantNearAdapter = new RestaurantAdapter(restaurantsNearList, getActivity(),
                    new OnItemClickListener() {
                        @Override
                        public void onItemClick(RestaurantDTO item) {
                            try {
                                Intent intent = new Intent(getActivity(), RestaurantMenuActivity.class);
                                intent.putExtra("restaurantID", item.getRestaurantID());
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
            recycleNearRestaurantView.setAdapter(restaurantNearAdapter);
        }
    }

    private void loadAllRestaurants() {
        restaurantsList = new ArrayList<>();
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        restaurantDAO.getAllRestaurant().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                try {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Log.d("USER", "DocID: " + doc.getId());
                        Log.d("USER", "Doc: " + doc.get("restaurantsInfo",
                                RestaurantDTO.class));
                        RestaurantDTO dto = doc.get("restaurantsInfo", RestaurantDTO.class);
                        restaurantsList.add(dto);
                    }
                    restaurantAdapter = new RestaurantAdapter(restaurantsList, getContext(),
                            new OnItemClickListener() {
                                @Override
                                public void onItemClick(RestaurantDTO item) {
                                    try {
                                        Intent intent = new Intent(getActivity(), RestaurantMenuActivity.class);
                                        intent.putExtra("restaurantID", item.getRestaurantID());
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    recyclerRestaurantView.setAdapter(restaurantAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}