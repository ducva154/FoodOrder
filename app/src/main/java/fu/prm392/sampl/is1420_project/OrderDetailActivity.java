package fu.prm392.sampl.is1420_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import fu.prm392.sampl.is1420_project.adapter.BasketItemAdapter;
import fu.prm392.sampl.is1420_project.dao.OrderDAO;
import fu.prm392.sampl.is1420_project.dto.BasketItemDocument;
import fu.prm392.sampl.is1420_project.dto.OrderDTO;
import fu.prm392.sampl.is1420_project.listener.OnItemBasketItemClickListener;

public class OrderDetailActivity extends AppCompatActivity {

    private RecyclerView recycleBasketItemView;
    private TextView txtRestaurantName, txtStatus, txtFrom, txtTo, txtBasketPrice;
    private String orderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        recycleBasketItemView = findViewById(R.id.recycleBasketItemView);
        txtRestaurantName = findViewById(R.id.txtRestaurantName);
        txtStatus = findViewById(R.id.txtStatus);
        txtFrom = findViewById(R.id.txtFrom);
        txtTo = findViewById(R.id.txtTo);
        txtBasketPrice = findViewById(R.id.txtBasketPrice);
        recycleBasketItemView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        Intent intent = getIntent();
        orderID = intent.getStringExtra("orderID");

        loadData();
    }

    private void loadData() {
        OrderDAO orderDAO = new OrderDAO();
        orderDAO.getOrderByID(orderID).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                OrderDTO orderDTO = documentSnapshot.get("ordersInfo", OrderDTO.class);
                txtRestaurantName.setText(orderDTO.getBasketsInfo().getRestaurantsInfo().getName());
                txtFrom.setText(orderDTO.getBasketsInfo().getRestaurantsInfo().getLocation());
                txtTo.setText(orderDTO.getAddress());
                txtStatus.setText(orderDTO.getStatus());
                txtBasketPrice.setText(orderDTO.getBasketsInfo().getBasketPrice() + "đ");
                List<BasketItemDocument> list = orderDTO.getListBasketItem();
                BasketItemAdapter basketItemAdapter = new BasketItemAdapter(list, getApplicationContext(), new OnItemBasketItemClickListener() {
                    @Override
                    public void onItemClick(BasketItemDocument item) {

                    }
                });
                recycleBasketItemView.setAdapter(basketItemAdapter);
            }
        });
    }
}