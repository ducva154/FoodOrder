package fu.prm392.sampl.is1420_project.dao;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fu.prm392.sampl.is1420_project.dto.RestaurantDTO;
import fu.prm392.sampl.is1420_project.dto.UserDTO;

public class UserDAO {
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public UserDAO() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public Task<Void> createUser(UserDTO userDTO) {
        DocumentReference df = db.collection("Users").document(userDTO.getUserID());
        Map<String, Object> data = new HashMap<>();
        data.put("userInfo", userDTO);
        return df.set(data);
    }

    public Task<DocumentSnapshot> getUserById(String id) {
        DocumentReference doc = db.collection("Users").document(id);
        return doc.get();
    }

    public Task<Void> saveToken(String token, String uid) {
        DocumentReference reference = db.collection("Users").document(uid);
        return reference.update("tokens", FieldValue.arrayUnion(token));
    }

    public Task<Void> updatePassword(String newPassword, FirebaseUser user) {
        return user.updatePassword(newPassword);
    }

    public Task<Void> updateUser(UserDTO userDTO, List<RestaurantDTO> restaurantDTOList) {
        WriteBatch batch = db.batch();

        DocumentReference docUser = db.collection("Users").document(userDTO.getUserID());
        Map<String, Object> dataUser = new HashMap<>();
        dataUser.put("userInfo.email", userDTO.getEmail());
        dataUser.put("userInfo.name", userDTO.getName());
        dataUser.put("userInfo.phone", userDTO.getPhone());
        dataUser.put("userInfo.role", userDTO.getRole());
        dataUser.put("userInfo.status", userDTO.getStatus());
        dataUser.put("userInfo.photoUri", userDTO.getPhotoUri());

        batch.update(docUser, dataUser);

        if (userDTO.getRole().equals("owner") && restaurantDTOList != null) {
            for (RestaurantDTO restaurantDTO : restaurantDTOList) {
                DocumentReference doc = db.collection("restaurants").document(restaurantDTO.getRestaurantID());
                Map<String, Object> dataInRestaurant = new HashMap<>();
                dataInRestaurant.put("ownerInfo.email", userDTO.getEmail());
                dataInRestaurant.put("ownerInfo.name", userDTO.getName());
                dataInRestaurant.put("ownerInfo.phone", userDTO.getPhone());
                dataInRestaurant.put("ownerInfo.role", userDTO.getRole());
                dataInRestaurant.put("ownerInfo.status", userDTO.getStatus());
                dataInRestaurant.put("ownerInfo.photoUri", userDTO.getPhotoUri());
                batch.update(doc, dataInRestaurant);
            }
        }
        return batch.commit();
    }

    public Task<Uri> uploadImgUserToFirebase(Uri uri) {
        StorageReference mStoreRef = FirebaseStorage.getInstance().getReference("user_images")
                .child(System.currentTimeMillis() + ".png");
        UploadTask uploadTask = mStoreRef.putFile(uri);
        return uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return mStoreRef.getDownloadUrl();
            }
        });
    }

    public Task<QuerySnapshot> getAllUser() {
        return db.collection("Users").get();
    }

    public Task<Void> deleteUser(String userID) {
        DocumentReference doc = db.collection("Users").document(userID);
        Map<String, Object> data = new HashMap<>();
        data.put("userInfo.status", "inactive");
        return doc.update(data);
    }
}
