package fu.prm392.sampl.is1420_project.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;

import fu.prm392.sampl.is1420_project.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PickLocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PickLocationFragment extends Fragment {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng marker;
    private String locationName;
    private Button btnPick;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PickLocationFragment() {
        // Required empty public constructor
    }

    private OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            Log.d("USER", "onMapReady");

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
            //if user reject permission
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Map went wrong", Toast.LENGTH_SHORT).show();
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
                return;
            }

            //get current user lcation
            LocationRequest mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(60000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationCallback mLocationCallback = new LocationCallback() {
            };

            fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        try {
                            Geocoder geocoder = new Geocoder(getContext());
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
                                    location.getLongitude(), 1);
                            Address address = addresses.get(0);
                            locationName = address.getAddressLine(0);
                            Log.d("USER", "location: " + locationName);
                            marker = new LatLng(location.getLatitude(), location.getLongitude());
                            //create maker options
                            MarkerOptions options = new MarkerOptions().position(marker).title("My location");
                            //Zoom map
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker, 15));
                            //Add marker to map
                            googleMap.addMarker(options);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getActivity(), "get location fail", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                    try {
                        marker = latLng;
                        Geocoder geocoder = new Geocoder(getContext());
                        List<Address> addresses = geocoder.getFromLocation(marker.latitude,
                                marker.longitude, 1);
                        Address address = addresses.get(0);
                        locationName = address.getAddressLine(0);
                        Log.d("USER", "location: " + locationName);
                        googleMap.clear();
                        MarkerOptions options = new MarkerOptions().position(marker).title("My location");
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker, 15));
                        googleMap.addMarker(options);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PickLocationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PickLocationFragment newInstance(String param1, String param2) {
        PickLocationFragment fragment = new PickLocationFragment();
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
        View view = inflater.inflate(R.layout.fragment_pick_location, container, false);
        btnPick = view.findViewById(R.id.btnPick);
        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (marker != null) {
                    try {
                        Intent intent = getActivity().getIntent();
                        intent.putExtra("lat", marker.latitude);
                        intent.putExtra("lng", marker.longitude);
                        intent.putExtra("locationName", locationName);
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        getActivity().finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please pick your location", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(onMapReadyCallback);
        }
    }
}