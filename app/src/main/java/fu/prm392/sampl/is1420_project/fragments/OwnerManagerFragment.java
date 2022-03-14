package fu.prm392.sampl.is1420_project.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import fu.prm392.sampl.is1420_project.CreateRestaurantActivity;
import fu.prm392.sampl.is1420_project.R;
import fu.prm392.sampl.is1420_project.adapter.RestaurantAdapter;
import fu.prm392.sampl.is1420_project.dao.UserDAO;
import fu.prm392.sampl.is1420_project.dto.RestaurantDTO;
import fu.prm392.sampl.is1420_project.dto.UserDocument;
import fu.prm392.sampl.is1420_project.listener.OnItemClickListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OwnerManagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OwnerManagerFragment extends Fragment {

    private ExtendedFloatingActionButton btnCreate;
    private RecyclerView recycleRestaurantView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OwnerManagerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OwnerManagerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OwnerManagerFragment newInstance(String param1, String param2) {
        OwnerManagerFragment fragment = new OwnerManagerFragment();
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
        View view = inflater.inflate(R.layout.fragment_owner_manager, container, false);

        btnCreate = view.findViewById(R.id.btnCreateRestaurant);
        recycleRestaurantView = view.findViewById(R.id.recycleRestaurantView);
        recycleRestaurantView.setLayoutManager(new LinearLayoutManager(getContext()));

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreateRestaurantActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserDAO userDAO = new UserDAO();
        userDAO.getUserById(user.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<RestaurantDTO> restaurantDTOList = documentSnapshot
                        .toObject(UserDocument.class).getRestaurantsInfo();
                Log.d("USER", "dto: " + restaurantDTOList);
                if (restaurantDTOList != null){
                    RestaurantAdapter restaurantAdapter = new RestaurantAdapter(restaurantDTOList
                            , getContext(), new OnItemClickListener() {
                        @Override
                        public void onItemClick(RestaurantDTO item) {

                        }
                    });
                    recycleRestaurantView.setAdapter(restaurantAdapter);
                }
            }
        });
    }
}