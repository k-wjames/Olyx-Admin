package ke.co.ideagalore.olyxadmin.viewmodels;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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

import ke.co.ideagalore.olyxadmin.common.Constants;
import ke.co.ideagalore.olyxadmin.models.Credit;
import ke.co.ideagalore.olyxadmin.models.Expense;
import ke.co.ideagalore.olyxadmin.models.Transaction;

@RequiresApi(api = Build.VERSION_CODES.O)
public class StatisticsViewModel extends ViewModel {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    String terminalId = auth.getUid();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(terminalId).child("Transactions");

    Constants constants = new Constants();

    List<Transaction> todayTransactionList = new ArrayList<>();
    List<Expense> todayExpenseList = new ArrayList<>();
    List<Credit> todayCreditList = new ArrayList<>();
    List<Transaction> todayNewGasList = new ArrayList<>();
    List<Transaction> todayGasRefillList = new ArrayList<>();
    List<Transaction> todayAccessoryList = new ArrayList<>();

    LocalDate localDate = LocalDate.now(ZoneOffset.UTC);
    long dateToday = localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

    MutableLiveData<Integer> transactions;
    MutableLiveData<Double> todaySales;
    MutableLiveData<Double> creditSales;
    MutableLiveData<Double> businessExpenses;
    MutableLiveData<Double> gasSalesToday;
    MutableLiveData<Double> gasRefillsToday;
    MutableLiveData<Double> accessorySalesToday;

    public LiveData<Double> getTodaySales() {

        if (todaySales == null) {
            todaySales = new MutableLiveData<>();
            fetchTodaySales();
        }
        return todaySales;

    }

    public LiveData<Integer> getTodayTransactions() {

        if (transactions == null) {
            transactions = new MutableLiveData<>();
        }
        return transactions;
    }

    public LiveData<Double> getTodayExpenses() {
        if (businessExpenses == null) {
            businessExpenses = new MutableLiveData<>();
            fetchTodayBusinessExpenses();
        }
        return businessExpenses;
    }

    public LiveData<Double> getTodayCreditSales() {
        if (creditSales == null) {
            creditSales = new MutableLiveData<>();
            fetchTodayCreditSales();
        }
        return creditSales;
    }

    public LiveData<Double> getTodayGasSales() {
        if (gasSalesToday == null) {
            gasSalesToday = new MutableLiveData<>();
        }
        return gasSalesToday;
    }

    public LiveData<Double> getTodayGasRefills() {
        if (gasRefillsToday == null) {
            gasRefillsToday = new MutableLiveData<>();
        }
        return gasRefillsToday;
    }

    public LiveData<Double> getTodayAccessorySales() {
        if (accessorySalesToday == null) {
            accessorySalesToday = new MutableLiveData<>();
        }
        return accessorySalesToday;
    }

    private void fetchTodaySales() {

        reference.child("Sales").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                todayTransactionList.clear();

                for (DataSnapshot saleSnapshot : snapshot.getChildren()) {

                    double allSales = 0;
                    double todayGasSales = 0;
                    double todayGasRefills = 0;
                    double todayAccessorySales = 0;

                    Transaction transaction = saleSnapshot.getValue(Transaction.class);

                    if (transaction.getDate() == dateToday) {

                        todayTransactionList.add(transaction);

                        for (Transaction myTransaction : todayTransactionList) {
                            allSales = allSales + myTransaction.getTotalPrice();
                        }


                        if (transaction.getTransactionType().equals(constants.NEW_GAS_CATEGORY)) {

                            todayNewGasList.add(transaction);

                            for (Transaction trans : todayNewGasList) {
                                todayGasSales = todayGasSales + trans.getTotalPrice();
                                gasSalesToday.postValue(todayGasSales);

                            }

                        }

                        if (transaction.getTransactionType().equals(constants.GAS_REFILL_CATEGORY)) {

                            todayGasRefillList.add(transaction);

                            for (Transaction trans : todayGasRefillList) {
                                todayGasRefills = todayGasRefills + trans.getTotalPrice();
                                gasRefillsToday.postValue(todayGasRefills);

                            }

                        }

                        if (transaction.getTransactionType().equals(constants.ACCESSORY_CATEGORY)) {

                            todayAccessoryList.add(transaction);

                            for (Transaction trans : todayAccessoryList) {
                                todayAccessorySales = todayAccessorySales + trans.getTotalPrice();
                                accessorySalesToday.postValue(todayAccessorySales);

                            }

                        }


                        todaySales.postValue(allSales);
                        transactions.postValue(todayTransactionList.size());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchTodayBusinessExpenses() {

        reference.child("Expenditure").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                todayExpenseList.clear();

                for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {

                    Expense expense = expenseSnapshot.getValue(Expense.class);
                    if (expense.getDate() == dateToday) {
                        todayExpenseList.add(expense);

                        double todayExpense = 0;

                        for (Expense exp : todayExpenseList) {
                            todayExpense = todayExpense + exp.getPrice();
                            businessExpenses.postValue(todayExpense);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void fetchTodayCreditSales() {
        reference.child("Creditors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                todayCreditList.clear();

                for (DataSnapshot creditorSnapshot : snapshot.getChildren()) {

                    Credit credit = creditorSnapshot.getValue(Credit.class);

                    if (credit.getDate() == dateToday) {
                        todayCreditList.add(credit);

                        double creditToday = 0;

                        for (Credit cred : todayCreditList) {

                            creditToday = creditToday + cred.getAmount();
                            creditSales.postValue(creditToday);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
