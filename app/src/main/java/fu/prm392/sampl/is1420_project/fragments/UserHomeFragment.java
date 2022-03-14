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
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fu.prm392.sampl.is1420_project.listener.OnItemClickListener;
import fu.prm392.sampl.is1420_project.R;
import fu.prm392.sampl.is1420_project.adapter.RestaurantAdapter;
import fu.prm392.sampl.is1420_project.dao.RestaurantDAO;
import fu.prm392.sampl.is1420_project.dto.RestaurantDTO;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserHomeFragment extends Fragment {

    private RecyclerView recyclerRestaurantView;
    private TextInputLayout etSearch;
    private List<RestaurantDTO> restaurantDTOList;
    private RestaurantAdapter restaurantAdapter;

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

        recyclerRestaurantView = view.findViewById(R.id.recycleRestaurantView);
        etSearch = view.findViewById(R.id.etSearch);
        recyclerRestaurantView.setLayoutManager(new LinearLayoutManager(getContext()));


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData();
    }

    private void loadData() {
        restaurantDTOList = new ArrayList<>();
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        restaurantDAO.getAllRestaurant().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                try {
                    for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                        Log.d("USER", "DocID: " + doc.getId());
                        Log.d("USER", "Doc: " + doc.get("restaurantInfo",
                                RestaurantDTO.class));
                        RestaurantDTO dto = doc.get("restaurantInfo", RestaurantDTO.class);
                        restaurantDTOList.add(dto);
                    }
                    restaurantAdapter = new RestaurantAdapter(restaurantDTOList, getContext(),
                            new OnItemClickListener() {
                        @Override
                        public void onItemClick(RestaurantDTO item) {
                        }
                    });
                    recyclerRestaurantView.setAdapter(restaurantAdapter);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}