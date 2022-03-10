package fu.prm392.sampl.is1420_project.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

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

}
