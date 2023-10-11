package gr.aueb.cf.firebaseproject;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import gr.aueb.cf.firebaseproject.fragments.CreatePostFragment;
import gr.aueb.cf.firebaseproject.fragments.FavoriteFragment;
import gr.aueb.cf.firebaseproject.fragments.HomeFragment;
import gr.aueb.cf.firebaseproject.fragments.MyPostFragment;
import gr.aueb.cf.firebaseproject.fragments.ProfileFragment;
import gr.aueb.cf.firebaseproject.helpers.FirebaseReferencesHelper;
import gr.aueb.cf.firebaseproject.helpers.NotificationsApiKey;
import gr.aueb.cf.firebaseproject.interfaces.ChangeFragment;
import gr.aueb.cf.firebaseproject.models.TokenModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity implements ChangeFragment {

    private MaterialToolbar toolbar;
    private FrameLayout frameLayout;
    private BottomNavigationView bottom_nav_view;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        frameLayout = findViewById(R.id.frameLayout);
        fragmentManager = getSupportFragmentManager();
        bottom_nav_view = findViewById(R.id.bottom_nav_view);
        bottom_nav_view.getMenu().getItem(2).setChecked(true);
        fragmentManager.beginTransaction().replace(R.id.frameLayout, new HomeFragment(), "HomeFragment").commit();

        // Subscribe to Topic (users will be able to receive notifications from everyone who created new post
        //FirebaseMessaging.getInstance().subscribeToTopic(NotificationsApiKey.SUBSCRIBE_TOPIC);
        FirebaseMessaging.getInstance().subscribeToTopic(NotificationsApiKey.SUBSCRIBE_TOPIC)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        String msg = ("Subscribed");
                        if (!task.isSuccessful()) {
                            msg = "Couldn't subscribe to topic";
                        }
                        Log.d("TAG", msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        // end of subscribe

        bottom_nav_view.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_item_upload) {
                    fragmentManager.beginTransaction().replace(R.id.frameLayout, new CreatePostFragment(), "CreatePostFragment").commit();
                    toolbar.setTitle("Create Post");
                } else if (item.getItemId() == R.id.menu_item_post) {
                    fragmentManager.beginTransaction().replace(R.id.frameLayout, new MyPostFragment(), "PostFragment").commit();
                    toolbar.setTitle("My Posts");
                } else if (item.getItemId() == R.id.menu_item_home) {
                    fragmentManager.beginTransaction().replace(R.id.frameLayout, new HomeFragment(), " HomeFragment").commit();
                    toolbar.setTitle("Home");
                } else if (item.getItemId() == R.id.menu_item_favourite) {
                    fragmentManager.beginTransaction().replace(R.id.frameLayout, new FavoriteFragment(), "FavouriteFragment").commit();
                    toolbar.setTitle("Favourites");
                } else if (item.getItemId() == R.id.menu_item_profile) {
                    fragmentManager.beginTransaction().replace(R.id.frameLayout, new ProfileFragment(), "ProfileFragment").commit();
                    toolbar.setTitle("My Profile");
                }
                return true;
            }
        });

        storeTokenIntoFirebase();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Permission Allowed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            });


    @Override
    public void onFragmentChange(Fragment fragment, String title) {
        fragmentManager = getSupportFragmentManager();
        bottom_nav_view.getMenu().getItem(2).setChecked(true);
        fragmentManager.beginTransaction().replace(R.id.frameLayout, new HomeFragment(), "").commit();
        toolbar.setTitle(title);
    }

    public void storeTokenIntoFirebase() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                TokenModel tokenModel = new TokenModel(s);
                FirebaseReferencesHelper.getTokenReference().child(FirebaseReferencesHelper.getCurrentUserId()).setValue(tokenModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}