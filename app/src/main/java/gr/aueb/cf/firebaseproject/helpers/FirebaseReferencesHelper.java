package gr.aueb.cf.firebaseproject.helpers;

import android.provider.ContactsContract;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseReferencesHelper {

    private static FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private static FirebaseAuth auth = FirebaseAuth.getInstance();

    public static DatabaseReference getUserReference(String userId) {
        return firebaseDatabase.getReference("users").child(userId);


    }

    public static String getCurrentUserId() {
        return auth.getCurrentUser().getUid();
    }

    public static void logout() {
        auth.signOut();
    }
}
