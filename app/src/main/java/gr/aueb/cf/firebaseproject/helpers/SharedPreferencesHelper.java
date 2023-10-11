package gr.aueb.cf.firebaseproject.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private SharedPreferences sharedPreferences;
    private final String PREFERENCES_FILE = "userData";
    private SharedPreferences.Editor editor;
    private Context context;

    public SharedPreferencesHelper(Context context) {
        this.context = context;

    }

    public void saveData(String name, String email) {
        sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.apply();
    }

    public String getName() {
        sharedPreferences = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
        return sharedPreferences.getString("name", "");
    }

}
