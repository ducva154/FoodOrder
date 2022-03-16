package fu.prm392.sampl.is1420_project.dao;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fu.prm392.sampl.is1420_project.dto.RestaurantDTO;
import fu.prm392.sampl.is1420_project.dto.UserDTO;

public class RestaurantDAO {
    private FirebaseFirestore db;

    public RestaurantDAO() {
        db = FirebaseFirestore.getInstance();
    }

    public Task<Void> createRestaurant(RestaurantDTO restaurantDTO, UserDTO owner) {
        DocumentReference restaurantReference = db.collection("restaurants").document();
        restaurantDTO.setRestaurantID(restaurantReference.getId());
        WriteBatch batch = db.batch();
        Map<String, Object> dataRestaurant = new HashMap<>();
        dataRestaurant.put("restaurantsInfo", restaurantDTO);
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
        return db.collection("restaurants").whereEqualTo("restaurantsInfo.status","active").get();
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

    public Task<DocumentSnapshot> getRestaurantByID(String restaurantID) {
        DocumentReference doc = db.collection("restaurants").document(restaurantID);
        return doc.get();
    }

    public Task<Void> updateRestaurant(RestaurantDTO restaurantDTO, RestaurantDTO previousRestaurantDTO, String uid) {
        DocumentReference docRestaurant = db.collection("restaurants")
                .document(restaurantDTO.getRestaurantID());
        DocumentReference docOwner = db.collection("Users").document(uid);


        Log.d("USER", "docRestaurantOfOwner: " + docOwner);
        return db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                Map<String, Object> dataRestaurant = new HashMap<>();
                dataRestaurant.put("restaurantsInfo.name", restaurantDTO.getName());
                dataRestaurant.put("restaurantsInfo.location", restaurantDTO.getLocation());
                dataRestaurant.put("restaurantsInfo.image", restaurantDTO.getImage());
                dataRestaurant.put("restaurantsInfo.status", restaurantDTO.getStatus());
                transaction.update(docRestaurant,dataRestaurant);

                Map<String, Object> dataDeleteRestaurant = new HashMap<>();
                dataDeleteRestaurant.put("restaurantsInfo", FieldValue.arrayRemove(previousRestaurantDTO));
                transaction.update(docOwner, dataDeleteRestaurant);

                Map<String, Object> dataUpdateRestaurant = new HashMap<>();
                dataUpdateRestaurant.put("restaurantsInfo", FieldValue.arrayUnion(restaurantDTO));
                transaction.update(docOwner, dataUpdateRestaurant);

                return null;
            }
        });
    }

    public List<Task<QuerySnapshot>> searchNearRestaurant(GeoLocation geoLocation, double radiusInM) {
        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(geoLocation, radiusInM);
        final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
        for (GeoQueryBounds b : bounds) {
            Query q = db.collection("restaurants")
                    .orderBy("restaurantsInfo.geoHash")
                    .startAt(b.startHash)
                    .endAt(b.endHash);
            tasks.add(q.get());
        }
        // Collect all the query results together into a single list
        return tasks;
    }
}
