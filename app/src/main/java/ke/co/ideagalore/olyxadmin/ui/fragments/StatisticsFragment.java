package ke.co.ideagalore.olyxadmin.ui.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.databinding.FragmentStatisticsBinding;
import ke.co.ideagalore.olyxadmin.models.Credit;
import ke.co.ideagalore.olyxadmin.models.Expense;
import ke.co.ideagalore.olyxadmin.models.Transaction;

public class StatisticsFragment extends Fragment implements View.OnClickListener {

    FragmentStatisticsBinding binding;
    String terminal, name, businessName, terminalId, dateToday;

    List<Transaction> transactionList = new ArrayList<>();
    List<Expense> expenseList = new ArrayList<>();
    List<Credit> creditList = new ArrayList<>();
    List<Transaction> refillList = new ArrayList<>();
    List<Transaction> gasSales = new ArrayList<>();
    List<Transaction> accessories = new ArrayList<>();

    int profits = 0;
    int netProfit = 0;
    int totalAccessoriesSales = 0;
    int totalNewGasSales = 0;
    int totalRefillGasSales = 0;

    SimpleDateFormat formatter;

    public StatisticsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Date date = new Date();
        formatter = new SimpleDateFormat("dd/MM/yyyy");
        dateToday = formatter.format(date);
        getPreferenceData();

        binding.ivBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == binding.ivBack) {
            Navigation.findNavController(view).navigate(R.id.mainFragment);
        }
    }

    private void getTransactionsData(String myTerminal) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(myTerminal).child("Transactions").child("Sales");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                transactionList.clear();

                for (DataSnapshot transactionSnapshot : snapshot.getChildren()) {
                    Transaction transaction = transactionSnapshot.getValue(Transaction.class);

                    if (transaction.getDate().equals(dateToday)) {

                        transactionList.add(transaction);

                        int sales = 0;
                        int totalProfit = 0;

                        for (Transaction item : transactionList) {
                            sales = sales + item.getTotalPrice();
                            binding.tvSales.setText("KES " + sales);
                            totalProfit = totalProfit + item.getProfit();
                            profits = totalProfit;

                        }

                        if (transactionList.size() < 10) {
                            binding.tvTransactions.setText("0" + transactionList.size());
                        } else {
                            binding.tvTransactions.setText(String.valueOf(transactionList.size()));
                        }

                        getRefillData(terminal);
                        getNewGasSalesData(terminal);
                        getAccessorySalesData(terminal);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getCreditSalesData(String terminal) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Transactions").child("Creditors");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                creditList.clear();
                for (DataSnapshot creditSnapshot : snapshot.getChildren()) {

                    Credit credit = creditSnapshot.getValue(Credit.class);

                    String date = credit.getDate();
                    if (date.equals(dateToday)) {
                        creditList.add(credit);
                        int creditSales = 0;

                        for (Credit myCredit : creditList) {
                            int currentCredit = myCredit.getAmount();
                            creditSales = creditSales + currentCredit;
                            binding.tvCreditSales.setText("KES " + creditSales);
                        }
                    } else {
                        binding.tvCreditSales.setText("KES 00");
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getRefillData(String myTerminal) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(myTerminal).child("Transactions").child("Sales");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                refillList.clear();
                for (DataSnapshot refillSnapshot : snapshot.getChildren()) {
                    Transaction transaction = refillSnapshot.getValue(Transaction.class);
                    if (transaction.getDate().equals(dateToday) && transaction.getTransactionType().equals("Gas refill")) {
                        refillList.add(0, transaction);
                        int totalRefills = 0;
                        for (Transaction refill : refillList) {
                            int refillPrice = refill.getTotalPrice();
                            totalRefills = totalRefills + refillPrice;
                            totalRefillGasSales = totalRefills;
                            binding.tvRefillTotals.setText("KES " + totalRefillGasSales);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getNewGasSalesData(String terminal) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Transactions").child("Sales");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gasSales.clear();

                for (DataSnapshot gasSnapshot : snapshot.getChildren()) {

                    Transaction transaction = gasSnapshot.getValue(Transaction.class);
                    if (transaction.getDate().equals(dateToday) && transaction.getTransactionType().equals("Gas sale")) {
                        gasSales.add(0, transaction);
                        int myGasSales = 0;
                        for (Transaction gasSale : gasSales) {
                            int newGasSale = gasSale.getTotalPrice();
                            myGasSales = myGasSales + newGasSale;
                            totalNewGasSales = myGasSales;
                            binding.tvNewGasTotals.setText("KES " + totalNewGasSales);
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAccessorySalesData(String terminal) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Transactions").child("Sales");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                accessories.clear();
                for (DataSnapshot accessorySnapshot : snapshot.getChildren()) {
                    Transaction transaction = accessorySnapshot.getValue(Transaction.class);
                    if (transaction.getDate().equals(dateToday) && transaction.getTransactionType().equals("Accessory sale")) {
                        accessories.add(0, transaction);
                        int accessorySales = 0;
                        for (Transaction accessorySale : accessories) {
                            int accessoryPrice = accessorySale.getTotalPrice();
                            accessorySales = accessorySales + accessoryPrice;
                            totalAccessoriesSales = accessorySales;
                            binding.tvTotalAccessoriesSales.setText("KES " + totalAccessoriesSales);

                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getExpenditureData(String myTerminal) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(myTerminal).child("Transactions").child("Expenditure");
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                expenseList.clear();

                for (DataSnapshot transactionSnapshot : snapshot.getChildren()) {
                    Expense expense = transactionSnapshot.getValue(Expense.class);
                    String date = expense.getDate();

                    if (date.equals(dateToday)) {
                        expenseList.add(expense);
                        int totalExpenses = 0;
                        for (Expense myExpense : expenseList) {
                            int currentExpense = myExpense.getPrice();
                            totalExpenses = totalExpenses + currentExpense;
                            netProfit = profits - totalExpenses;
                            binding.tvExpenses.setText("KES " + totalExpenses);
                            binding.tvNetProfits.setText("KES " + netProfit);

                        }

                    } else {

                        binding.tvExpenses.setText("KES 00");
                        int expenses = 0;
                        netProfit = profits - expenses;
                        binding.tvNetProfits.setText("KES " + netProfit);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTerminalData(String terminalId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(terminalId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                businessName = snapshot.child("business").getValue(String.class);
                name = snapshot.child("name").getValue(String.class);
                savePreferencesData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void savePreferencesData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Terminal", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("business", businessName);
        editor.putString("terminal", terminalId);
        editor.commit();
        getPreferenceData();
    }

    private void getPreferenceData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Terminal", MODE_PRIVATE);
        String name = sharedPreferences.getString("name", null);
        terminal = sharedPreferences.getString("terminal", null);
        businessName = sharedPreferences.getString("business", null);

        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(terminal)) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            terminalId = auth.getUid();
            getTerminalData(terminalId);

        } else {
            getTransactionsData(terminal);
            getExpenditureData(terminal);
            getCreditSalesData(terminal);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceData();
    }
}