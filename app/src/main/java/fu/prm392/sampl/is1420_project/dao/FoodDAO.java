package fu.prm392.sampl.is1420_project.dao;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import fu.prm392.sampl.is1420_project.dto.FoodDTO;
import fu.prm392.sampl.is1420_project.dto.RestaurantDTO;

public class FoodDAO {
    private FirebaseFirestore db;

    public FoodDAO() {
        db = FirebaseFirestore.getInstance();
    }

    public Task<Uri> uploadImgToFirebase(Uri uriImg) {
        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference("food_images").child(System.currentTimeMillis()+".png");
        UploadTask uploadTask = storageReference.putFile(uriImg);
        return uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return storageReference.getDownloadUrl();
            }
        });
    }

    public Task<Void> addFood(FoodDTO foodDTO, RestaurantDTO restaurantDTO) {
        DocumentReference foodReference = db.collection("foods").document();
        foodDTO.setFoodID(foodReference.getId());
        WriteBatch batch = db.batch();
        Map<String,Object> dataFood = new HashMap<>();
        dataFood.put("foodsInfo", foodDTO);
        batch.set(foodReference,dataFood, SetOptions.merge());

        Map<String, Object> dataRestaurant = new HashMap<>();
        dataRestaurant.put("restaurantsInfo", restaurantDTO);
        batch.set(foodReference, dataRestaurant, SetOptions.merge());

        DocumentReference foodInfoReference = db.collection("restaurants")
                .document(restaurantDTO.getRestaurantID());
        Map<String, Object> dataInRestaurant = new HashMap<>();
        dataInRestaurant.put("foodsInfo", FieldValue.arrayUnion(foodDTO));
        batch.update(foodInfoReference, dataInRestaurant);

        return batch.commit();
    }

    public Task<DocumentSnapshot> getFoodByID(String foodID) {
        DocumentReference doc = db.collection("foods").document(foodID);
        return doc.get();
    }

    public Task<Void> updateFood(FoodDTO foodDTO, FoodDTO previousFoodDTO, String restaurantID) {
        DocumentReference docFood = db.collection("foods")
                .document(foodDTO.getFoodID());
        DocumentReference docRestaurant = db.collection("restaurants")
                .document(restaurantID);
        Log.d("USER", "docFoodOfRestaurant: " + docRestaurant);

        return db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                Map<String, Object> dataFood = new HashMap<>();
                dataFood.put("foodsInfo.name",foodDTO.getName());
                dataFood.put("foodsInfo.price",foodDTO.getPrice());
                dataFood.put("foodsInfo.description",foodDTO.getDescription());
                dataFood.put("foodsInfo.image",foodDTO.getImage());
                dataFood.put("foodsInfo.status",foodDTO.getStatus());
                transaction.update(docFood,dataFood);

                Map<String,Object> dataDeleteFood = new HashMap<>();
                dataDeleteFood.put("foodsInfo", FieldValue.arrayRemove(previousFoodDTO));
                transaction.update(docRestaurant,dataDeleteFood);

                Map<String,Object> dataUpdateFood = new HashMap<>();
                dataUpdateFood.put("foodsInfo", FieldValue.arrayUnion(foodDTO));
                transaction.update(docRestaurant,dataUpdateFood);

                return null;
            }
        });
    }
}
