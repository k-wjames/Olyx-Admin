package ke.co.ideagalore.olyxadmin.common;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(terminal)) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            terminal = auth.getUid();
            getTerminalData(context, terminal);

        }

    }

    private void getTerminalData(Context context, String terminalId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(terminalId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                business = snapshot.child("business").getValue(String.class);
                name = snapshot.child("name").getValue(String.class);
                savePreferencesData(context, name, business, terminal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String getOwner() {
        return name;
    }

    public String getBusiness() {
        return business;
    }

    public String getTerminal() {
        return terminal;
    }

}
