package ke.co.ideagalore.olyxadmin.ui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import ke.co.ideagalore.olyxadmin.databinding.FragmentStatisticsBinding;
import ke.co.ideagalore.olyxadmin.models.Expense;
import ke.co.ideagalore.olyxadmin.models.Transaction;
import ke.co.ideagalore.olyxadmin.viewmodels.StatisticsViewModel;

public class StatisticsFragment extends Fragment implements View.OnClickListener {

    FragmentStatisticsBinding binding;

    private FirebaseAuth auth;
    String terminalId;

    private DatabaseReference reference;

    long dateToday;

    double profits, netProfit;

    List<Transaction> transactionList = new ArrayList<>();
    List<Expense> expenseList = new ArrayList<>();

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
            binding.viewToday.setVisibility(View.VISIBLE);
            binding.viewThisWeek.setVisibility(View.GONE);
            binding.viewThisMonth.setVisibility(View.GONE);
            binding.viewCustom.setVisibility(View.GONE);

            fetchTodayData();

        } else if (view == binding.tvThisWeek) {
            binding.viewToday.setVisibility(View.GONE);
            binding.viewThisWeek.setVisibility(View.VISIBLE);
            binding.viewThisMonth.setVisibility(View.GONE);
            binding.viewCustom.setVisibility(View.GONE);
        } else if (view == binding.tvThisMonth) {
            binding.viewToday.setVisibility(View.GONE);
            binding.viewThisWeek.setVisibility(View.GONE);
            binding.viewThisMonth.setVisibility(View.VISIBLE);
            binding.viewCustom.setVisibility(View.GONE);
        }else if (view==binding.tvCustom){
            binding.viewToday.setVisibility(View.GONE);
            binding.viewThisWeek.setVisibility(View.GONE);
            binding.viewThisMonth.setVisibility(View.GONE);
            binding.viewCustom.setVisibility(View.VISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fetchTodayData() {

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStart() {
        super.onStart();
        fetchTodayData();
    }
}