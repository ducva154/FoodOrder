package fu.prm392.sampl.is1420_project.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

import fu.prm392.sampl.is1420_project.dto.OrderDTO;

public class OrderDAO {
    private FirebaseFirestore db;

    public OrderDAO() {
        db = FirebaseFirestore.getInstance();
    }


    public Task<Void> createOrder(OrderDTO orderDTO) {
        DocumentReference orderRef = db.collection("orders").document();
        orderDTO.setOrderID(orderRef.getId());
        WriteBatch batch = db.batch();
        Map<String, Object> dataOrder = new HashMap<>();
        dataOrder.put("ordersInfo", orderDTO);
        batch.set(orderRef, dataOrder, SetOptions.merge());
        return batch.commit();
    }


    public Task<QuerySnapshot> getOrderByUserID(String uid) {
        return db.collection("orders")
                .whereEqualTo("ordersInfo.userInfo.userID", uid)
                .get();
    }

    public Task<DocumentSnapshot> getOrderByID(String orderID) {
        DocumentReference doc = db.collection("orders").document(orderID);
        return doc.get();
    }

    public Task<QuerySnapshot> getOrderByRestaurantID(String restaurantID) {
        return db.collection("orders")
                .whereEqualTo("ordersInfo.basketsInfo.restaurantsInfo.restaurantID", restaurantID)
                .get();
    }

    public Task<Void> updateStatus(String status, String orderID) {
        return db.collection("orders").document(orderID).update("ordersInfo.status", status);
    }
}
