package fu.prm392.sampl.is1420_project.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fu.prm392.sampl.is1420_project.AdminEditProfileActivity;
import fu.prm392.sampl.is1420_project.CreateUserActivity;
import fu.prm392.sampl.is1420_project.R;
import fu.prm392.sampl.is1420_project.adapter.UserAdapter;
import fu.prm392.sampl.is1420_project.dao.UserDAO;
import fu.prm392.sampl.is1420_project.dto.UserDTO;
import fu.prm392.sampl.is1420_project.listener.OnItemUserClickListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManageUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageUserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView recycleUserView;
    private ExtendedFloatingActionButton btnCreateUser;
    private TextInputLayout etSearchUser;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ManageUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManageUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ManageUserFragment newInstance(String param1, String param2) {
        ManageUserFragment fragment = new ManageUserFragment();
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
        View view = inflater.inflate(R.layout.fragment_manage_user, container, false);

        recycleUserView = view.findViewById(R.id.recycleUserView);
        etSearchUser = view.findViewById(R.id.etSearchUser);
        btnCreateUser = view.findViewById(R.id.btnCreateUser);
        recycleUserView.setLayoutManager(new LinearLayoutManager(getActivity()));

        btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateUserActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData();
    }

    private void loadData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserDAO userDAO = new UserDAO();
        List<UserDTO> listUser = new ArrayList<>();
        userDAO.getAllUser().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    UserDTO dto = doc.get("userInfo", UserDTO.class);
                    if (!user.getUid().equals(dto.getUserID())) {
                        listUser.add(dto);
                    }
                }
                UserAdapter adapter = new UserAdapter(listUser, getActivity(), new OnItemUserClickListener() {
                    @Override
                    public void onItemClick(UserDTO item) {
                        Intent intent = new Intent(getActivity(), AdminEditProfileActivity.class);
                        intent.putExtra("userID", item.getUserID());
                        startActivity(intent);
                    }
                });
                recycleUserView.setAdapter(adapter);
            }
        });
    }
}