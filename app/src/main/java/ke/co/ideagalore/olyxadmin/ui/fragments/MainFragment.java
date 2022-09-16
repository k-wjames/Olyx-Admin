package ke.co.ideagalore.olyxadmin.ui.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ke.co.ideagalore.olyxadmin.databinding.FragmentMainBinding;
import ke.co.ideagalore.olyxadmin.models.Catalogue;
import ke.co.ideagalore.olyxadmin.models.Stores;
import ke.co.ideagalore.olyxadmin.models.Transaction;

public class MainFragment extends Fragment {

    FragmentMainBinding binding;
    List<Stores> storesList = new ArrayList<>();
    List<Catalogue> catalogueList = new ArrayList<>();
    List<Transaction> transactionList = new ArrayList<>();

    String terminal;

    public MainFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getCurrentDate();
        getPreferenceData();
        getCatalogueData();
        getStoresData();
        getTransactions();
    }

    private void getCatalogueData() {
        catalogueList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Catalogue");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot storeSnapshot : snapshot.getChildren()) {
                    Catalogue catalogue = storeSnapshot.getValue(Catalogue.class);
                    catalogueList.add(catalogue);
                    if (catalogueList.size() < 10) {
                        binding.tvCatalogueItems.setText(0 + "" + catalogueList.size());
                    } else
                        binding.tvCatalogueItems.setText(String.valueOf(catalogueList.size()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getStoresData() {
        storesList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Stores");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot storeSnapshot : snapshot.getChildren()) {
                    Stores store = storeSnapshot.getValue(Stores.class);
                    storesList.add(store);
                    if (storesList.size() < 10) {
                        binding.tvStores.setText(0 + "" + storesList.size());
                    } else
                        binding.tvStores.setText(String.valueOf(storesList.size()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTransactions() {

        transactionList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Transactions");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot storeSnapshot : snapshot.getChildren()) {
                    Transaction transaction = storeSnapshot.getValue(Transaction.class);
                    transactionList.add(transaction);
                    if (catalogueList.size() < 10) {
                        binding.tvTransactions.setText(0 + "" + transactionList.size());
                    } else
                        binding.tvTransactions.setText(String.valueOf(transactionList.size()));
                    int sales = 0;
                    for (Transaction salesTransaction : transactionList) {
                        sales = sales + salesTransaction.getSellingPrice();
                        binding.tvSales.setText("KES " + sales);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());

        String dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

        binding.tvDay.setText(dayOfWeek.toUpperCase() + ",");
        binding.tvDate.setText(day + " " + month.toUpperCase() + " " + year);

    }

    private void getPreferenceData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Terminal", MODE_PRIVATE);
        String business = sharedPreferences.getString("business", null);
        String name = sharedPreferences.getString("name", null);
        terminal = sharedPreferences.getString("terminal", null);
        binding.tvName.setText(name + ",");
    }
}