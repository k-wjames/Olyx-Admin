package ke.co.ideagalore.olyxadmin.ui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.databinding.FragmentStatisticsBinding;
import ke.co.ideagalore.olyxadmin.models.Expense;
import ke.co.ideagalore.olyxadmin.models.Transaction;
import ke.co.ideagalore.olyxadmin.viewmodels.StatisticsViewModel;

public class StatisticsFragment extends Fragment implements View.OnClickListener {

    FragmentStatisticsBinding binding;

    private FirebaseAuth auth;
    String terminalId;

    private DatabaseReference reference;

    long dateToday, firstDayOfWeek, firstDayOfMonth;

    Long startDate;
    Long endDate;

    double profits, netProfit, weeklyProfit, weeklyNetProfit, monthlyProfit, monthlyNetProfit;

    List<Transaction> transactionList = new ArrayList<>();
    List<Expense> expenseList = new ArrayList<>();

    List<Transaction> weeklyTransactionList = new ArrayList<>();
    List<Expense> weeklyExpenseList = new ArrayList<>();

    List<Transaction> monthlyTransactionList = new ArrayList<>();
    List<Expense> monthlyExpenseList = new ArrayList<>();

    StatisticsViewModel viewModel;

    public StatisticsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LocalDate localDate = LocalDate.now(ZoneOffset.UTC);
        dateToday = localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        LocalDateTime firstOfWeek = LocalDateTime.now().with(ChronoField.DAY_OF_WEEK, 1).toLocalDate().atStartOfDay();
        LocalDateTime firstOfMonth = LocalDateTime.now().with(ChronoField.DAY_OF_MONTH, 1).toLocalDate().atStartOfDay();

        firstDayOfWeek = firstOfWeek.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        firstDayOfMonth = firstOfMonth.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();


        auth = FirebaseAuth.getInstance();
        terminalId = auth.getUid();

        reference = FirebaseDatabase.getInstance().getReference("Users").child(terminalId).child("Transactions");

        viewModel = new ViewModelProvider(this).get(StatisticsViewModel.class);

        fetchTodayData();

