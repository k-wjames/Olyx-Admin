package ke.co.ideagalore.olyxadmin.ui.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.adapters.StoreAdapter;
import ke.co.ideagalore.olyxadmin.adapters.TransactionsAdapter;
import ke.co.ideagalore.olyxadmin.common.CustomDialogs;
import ke.co.ideagalore.olyxadmin.databinding.FragmentTransactionsBinding;
import ke.co.ideagalore.olyxadmin.models.Stores;
import ke.co.ideagalore.olyxadmin.models.Transaction;

public class TransactionsFragment extends Fragment implements View.OnClickListener {

    FragmentTransactionsBinding binding;
    List<Transaction> transactionList = new ArrayList<>();

    List<Stores> storesList = new ArrayList<>();
    String terminal, selectedShop;
    CustomDialogs customDialogs = new CustomDialogs();

    public TransactionsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTransactionsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPreferenceData();

        getStoresData();

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchProduct(newText);
                return true;
            }
        });

        binding.ivFilter.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == binding.ivFilter) {
            showFilterByShopDialog(storesList);
        }
    }

    private void showFilterByShopDialog(List<Stores> storesList) {

        Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.outlets_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        ImageView ivCancel = dialog.findViewById(R.id.iv_cancel);
        ivCancel.setOnClickListener(view -> dialog.dismiss());

        StoreAdapter adapter = new StoreAdapter(storesList, store -> {
            List<Transaction> filteredTransactionsList = new ArrayList<>();
            String outlet = store.getStore();
            for (Transaction transaction : transactionList) {
                selectedShop = transaction.getStore();
                if (outlet.equals(selectedShop)){
                    filteredTransactionsList.add(transaction);
                    displayList(filteredTransactionsList);
                }
                dialog.dismiss();

            }

        });
        RecyclerView rvStores = dialog.findViewById(R.id.rv_outlets);
        rvStores.setLayoutManager(new LinearLayoutManager(requireActivity()));
        rvStores.setHasFixedSize(true);
        rvStores.setAdapter(adapter);
    }

    private void getPreferenceData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Terminal", MODE_PRIVATE);
        terminal = sharedPreferences.getString("terminal", null);
        getTransactionsData(terminal);
    }

    private void getStoresData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Stores");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                storesList.clear();
                for (DataSnapshot storeSnapshot : snapshot.getChildren()) {
                    Stores store = storeSnapshot.getValue(Stores.class);

                    assert store != null;
                    storesList.add(0, store);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void filterTransactionsByShop(String selectedShop, Dialog dialog) {
        for (Transaction transaction : transactionList) {
            String outlet = transaction.getStore();
            if (outlet.equals(selectedShop)) {
                List<Transaction> filteredTransactionsList = new ArrayList<>();
                filteredTransactionsList.add(transaction);
                displayList(filteredTransactionsList);
                dialog.dismiss();
                Toast.makeText(requireActivity(), "" + filteredTransactionsList.size(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getTransactionsData(String myTerminal) {
        customDialogs.showProgressDialog(requireActivity(), "Fetching transactions");
        transactionList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(myTerminal).child("Transactions").child("Sales");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                customDialogs.dismissProgressDialog();
                for (DataSnapshot transactionSnapshot : snapshot.getChildren()) {

                    Transaction transaction = transactionSnapshot.getValue(Transaction.class);
                    transactionList.add(0, transaction);
                    displayList(transactionList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                customDialogs.dismissProgressDialog();
                customDialogs.showSnackBar(requireActivity(), error.getMessage());
            }
        });

    }

    private void displayList(List<Transaction> list) {
        binding.rvTransactions.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvTransactions.setHasFixedSize(true);
        TransactionsAdapter adapter = new TransactionsAdapter(list);
        binding.rvTransactions.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void searchProduct(String newText) {

        List<Transaction> filteredList = new ArrayList<>();
        for (Transaction object : transactionList) {
            if (object.getProduct().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(object);
            }
        }
        displayList(filteredList);
    }
}