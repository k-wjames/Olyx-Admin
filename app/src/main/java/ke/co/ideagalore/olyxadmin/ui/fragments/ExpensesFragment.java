package ke.co.ideagalore.olyxadmin.ui.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.adapters.ExpenseAdapter;
import ke.co.ideagalore.olyxadmin.common.CustomDialogs;
import ke.co.ideagalore.olyxadmin.databinding.FragmentExpensesBinding;
import ke.co.ideagalore.olyxadmin.models.Expense;

public class ExpensesFragment extends Fragment implements View.OnClickListener{
    FragmentExpensesBinding binding;
    CustomDialogs customDialogs=new CustomDialogs();
    String terminal;

    List<Expense>expenseList=new ArrayList<>();

    public ExpensesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentExpensesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPreferenceData();

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

        binding.ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view==binding.ivBack){
            Navigation.findNavController(view).navigate(R.id.mainFragment);
        }
    }
    private void getPreferenceData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Terminal", MODE_PRIVATE);
        terminal = sharedPreferences.getString("terminal", null);
        getExpenditureData(terminal);

    }
    private void getExpenditureData(String myTerminal) {
        customDialogs.showProgressDialog(requireActivity(),"Fetching expenditure data");
        expenseList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(myTerminal).child("Transactions").child("Expenditure");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                customDialogs.dismissProgressDialog();
                for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                    Expense expense = expenseSnapshot.getValue(Expense.class);
                    expenseList.add(expense);
                }
                binding.rvExpenditure.setLayoutManager(new LinearLayoutManager(getActivity()));
                binding.rvExpenditure.setHasFixedSize(true);
                ExpenseAdapter adapter=new ExpenseAdapter(expenseList);
                binding.rvExpenditure.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                customDialogs.dismissProgressDialog();
                customDialogs.showSnackBar(requireActivity(), error.getMessage());
            }
        });
    }

    private void searchProduct(String newText) {

        List<Expense> filteredList = new ArrayList<>();
        for (Expense object : expenseList) {
            if (object.getExpense().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(object);
            }
        }
        binding.rvExpenditure.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvExpenditure.setHasFixedSize(true);
        ExpenseAdapter adapter=new ExpenseAdapter(filteredList);
        binding.rvExpenditure.setAdapter(adapter);
    }
}