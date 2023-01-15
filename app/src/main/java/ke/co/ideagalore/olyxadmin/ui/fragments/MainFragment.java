package ke.co.ideagalore.olyxadmin.ui.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import ke.co.ideagalore.olyxadmin.common.MySharedPreferences;
import ke.co.ideagalore.olyxadmin.databinding.FragmentMainBinding;
import ke.co.ideagalore.olyxadmin.models.Credit;
import ke.co.ideagalore.olyxadmin.models.CreditRepayment;
import ke.co.ideagalore.olyxadmin.models.Expense;
import ke.co.ideagalore.olyxadmin.models.Transaction;
import ke.co.ideagalore.olyxadmin.viewmodels.FragmentMainViewModel;

public class MainFragment extends Fragment implements View.OnClickListener {

    FragmentMainBinding binding;
    FragmentMainViewModel viewModel;

    List<Transaction> transactionList = new ArrayList<>();
    List<Expense> expenseList = new ArrayList<>();
    private FirebaseAuth auth;

    String terminalId;
    long dateToday;

    int profits = 0;
    int netProfit = 0;

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
        getDaysNetProfit();

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

    }

    private void getDaysNetProfit() {

        getDaysProfit(terminalId);
        getTodayExpenditure(terminalId);

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
        } else if (view == binding.ivFilter) {
            showFilterPeriodDialog();
        } else if (view == binding.btnAddExpense) {
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

    private void showFilterPeriodDialog() {
        Dialog myDialog = new Dialog(getActivity());
        myDialog.setContentView(R.layout.period_filter_dialog);
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        myDialog.show();
        TextView currentDate = myDialog.findViewById(R.id.tv_today);
        TextView specificDate = myDialog.findViewById(R.id.tv_specific_date);
        TextView selectedPeriod = myDialog.findViewById(R.id.tv_period);
        TextView noFilter = myDialog.findViewById(R.id.tv_all);

        currentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
                filterDataByCurrentDate();
            }
        });

        specificDate.setOnClickListener(view -> {
            myDialog.dismiss();
            showDatePickerDialog();
        });

        selectedPeriod.setOnClickListener(view -> {
            myDialog.dismiss();
        });

        noFilter.setOnClickListener(view -> {
            myDialog.dismiss();
            binding.tvPeriod.setText("All Time");
            displayTransactionsList(transactionList);
        });

    }

    private void showDatePickerDialog() {
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select range");
        MaterialDatePicker materialDatePicker = builder.build();
        materialDatePicker.show(requireActivity().getSupportFragmentManager(), "Range Picker");
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {


            String selectedDate = formatter.format(selection);
            if (selectedDate.equals(dateToday)) {
                binding.tvPeriod.setText("Today");
            } else {
                binding.tvPeriod.setText("Date : " + selectedDate);
            }
            filterDataBySpecificDate(selectedDate);
        });

    }

    private void filterDataByCurrentDate() {
        List<Transaction> todayList = new ArrayList<>();
        for (Transaction transaction : transactionList) {
            if (transaction.getDate() == dateToday) {

                todayList.add(0, transaction);

                binding.tvPeriod.setText("Today");
                displayTransactionsList(todayList);

            }
        }
    }

    private void filterDataBySpecificDate(String selectedDate) {
        List<Transaction> list = new ArrayList<>();
        for (Transaction transaction : transactionList) {
            if (selectedDate.equals(transaction.getDate())) {

                list.add(0, transaction);
                displayTransactionsList(list);

            }
        }
    }

    private void getDaysProfit(String myTerminal) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(myTerminal).child("Transactions").child("Sales");
        reference.addValueEventListener(new ValueEventListener() {
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

                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getTodayExpenditure(String myTerminal) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(myTerminal).child("Transactions").child("Expenditure");
        reference.addValueEventListener(new ValueEventListener() {

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
                            binding.tvExpenses.setText("KES " + totalExpenses);
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
        getTodayData();
    }
}