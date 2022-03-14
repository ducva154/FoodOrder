package fu.prm392.sampl.is1420_project.dao;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import fu.prm392.sampl.is1420_project.dto.RestaurantDTO;
import fu.prm392.sampl.is1420_project.dto.UserDTO;

public class RestaurantDAO {
    private FirebaseFirestore db;

    public RestaurantDAO() {
        db = FirebaseFirestore.getInstance();
    }

    public Task<Void> createRestaurent(RestaurantDTO restaurantDTO, UserDTO owner) {
        DocumentReference restaurantReference = db.collection("restaurants").document();
        restaurantDTO.setRestaurantID(restaurantReference.getId());
        WriteBatch batch = db.batch();
        Map<String, Object> dataRestaurant = new HashMap<>();
        dataRestaurant.put("restaurantInfo", restaurantDTO);
        batch.set(restaurantReference,dataRestaurant, SetOptions.merge());

        Map<String, Object> dataOwner = new HashMap<>();
        dataOwner.put("ownerInfo", owner);
        batch.set(restaurantReference, dataOwner, SetOptions.merge());

        DocumentReference restaurantInfoReference = db.collection("Users").document(owner.getUserID());
        Map<String, Object> dataInOwner = new HashMap<>();
        dataInOwner.put("restaurantsInfo", FieldValue.arrayUnion(restaurantDTO));
        batch.update(restaurantInfoReference, dataInOwner);

        return batch.commit();
    }

    public Task<QuerySnapshot>getAllRestaurant(){
        return db.collection("restaurants").get();
    }

    public Task<Uri> uploadImgToFirebase(Uri uriImg) {
        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference("restaurant_images").child(System.currentTimeMillis()+".png");
        UploadTask uploadTask = storageReference.putFile(uriImg);
        return uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return storageReference.getDownloadUrl();
            }
        });
    }
}
