package ke.co.ideagalore.olyxadmin.common;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    String business, terminal, name;

    public void savePreferencesData(Context context, String owner, String businessName, String terminal) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Terminal", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", owner);
        editor.putString("business", businessName);
        editor.putString("terminal", terminal);
        editor.commit();
    }

    public void getPreferenceData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Terminal", MODE_PRIVATE);
        business = sharedPreferences.getString("business", null);
        terminal = sharedPreferences.getString("terminal", null);
        name = sharedPreferences.getString("name", null);

    }

    public String getOwner(Context context) {
        return name;
    }

}
