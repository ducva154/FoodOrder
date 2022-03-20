package fu.prm392.sampl.is1420_project.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

import fu.prm392.sampl.is1420_project.dto.BasketDTO;

public class OrderDAO {
    private FirebaseFirestore db;

    public OrderDAO() {
        db = FirebaseFirestore.getInstance();
    }

    public Task<Void> createOrder(BasketDTO basketDTO, String cartID) {
        DocumentReference orderReference = db.collection("orders").document();
        basketDTO.setBasketID(orderReference.getId());

        WriteBatch batch = db.batch();
        Map<String, Object> dataOrder = new HashMap<>();
        dataOrder.put("ordersInfo", basketDTO);
        batch.set(orderReference, dataOrder, SetOptions.merge());

        DocumentReference ordersInfoReference = db.collection("carts")
                .document(cartID);
        Map<String, Object> dataInCart = new HashMap<>();
        dataInCart.put("ordersInfo", FieldValue.arrayUnion(basketDTO));
        dataInCart.put("cartID", cartID);
        batch.update(ordersInfoReference, dataInCart);

        return batch.commit();
    }

    public Task<QuerySnapshot> getOrderByRestaurantID(String restaurantID) {
        return db.collection("orders")
                .whereEqualTo("ordersInfo.restaurantDTO.restaurantID", restaurantID)
                .get();
    }
}