        binding.tvToday.setOnClickListener(this);
        binding.tvThisWeek.setOnClickListener(this);
        binding.tvThisMonth.setOnClickListener(this);
        binding.tvCustom.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        if (view == binding.tvToday) {

            fetchTodayData();

        } else if (view == binding.tvThisWeek) {

            fetchWeeklyData();

        } else if (view == binding.tvThisMonth) {

            fetchMonthlyData();

        } else if (view == binding.tvCustom) {
            fetchCustomPeriodData();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fetchTodayData() {

        binding.viewToday.setVisibility(View.VISIBLE);
        binding.viewThisWeek.setVisibility(View.GONE);
        binding.viewThisMonth.setVisibility(View.GONE);
        binding.viewCustom.setVisibility(View.GONE);

        clearViews();

        viewModel.getTodaySales().observe(requireActivity(), salesToday -> {
            if (salesToday != null) binding.tvSales.setText("KES " + salesToday);
        });

        viewModel.getTodayTransactions().observe(requireActivity(), todayTransactions -> {
            if (todayTransactions != null)
                binding.tvTransactions.setText(String.valueOf(todayTransactions));
        });

        viewModel.getTodayExpenses().observe(requireActivity(), todayExpenses -> {
            if (todayExpenses != null) binding.tvExpenses.setText("KES " + todayExpenses);
        });

        viewModel.getTodayCreditSales().observe(requireActivity(), todayCreditSales -> {
            if (todayCreditSales != null) binding.tvCreditSales.setText("KES " + todayCreditSales);
        });

        viewModel.getTodayGasSales().observe(requireActivity(), todayGasSales -> {
            if (todayGasSales != null) binding.tvNewGasTotals.setText("KES " + todayGasSales);
        });

        viewModel.getTodayGasRefills().observe(requireActivity(), todayGasRefills -> {
            if (todayGasRefills != null) binding.tvRefillTotals.setText("KES " + todayGasRefills);
        });

        viewModel.getTodayAccessorySales().observe(requireActivity(), todayAccessorySales -> {
            if (todayAccessorySales != null)
                binding.tvTotalAccessoriesSales.setText("KES " + todayAccessorySales);
        });

        getTodayNetProfit();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fetchWeeklyData() {

        binding.viewToday.setVisibility(View.GONE);
        binding.viewThisWeek.setVisibility(View.VISIBLE);
        binding.viewThisMonth.setVisibility(View.GONE);
        binding.viewCustom.setVisibility(View.GONE);

        clearViews();

        getWeeklyNetProfit();

        viewModel.getWeeklySales().observe(requireActivity(), weeklySales -> {
            if (weeklySales != null) binding.tvSales.setText("KES " + weeklySales);
        });

        viewModel.getWeeklyTransactions().observe(requireActivity(), weeklyTransactions -> {
            if (weeklyTransactions != null)
                binding.tvTransactions.setText(String.valueOf(weeklyTransactions));
        });

        viewModel.getWeeklyNewGasSales().observe(requireActivity(), weeklyGasSales -> {
            if (weeklyGasSales != null) binding.tvNewGasTotals.setText("KES " + weeklyGasSales);
        });

        viewModel.getWeeklyGasRefills().observe(requireActivity(), weeklyGasRefills -> {
            if (weeklyGasRefills != null) binding.tvRefillTotals.setText("KES " + weeklyGasRefills);
        });

        viewModel.getWeeklyAccessorySales().observe(requireActivity(), weeklyAccessorySales -> {
            if (weeklyAccessorySales != null)
                binding.tvTotalAccessoriesSales.setText("KES " + weeklyAccessorySales);
        });

        viewModel.getTotalWeeklyExpenditure().observe(requireActivity(), weeklyExpenditure -> {
            if (weeklyExpenditure != null) binding.tvExpenses.setText("KES " + weeklyExpenditure);
        });

        viewModel.getWeeklyTotalCreditSales().observe(requireActivity(), weeklyCreditSales -> {
            if (weeklyCreditSales != null)
                binding.tvCreditSales.setText("KES " + weeklyCreditSales);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fetchMonthlyData() {

        binding.viewToday.setVisibility(View.GONE);
        binding.viewThisWeek.setVisibility(View.GONE);
        binding.viewThisMonth.setVisibility(View.VISIBLE);
        binding.viewCustom.setVisibility(View.GONE);

        clearViews();

        getMonthlyNetProfit();

        viewModel.getMonthlySales().observe(requireActivity(), monthlySales -> {
            if (monthlySales != null) binding.tvSales.setText("KES " + monthlySales);

        });

        viewModel.getMonthlyTransactions().observe(requireActivity(), monthlyTransactions -> {
            if (monthlyTransactions != null)
                binding.tvTransactions.setText(String.valueOf(monthlyTransactions));
        });

        viewModel.getMonthlyNewGasSales().observe(requireActivity(), monthlyGasSales -> {
            if (monthlyGasSales != null) binding.tvNewGasTotals.setText("KES " + monthlyGasSales);
        });

        viewModel.getMonthlyGasRefills().observe(requireActivity(), monthlyGasRefills -> {
            if (monthlyGasRefills != null)
                binding.tvRefillTotals.setText("KES " + monthlyGasRefills);
        });

        viewModel.getMonthlyAccessorySales().observe(requireActivity(), monthlyAccessorySales -> {
            if (monthlyAccessorySales != null)
                binding.tvTotalAccessoriesSales.setText("KES " + monthlyAccessorySales);
        });

        viewModel.getTotalMonthlyExpenditure().observe(requireActivity(), monthlyExpenditure -> {
            if (monthlyExpenditure != null)
                binding.tvExpenses.setText(String.valueOf(monthlyExpenditure));
        });

        viewModel.getMonthlyTotalCreditSales().observe(requireActivity(), monthlyCreditSales -> {
            if (monthlyCreditSales != null)
                binding.tvCreditSales.setText(String.valueOf(monthlyCreditSales));
        });
    }

    private void fetchCustomPeriodData() {
        binding.viewToday.setVisibility(View.GONE);
        binding.viewThisWeek.setVisibility(View.GONE);
        binding.viewThisMonth.setVisibility(View.GONE);
        binding.viewCustom.setVisibility(View.VISIBLE);

        clearViews();

        pickDateRange();
    }

    private void getTodayNetProfit() {
        getTodayProfit();
        getTodayExpenditure();
    }

    private void getWeeklyNetProfit() {
        getWeeklyProfit();
        getWeeklyExpenditure();
    }

    private void getMonthlyNetProfit() {
        getMonthlyProfit();
        getMonthlyExpenditure();
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

    private void getWeeklyProfit() {

        DatabaseReference ref = reference.child("Sales");

        Query query = ref.orderByChild("date").startAt(firstDayOfWeek).endAt(dateToday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                weeklyTransactionList.clear();

                for (DataSnapshot transactionSnapshot : snapshot.getChildren()) {
                    Transaction transaction = transactionSnapshot.getValue(Transaction.class);

                    weeklyTransactionList.add(transaction);

                    int totalWeeklyProfit = 0;

                    for (Transaction item : weeklyTransactionList) {
                        totalWeeklyProfit = totalWeeklyProfit + item.getProfit();
                        weeklyProfit = totalWeeklyProfit;
                        binding.tvNetProfits.setText(String.valueOf(weeklyProfit));

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getWeeklyExpenditure() {

        DatabaseReference ref = reference.child("Expenditure");
        Query query = ref.orderByChild("date").startAt(firstDayOfWeek).endAt(dateToday);
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                weeklyExpenseList.clear();

                for (DataSnapshot transactionSnapshot : snapshot.getChildren()) {

                    Expense expense = transactionSnapshot.getValue(Expense.class);
                    weeklyExpenseList.add(expense);
                    int totalWeeklyExpenses = 0;
                    for (Expense myExpense : weeklyExpenseList) {
                        totalWeeklyExpenses = totalWeeklyExpenses + myExpense.getPrice();
                        weeklyNetProfit = weeklyProfit - totalWeeklyExpenses;
                        binding.tvNetProfits.setText("KES " + weeklyNetProfit);

                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthlyProfit() {

        DatabaseReference ref = reference.child("Sales");
        Query query = ref.orderByChild("date").startAt(firstDayOfMonth).endAt(dateToday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                monthlyTransactionList.clear();

                for (DataSnapshot transactionSnapshot : snapshot.getChildren()) {
                    Transaction transaction = transactionSnapshot.getValue(Transaction.class);
                    monthlyTransactionList.add(transaction);

                    int totalMonthlyProfit = 0;

                    for (Transaction item : monthlyTransactionList) {
                        totalMonthlyProfit = totalMonthlyProfit + item.getProfit();
                        monthlyProfit = totalMonthlyProfit;
                        binding.tvNetProfits.setText(String.valueOf(monthlyProfit));

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthlyExpenditure() {

        DatabaseReference ref = reference.child("Expenditure");
        Query query = ref.orderByChild("date").startAt(firstDayOfMonth).endAt(dateToday);
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                monthlyExpenseList.clear();

                for (DataSnapshot transactionSnapshot : snapshot.getChildren()) {

                    Expense expense = transactionSnapshot.getValue(Expense.class);
                    monthlyExpenseList.add(expense);
                    int totalMonthlyExpenses = 0;
                    for (Expense myExpense : monthlyExpenseList) {
                        totalMonthlyExpenses = totalMonthlyExpenses + myExpense.getPrice();
                        monthlyNetProfit = monthlyProfit - totalMonthlyExpenses;
                        binding.tvNetProfits.setText("KES " + monthlyNetProfit);

                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clearViews() {
        String empty = "KES 00";
        binding.tvSales.setText(empty);
        binding.tvTransactions.setText("00");
        binding.tvExpenses.setText(empty);
        binding.tvCreditSales.setText(empty);
        binding.tvNewGasTotals.setText(empty);
        binding.tvRefillTotals.setText(empty);
        binding.tvTotalAccessoriesSales.setText(empty);
        binding.tvNetProfits.setText(empty);
    }

    private void pickDateRange() {

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTheme(R.style.MaterialCalendarTheme_RangeFill);
        MaterialDatePicker<Pair<Long, Long>> pickerRange = builder.build();
        pickerRange.show(requireActivity().getSupportFragmentManager(), pickerRange.toString());

        pickerRange.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                startDate = selection.first;
                endDate = selection.second;

                //binding.tvSales.setText(String.valueOf(viewModel.getCustomPeriodSales(startDate, endDate)));
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStart() {
        super.onStart();
        fetchTodayData();
    }
}