package ke.co.ideagalore.olyxadmin.ui.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.adapters.ExpenseAdapter;
import ke.co.ideagalore.olyxadmin.common.CustomDialogs;
import ke.co.ideagalore.olyxadmin.common.ValidateFields;
import ke.co.ideagalore.olyxadmin.databinding.FragmentExpensesBinding;
import ke.co.ideagalore.olyxadmin.models.Expense;

public class ExpensesFragment extends Fragment implements View.OnClickListener {
    FragmentExpensesBinding binding;
    CustomDialogs customDialogs = new CustomDialogs();
    ValidateFields validator = new ValidateFields();
    String terminal, selectedItem;

    long dateToday;

    List<Expense> expenseList = new ArrayList<>();

    public ExpensesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExpensesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPreferenceData();

        LocalDate localDate = LocalDate.now(ZoneOffset.UTC);
        dateToday = localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

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

        binding.ivAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.ivAdd) {
            showAddExpenseDialog();
        }
    }

    private void getPreferenceData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Terminal", MODE_PRIVATE);
        terminal = sharedPreferences.getString("terminal", null);
        getExpenditureData(terminal);

    }

    private void getExpenditureData(String myTerminal) {
        //binding.progressBar.setVisibility(View.VISIBLE);
        expenseList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(myTerminal).child("Transactions").child("Expenditure");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                    Expense expense = expenseSnapshot.getValue(Expense.class);
                    expenseList.add(0,expense);
                    //binding.progressBar.setVisibility(View.GONE);
                }
                binding.rvExpenditure.setLayoutManager(new LinearLayoutManager(getActivity()));
                binding.rvExpenditure.setHasFixedSize(true);
                ExpenseAdapter adapter = new ExpenseAdapter(expenseList);
                binding.rvExpenditure.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //binding.progressBar.setVisibility(View.GONE);
                customDialogs.showSnackBar(requireActivity(), error.getMessage());
            }
        });
    }

    private void showAddExpenseDialog() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.add_expense_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        Spinner spinnerCategory=dialog.findViewById(R.id.spinner_category);

        String[] category = new String[]{"Administrative","Taxes","Rent","Salaries", "Marketing", "Repairs", "Fuel"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                category);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerCategory.setAdapter(arrayAdapter);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem =spinnerCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerCategory.setAdapter(arrayAdapter);

        TextView cancel = dialog.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(view -> dialog.dismiss());

        EditText description, cost;
        description = dialog.findViewById(R.id.edt_description);
        cost = dialog.findViewById(R.id.edt_cost);

        ProgressBar progressBar=dialog.findViewById(R.id.progress_bar);

        Button save = dialog.findViewById(R.id.btn_add_expense);
        save.setOnClickListener(view -> {

            String  expenseDescription, totalCost;
            expenseDescription = description.getText().toString();
            totalCost = cost.getText().toString();

            if (validator.validateEditTextFields(requireActivity(), description, "Description")
                    && validator.validateEditTextFields(requireActivity(), cost, "Amount")) {
                progressBar.setVisibility(View.VISIBLE);
                addNewExpenditure(expenseDescription, totalCost, dialog);
            }
        });

    }

    private void addNewExpenditure(String expenseDescription, String totalCost, Dialog dialog) {

        DateFormat formatter = new SimpleDateFormat("hh:mm:ss a");
        String time = formatter.format(new Date());
        String expenseId;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Transactions").child("Expenditure");
        expenseId = ref.push().getKey();
        Expense exp = new Expense();
        exp.setCategory(selectedItem);
        exp.setExpenseId(expenseId);
        exp.setDescription(expenseDescription);
        exp.setPrice(Integer.parseInt(totalCost));
        exp.setDate(dateToday);
        exp.setTime(time);

        ref.child(expenseId).setValue(exp).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                dialog.dismiss();
                getExpenditureData(terminal);

            } else {
                customDialogs.showSnackBar(requireActivity(),"Failed to add expense. Please try again.");
            }

        });

    }

    private void searchProduct(String newText) {

        List<Expense> filteredList = new ArrayList<>();
        for (Expense object : expenseList) {
            if (object.getCategory().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(object);
            }
        }
        binding.rvExpenditure.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvExpenditure.setHasFixedSize(true);
        ExpenseAdapter adapter = new ExpenseAdapter(filteredList);
        binding.rvExpenditure.setAdapter(adapter);
    }
}