package fu.prm392.sampl.is1420_project.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class BasketItemDAO {
    private FirebaseFirestore db;

    public BasketItemDAO() {
        db = FirebaseFirestore.getInstance();
    }

    public Task<QuerySnapshot> getBasketItemByBasketID(String basketID) {
        return db.collection("basketItems")
                .whereEqualTo("basketsInfo.basketID", basketID)
                .get();
    }
}
