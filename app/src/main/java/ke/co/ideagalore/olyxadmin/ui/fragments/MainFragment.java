package ke.co.ideagalore.olyxadmin.ui.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.adapters.ExpenseAdapter;
import ke.co.ideagalore.olyxadmin.adapters.TransactionsAdapter;
import ke.co.ideagalore.olyxadmin.databinding.FragmentMainBinding;
import ke.co.ideagalore.olyxadmin.models.Expense;
import ke.co.ideagalore.olyxadmin.models.Transaction;
import ke.co.ideagalore.olyxadmin.viewmodels.FragmentMainViewModel;

public class MainFragment extends Fragment implements View.OnClickListener {

    FragmentMainBinding binding;
    FragmentMainViewModel viewModel;

    List<Transaction> transactionList = new ArrayList<>();
    List<Expense> expenseList = new ArrayList<>();
    private FirebaseAuth auth;

    DatabaseReference reference;

    String terminalId;
    long dateToday;

    double profits, netProfit;

    double totalExpenses;

    String name;

    SimpleDateFormat formatter;

    public MainFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getCurrentDate();

        auth = FirebaseAuth.getInstance();
        terminalId = auth.getUid();

        reference = FirebaseDatabase.getInstance().getReference("Users").child(terminalId).child("Transactions");

        LocalDate localDate = LocalDate.now(ZoneOffset.UTC);
        dateToday = localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        getPreferenceData();
        getTodayData();

        binding.tvViewTransactions.setOnClickListener(this);
        binding.btnSales.setOnClickListener(this);
        binding.btnExpenditure.setOnClickListener(this);
        binding.cvTransact.setOnClickListener(this);
        binding.cvCatalogue.setOnClickListener(this);
        binding.btnAddExpense.setOnClickListener(this);
        binding.btnAddCredit.setOnClickListener(this);

        binding.ivFilter.setOnClickListener(this);

    }

    private void getTodayData() {

        viewModel = new ViewModelProvider(this).get(FragmentMainViewModel.class);
        viewModel.getSales().observe(requireActivity(), sales -> {
            if (sales != null) binding.tvSales.setText(" KES " + sales);
        });

        viewModel.getTransactions().observe(requireActivity(), transactions -> {
            if (transactions != null) binding.tvTransactions.setText(String.valueOf(transactions));
        });

        viewModel.getExpenses().observe(requireActivity(), expenses -> {
            if (expenses != null) {
                totalExpenses = expenses;
                binding.tvExpenses.setText("KES " + totalExpenses);
            }
        });

        viewModel.getCreditSales().observe(requireActivity(), creditSales -> {
            if (creditSales != null) binding.tvCreditSales.setText("KES " + creditSales);
        });

        viewModel.getGasSales().observe(requireActivity(), totalGasSales -> {
            if (totalGasSales != null) binding.tvNewGasTotals.setText("KES " + totalGasSales);
        });

        viewModel.getGasRefills().observe(requireActivity(), gasRefills -> {
            if (gasRefills != null) binding.tvRefillTotals.setText("KES " + gasRefills);
        });

        viewModel.getAccessorySales().observe(requireActivity(), accessorySales -> {
            if (accessorySales != null)
                binding.tvTotalAccessoriesSales.setText("KES " + accessorySales);
        });

        viewModel.getCreditPaid().observe(requireActivity(), clearedCredit -> {
            if (clearedCredit != null) binding.tvCreditRepayed.setText("KES " + clearedCredit);
        });

        getTodayNetProfit();

    }


    @Override
    public void onClick(View view) {
        if (view == binding.tvViewTransactions) {
            Navigation.findNavController(view).navigate(R.id.transactionsFragment);
        } else if (view == binding.btnSales) {
            displayTransactionsList(transactionList);
        } else if (view == binding.btnExpenditure) {
            displayExpenditureList(expenseList);
        } else if (view == binding.cvTransact) {
            Navigation.findNavController(view).navigate(R.id.sellFragment);
        }  else if (view == binding.btnAddExpense) {
            Navigation.findNavController(view).navigate(R.id.expensesFragment);
        } else if (view == binding.btnAddCredit) {
            Navigation.findNavController(view).navigate(R.id.creditFragment);
        } else {
            Navigation.findNavController(view).navigate(R.id.catalogueItemsFragment);
        }

    }

    public void getPreferenceData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Terminal", MODE_PRIVATE);
        name = sharedPreferences.getString("name", null);
        binding.tvName.setText(name);

    }


    private void getTodayNetProfit() {
        getTodayProfit();
        getTodayExpenditure();
    }

    private void getTodayProfit() {
        reference.child("Sales").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                transactionList.clear();

                for (DataSnapshot transactionSnapshot : snapshot.getChildren()) {
                    Transaction transaction = transactionSnapshot.getValue(Transaction.class);
                    if (transaction.getDate() == dateToday) {

                        transactionList.add(transaction);

                        int totalProfit = 0;

                        for (Transaction item : transactionList) {
                            totalProfit = totalProfit + item.getProfit();
                            profits = totalProfit;
                            binding.tvNetProfits.setText(String.valueOf(profits));

                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTodayExpenditure() {
        reference.child("Expenditure").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                expenseList.clear();

                for (DataSnapshot transactionSnapshot : snapshot.getChildren()) {

                    Expense expense = transactionSnapshot.getValue(Expense.class);

                    if (expense.getDate() == dateToday) {
                        expenseList.add(expense);
                        int totalExpenses = 0;
                        for (Expense myExpense : expenseList) {
                            int currentExpense = myExpense.getPrice();
                            totalExpenses = totalExpenses + currentExpense;
                            netProfit = profits - totalExpenses;
                            binding.tvNetProfits.setText("KES " + netProfit);


                        }

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
        binding.tvDate.setText(day + "  " + month.toUpperCase() + " " + year);

    }

    private void displayTransactionsList(List<Transaction> list) {

        binding.rvTransactions.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvTransactions.setHasFixedSize(true);
        TransactionsAdapter adapter = new TransactionsAdapter(list);
        binding.rvTransactions.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void displayExpenditureList(List<Expense> expenseList) {
        binding.rvTransactions.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvTransactions.setHasFixedSize(true);
        ExpenseAdapter adapter = new ExpenseAdapter(expenseList);
        binding.rvTransactions.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceData();
    }

}