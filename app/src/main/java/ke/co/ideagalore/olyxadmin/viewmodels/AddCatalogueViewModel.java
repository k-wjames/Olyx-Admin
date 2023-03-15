package ke.co.ideagalore.olyxadmin.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ke.co.ideagalore.olyxadmin.models.Stores;

public class AddCatalogueViewModel extends ViewModel {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    String terminalId = auth.getUid();
    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(terminalId).child("Stores");

    List<String> storesList = new ArrayList<>();

    MutableLiveData<List<String>> stores;

    public LiveData<List<String>> getStores() {

        if (stores == null) {
            stores= new MutableLiveData<>();
            fetchStores();
        }
        return stores;

    }

    private void fetchStores() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 storesList.clear();
                for (DataSnapshot storeSnapshot: snapshot.getChildren()){
                    Stores stores =storeSnapshot.getValue(Stores.class);
                    String store=stores.getStore();
                    storesList.add(store);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        stores.postValue(storesList);
    }

}
