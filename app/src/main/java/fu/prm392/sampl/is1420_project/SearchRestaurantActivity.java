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

import fu.prm392.sampl.is1420_project.adapter.RestaurantAdapter;
import fu.prm392.sampl.is1420_project.dao.RestaurantDAO;
import fu.prm392.sampl.is1420_project.dto.RestaurantDTO;
import fu.prm392.sampl.is1420_project.listener.OnItemClickListener;

public class SearchRestaurantActivity extends AppCompatActivity {

    private RecyclerView recyclerSearchRestaurant;
    private EditText etSearch;
    private TextView txtTitle;
    private List<RestaurantDTO> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_restaurant);
        recyclerSearchRestaurant = findViewById(R.id.recycleSearchRestaurant);
        etSearch = findViewById(R.id.etSearch);
        txtTitle = findViewById(R.id.txtTitle);
        recyclerSearchRestaurant.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null &&
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    // the user is done typing.
                    try {
                        searchRestaurant();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;

                }
                return false;
            }
        });
    }

    private void searchRestaurant() {
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        String search = etSearch.getText().toString().trim();
        try {
            restaurantDAO.searchRestaurant(search).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
            RestaurantDTO restaurantDTO = snap.get("restaurantsInfo", RestaurantDTO.class);
            if (restaurantDTO.getStatus().equals("active")) {
                list.add(restaurantDTO);
            }
        }
        RestaurantAdapter restaurantAdapter = new RestaurantAdapter(list, getApplicationContext(), new OnItemClickListener() {
            @Override
            public void onItemClick(RestaurantDTO item) {
                Intent intent = new Intent(getApplicationContext(), RestaurantMenuActivity.class);
                intent.putExtra("restaurantID", item.getRestaurantID());
                startActivity(intent);
            }
        });
        recyclerSearchRestaurant.setAdapter(restaurantAdapter);
    }

    public void clickToBack(View view) {
        onBackPressed();
    }
}