package fu.prm392.sampl.is1420_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class OwnerOrderDetailActivity extends AppCompatActivity {

    private RecyclerView recycleBasketItemView;
    private TextView txtCustomerName, txtStatus, txtAddress, txtOrderTime, txtBasketPrice;
    private String orderID;
    private Button btnApprove, btnReject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_order_detail);
        recycleBasketItemView = findViewById(R.id.recycleBasketItemView);
        txtCustomerName = findViewById(R.id.txtCustomerName);
        txtStatus = findViewById(R.id.txtStatus);
        txtAddress = findViewById(R.id.txtAddress);
        txtOrderTime = findViewById(R.id.txtOrderTime);
        txtBasketPrice = findViewById(R.id.txtBasketPrice);
        btnApprove = findViewById(R.id.btnApprove);
        btnReject = findViewById(R.id.btnReject);
        recycleBasketItemView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        Intent intent = getIntent();
        orderID = intent.getStringExtra("orderID");

        loadData();
        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderDAO orderDAO = new OrderDAO();
                orderDAO.updateStatus("Approved", orderID).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        btnApprove.setVisibility(View.GONE);
                        btnReject.setVisibility(View.GONE);
                        finish();
                    }
                });
            }
        });

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderDAO orderDAO = new OrderDAO();
                orderDAO.updateStatus("Rejected", orderID).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        btnApprove.setVisibility(View.GONE);
                        btnReject.setVisibility(View.GONE);
                        finish();
                    }
                });
            }
        });
    }

    private void loadData() {
        OrderDAO orderDAO = new OrderDAO();
        orderDAO.getOrderByID(orderID).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                OrderDTO orderDTO = documentSnapshot.get("ordersInfo", OrderDTO.class);
                txtCustomerName.setText(orderDTO.getUserInfo().getName());
                txtAddress.setText(orderDTO.getAddress());
                txtOrderTime.setText(orderDTO.getOrderTime().toString());
                txtStatus.setText(orderDTO.getStatus());
                txtBasketPrice.setText(orderDTO.getBasketsInfo().getBasketPrice() + "đ");
                List<BasketItemDocument> list = orderDTO.getListBasketItem();
                BasketItemAdapter basketItemAdapter = new BasketItemAdapter(list, getApplicationContext(), new OnItemBasketItemClickListener() {
                    @Override
                    public void onItemClick(BasketItemDocument item) {

                    }
                });
                recycleBasketItemView.setAdapter(basketItemAdapter);
                if (orderDTO.getStatus().equals("Approved") || orderDTO.getStatus().equals("Rejected")) {
                    btnApprove.setVisibility(View.GONE);
                    btnReject.setVisibility(View.GONE);
                }
            }
        });
    }
}