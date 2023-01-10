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
import ke.co.ideagalore.olyxadmin.models.CreditRepayment;
import ke.co.ideagalore.olyxadmin.models.Expense;
import ke.co.ideagalore.olyxadmin.models.Transaction;

public class FragmentMainViewModel extends ViewModel {

    Constants constants = new Constants();

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    String terminalId = auth.getUid();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

    List<Transaction> transactionList = new ArrayList<>();
    List<Expense> expenseList = new ArrayList<>();
    List<Credit> creditList = new ArrayList<>();
    List<CreditRepayment> clearedCreditList = new ArrayList<>();
    List<Transaction> newGasList = new ArrayList<>();
    List<Transaction> gasRefillList = new ArrayList<>();
    List<Transaction> accessoryList = new ArrayList<>();

    long dateToday;

    private MutableLiveData<Double> sales;
    private MutableLiveData<Integer> transactions;
    private MutableLiveData<Double> businessExpenses;
    private MutableLiveData<Double> creditSales;
    private MutableLiveData<Double> gasSales;
    private MutableLiveData<Double> gasRefills;
    private MutableLiveData<Double> accessorySales;
    private MutableLiveData<Double> creditCleared;

    public LiveData<Double> getSales() {

        if (sales == null) {
            sales = new MutableLiveData<>();
            getAllSales();
        }
        return sales;

    }

    private void getAllSales() {

        reference.child(terminalId).child("Transactions").child("Sales").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                transactionList.clear();

                for (DataSnapshot saleSnapshot : snapshot.getChildren()) {

                    LocalDate localDate = LocalDate.now(ZoneOffset.UTC);
                    dateToday = localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

                    double allSales = 0;
                    double profits = 0;
                    Transaction transaction = saleSnapshot.getValue(Transaction.class);

                    if (transaction.getDate() == dateToday) {

                        transactionList.add(transaction);

                        for (Transaction myTransaction : transactionList) {
                            allSales = allSales + myTransaction.getTotalPrice();
                            profits = profits + myTransaction.getProfit();
                        }

                        sales.postValue(allSales);
                        transactions.postValue(transactionList.size());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public LiveData<Integer> getTransactions() {
        if (transactions == null) {
            transactions = new MutableLiveData<>();
        }
        return transactions;
    }

    public LiveData<Double> getExpenses() {
        if (businessExpenses == null) {
            businessExpenses = new MutableLiveData<>();
            getTodayExpenses();
        }
        return businessExpenses;
    }

    private void getTodayExpenses() {
        reference.child(terminalId).child("Transactions").child("Expenditure").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                expenseList.clear();
                for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                    Expense expense = expenseSnapshot.getValue(Expense.class);
                    if (expense.getDate() == dateToday) {
                        expenseList.add(expense);


                        double myExpense = 0;
                        for (Expense exp : expenseList) {

                            myExpense = myExpense + exp.getPrice();
                            businessExpenses.postValue(myExpense);
                        }

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public LiveData<Double> getGasSales() {
        if (gasSales == null) {
            gasSales = new MutableLiveData<>();
            getSalesPerCategory();
        }
        return gasSales;
    }

    public LiveData<Double> getGasRefills() {
        if (gasRefills == null) {
            gasRefills = new MutableLiveData<>();
            getSalesPerCategory();
        }
        return gasRefills;
    }

    public LiveData<Double> getAccessorySales() {
        if (accessorySales == null) {
            accessorySales = new MutableLiveData<>();
            getSalesPerCategory();
        }
        return accessorySales;
    }

    public LiveData<Double> getCreditSales() {
        if (creditSales == null) {
            creditSales = new MutableLiveData<>();
            getAllCreditSales();
        }
        return creditSales;
    }

    private void getAllCreditSales() {
        reference.child(terminalId).child("Transactions").child("Creditors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                creditList.clear();
                for (DataSnapshot creditSnapshot : snapshot.getChildren()) {
                    Credit credit = creditSnapshot.getValue(Credit.class);

                    if (credit.getDate() == dateToday) {
                        creditList.add(credit);
                        double totalCredit = 0;
                        for (Credit cred : creditList) {
                            totalCredit = totalCredit + cred.getAmount();
                            creditSales.postValue(totalCredit);
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSalesPerCategory() {

        reference.child(terminalId).child("Transactions").child("Sales").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                newGasList.clear();
                gasRefillList.clear();
                accessoryList.clear();

                for (DataSnapshot saleSnapshot : snapshot.getChildren()) {

                    LocalDate localDate = LocalDate.now(ZoneOffset.UTC);
                    dateToday = localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

                    double newGas = 0;
                    double gasRefill = 0;
                    double accessories = 0;
                    Transaction transaction = saleSnapshot.getValue(Transaction.class);

                    if (transaction.getTransactionType().equals(constants.NEW_GAS_CATEGORY)
                            && transaction.getDate() == dateToday) {

                        newGasList.add(transaction);
                        for (Transaction trans : newGasList) {
                            newGas = newGas + trans.getTotalPrice();
                        }

                        gasSales.postValue(newGas);
                    }

                    if (transaction.getTransactionType().equals(constants.GAS_REFILL_CATEGORY)
                            && transaction.getDate() == dateToday) {

                        gasRefillList.add(transaction);

                        for (Transaction trans : gasRefillList) {
                            gasRefill = gasRefill + trans.getTotalPrice();
                        }

                        gasRefills.postValue(gasRefill);


                    }

                    if (transaction.getTransactionType().equals(constants.ACCESSORY_CATEGORY)
                            && transaction.getDate() == dateToday) {

                        accessoryList.add(transaction);
                        for (Transaction trans : accessoryList) {
                            accessories = accessories + trans.getTotalPrice();
                        }
                        accessorySales.postValue(accessories);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public LiveData<Double> getCreditPaid() {
        if (creditCleared == null) {
            creditCleared = new MutableLiveData<>();
            getClearedCredit();
        }
        return creditCleared;
    }

    private void getClearedCredit() {
        reference.child(terminalId).child("Transactions").child("Repayments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clearedCreditList.clear();
                for (DataSnapshot clearedSnapshot : snapshot.getChildren()) {
                    CreditRepayment repayment = clearedSnapshot.getValue(CreditRepayment.class);
                    if (repayment.getDate() == dateToday) {
                        clearedCreditList.add(repayment);
                        double totalClearedCredit = 0;

                        for (CreditRepayment cred : clearedCreditList) {
                            totalClearedCredit = totalClearedCredit + cred.getAmount();
                        }

                        creditCleared.postValue(totalClearedCredit);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
