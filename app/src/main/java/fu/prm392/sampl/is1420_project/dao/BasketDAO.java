package fu.prm392.sampl.is1420_project.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class BasketDAO {
    private FirebaseFirestore db;

    public BasketDAO() {
        db = FirebaseFirestore.getInstance();
    }

    public Task<QuerySnapshot> getBasketByRestaurantID(String restaurantID) {
        return db.collection("baskets")
                .whereEqualTo("basketsInfo.restaurantsInfo.restaurantID", restaurantID)
                .get();
    }
}
