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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import fu.prm392.sampl.is1420_project.BasketActivity;
import fu.prm392.sampl.is1420_project.R;
import fu.prm392.sampl.is1420_project.adapter.BasketAdapter;
import fu.prm392.sampl.is1420_project.dao.CartDAO;
import fu.prm392.sampl.is1420_project.dao.UserDAO;
import fu.prm392.sampl.is1420_project.dto.BasketDTO;
import fu.prm392.sampl.is1420_project.dto.CartDocument;
import fu.prm392.sampl.is1420_project.listener.OnItemBasketClickListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment {

    private RecyclerView recycleCartView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
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
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        recycleCartView = view.findViewById(R.id.recycleCartView);
        recycleCartView.setLayoutManager(new LinearLayoutManager(getContext()));
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
                CartDAO cartDAO = new CartDAO();
                cartDAO.getCartByUserID(user.getUid()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                            CartDocument cartDocument = doc.toObject(CartDocument.class);
                            List<BasketDTO> basketDTOList = cartDocument.getBasketsInfo();
                            if (basketDTOList != null) {
                                BasketAdapter basketAdapter = new BasketAdapter(basketDTOList,
                                        getActivity(), new OnItemBasketClickListener() {
                                    @Override
                                    public void onItemClick(BasketDTO item) {
                                        Intent intent = new Intent(getActivity(), BasketActivity.class);
                                        intent.putExtra("basketID", item.getBasketID());
                                        startActivity(intent);
                                    }
                                });
                                recycleCartView.setAdapter(basketAdapter);
                            }
                        }
                    }
                });
            }
        });
    }
}