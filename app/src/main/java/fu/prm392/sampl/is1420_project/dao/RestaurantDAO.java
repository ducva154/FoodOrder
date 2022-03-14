package fu.prm392.sampl.is1420_project.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

import fu.prm392.sampl.is1420_project.dto.RestaurantDTO;

public class RestaurantDAO {
    private FirebaseFirestore db;

    public RestaurantDAO() {
        db = FirebaseFirestore.getInstance();
    }

    public Task<Void> createRestaurent(RestaurantDTO restaurantDTO) {
        DocumentReference restaurantReference = db.collection("restaurants").document();
        restaurantDTO.setRestaurantID(restaurantReference.getId());
        WriteBatch batch = db.batch();
        Map<String, Object> dataRestaurant = new HashMap<>();
        dataRestaurant.put("restaurantInfo", restaurantDTO);
        batch.set(restaurantReference,dataRestaurant, SetOptions.merge());

        return batch.commit();
    }

    public Task<QuerySnapshot>getAllRestaurant(){
        return db.collection("restaurants").get();
    }
}
