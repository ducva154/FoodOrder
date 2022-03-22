package fu.prm392.sampl.is1420_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fu.prm392.sampl.is1420_project.adapter.OwnerOrderAdapter;
import fu.prm392.sampl.is1420_project.dao.OrderDAO;
import fu.prm392.sampl.is1420_project.dto.OrderDTO;
import fu.prm392.sampl.is1420_project.listener.OnItemOrderClickListener;

public class OwnerOrderActivity extends AppCompatActivity {

    private RecyclerView recycleOrderView;
    private String restaurantID;
    private MaterialToolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_order);
        recycleOrderView = findViewById(R.id.recycleOrderView);
        recycleOrderView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        restaurantID = intent.getStringExtra("restaurantID");
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    private void loadData() {
        OrderDAO orderDAO = new OrderDAO();
        orderDAO.getOrderByRestaurantID(restaurantID).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<OrderDTO> list = new ArrayList<>();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    OrderDTO orderDTO = doc.get("ordersInfo", OrderDTO.class);
                    list.add(orderDTO);
                }
                OwnerOrderAdapter orderAdapter = new OwnerOrderAdapter(list, getApplicationContext(), new OnItemOrderClickListener() {
                    @Override
                    public void onItemClick(OrderDTO item) {
                        Intent intent = new Intent(OwnerOrderActivity.this, OwnerOrderDetailActivity.class);
                        intent.putExtra("orderID", item.getOrderID());
                        startActivity(intent);
                    }
                });
                recycleOrderView.setAdapter(orderAdapter);
            }
        });
    }
}