package fu.prm392.sampl.is1420_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fu.prm392.sampl.is1420_project.adapter.UserAdapter;
import fu.prm392.sampl.is1420_project.dao.UserDAO;
import fu.prm392.sampl.is1420_project.dto.UserDTO;
import fu.prm392.sampl.is1420_project.listener.OnItemUserClickListener;

public class SearchUserActivity extends AppCompatActivity {

    private RecyclerView recyclerSearchUser;
    private EditText etSearch;
    private TextView txtTitle;
    private List<UserDTO> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        recyclerSearchUser = findViewById(R.id.recycleSearchUser);
        etSearch = findViewById(R.id.etSearch);
        txtTitle = findViewById(R.id.txtTitle);
        recyclerSearchUser.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null &&
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    try {
                        searchUser();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;

                }
                return false;
            }
        });
    }

    private void searchUser() {
        UserDAO userDAO = new UserDAO();
        String search = etSearch.getText().toString().trim();
        try {
            userDAO.searchUser(search).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    loadData(queryDocumentSnapshots);
                    if (queryDocumentSnapshots.size() > 0) {
                        txtTitle.setText("Results of " + search);
                    } else {
                        txtTitle.setText("Not found");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData(QuerySnapshot queryDocumentSnapshots) {
        list = new ArrayList<>();
        for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
            UserDTO userDTO = snap.get("userInfo", UserDTO.class);
            list.add(userDTO);
        }
        UserAdapter userAdapter = new UserAdapter(list, getApplicationContext(), new OnItemUserClickListener() {
            @Override
            public void onItemClick(UserDTO item) {
                Intent intent = new Intent(getApplicationContext(), AdminEditProfileActivity.class);
                intent.putExtra("userID", item.getUserID());
                startActivity(intent);
            }
        });
        recyclerSearchUser.setAdapter(userAdapter);
    }

    public void clickToBack(View view) {
        onBackPressed();
    }
}