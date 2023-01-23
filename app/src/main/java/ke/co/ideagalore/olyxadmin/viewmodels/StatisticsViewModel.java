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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
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
    List<Transaction> weeklyTransactions = new ArrayList<>();
    List<Expense> weeklyExpenseList = new ArrayList<>();
    List<Credit> weeklyCreditList = new ArrayList<>();
    List<Transaction> weeklyNewGasList = new ArrayList<>();
    List<Transaction> weeklyGasRefillList = new ArrayList<>();
    List<Transaction> weeklyAccessoryList = new ArrayList<>();
    List<Transaction> monthlyTransactionsList = new ArrayList<>();
    List<Expense> monthlyExpenseList = new ArrayList<>();
    List<Credit> monthlyCreditList = new ArrayList<>();
    List<Transaction> monthlyNewGasList = new ArrayList<>();
    List<Transaction> monthlyGasRefillList = new ArrayList<>();
    List<Transaction> monthlyAccessoryList = new ArrayList<>();
    List<Transaction> customPeriodSalesList = new ArrayList<>();

    LocalDate localDate = LocalDate.now(ZoneOffset.UTC);
    long dateToday = localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

    LocalDateTime firstOfWeek = LocalDateTime.now().with(ChronoField.DAY_OF_WEEK, 1).toLocalDate().atStartOfDay();
    LocalDateTime firstOfMonth = LocalDateTime.now().with(ChronoField.DAY_OF_MONTH, 1).toLocalDate().atStartOfDay();

    long firstDayOfWeek = firstOfWeek.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    long firstDayOfMonth = firstOfMonth.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

    MutableLiveData<Integer> transactions;
    MutableLiveData<Double> todaySales;
    MutableLiveData<Double> creditSales;
    MutableLiveData<Double> businessExpenses;
    MutableLiveData<Double> gasSalesToday;
    MutableLiveData<Double> gasRefillsToday;
    MutableLiveData<Double> accessorySalesToday;
    MutableLiveData<Integer> weeklyTotalTransactions;
    MutableLiveData<Double> weeklySales;
    MutableLiveData<Double> weeklyNewGasSales;
    MutableLiveData<Double> weeklyGasRefills;
    MutableLiveData<Double> weeklyAccessorySales;
    MutableLiveData<Double> weeklyTotalExpenditure;
    MutableLiveData<Double> weeklyTotalCreditSales;
    MutableLiveData<Integer> monthlyTransactions;
    MutableLiveData<Double> monthlySales;
    MutableLiveData<Double> monthlyNewGasSales;
    MutableLiveData<Double> monthlyGasRefills;
    MutableLiveData<Double> monthlyAccessorySales;
    MutableLiveData<Double> monthlyTotalExpenditure;
    MutableLiveData<Double> monthlyTotalCreditSales;

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

    public LiveData<Double> getWeeklySales() {
        if (weeklySales == null) {

            weeklySales = new MutableLiveData<>();
            fetchWeeklySalesData();
        }
        return weeklySales;
    }

    public LiveData<Integer> getWeeklyTransactions() {
        if (weeklyTotalTransactions == null) {
            weeklyTotalTransactions = new MutableLiveData<>();
        }
        return weeklyTotalTransactions;
    }

    public LiveData<Double> getWeeklyNewGasSales() {
        if (weeklyNewGasSales == null) {
            weeklyNewGasSales = new MutableLiveData<>();
        }
        return weeklyNewGasSales;
    }

    public LiveData<Double> getWeeklyGasRefills() {
        if (weeklyGasRefills == null) {
            weeklyGasRefills = new MutableLiveData<>();
        }
        return weeklyGasRefills;
    }

    public LiveData<Double> getWeeklyAccessorySales() {
        if (weeklyAccessorySales == null) {
            weeklyAccessorySales = new MutableLiveData<>();
        }
        return weeklyAccessorySales;
    }

    public LiveData<Double> getTotalWeeklyExpenditure() {
        if (weeklyTotalExpenditure == null) {
            weeklyTotalExpenditure = new MutableLiveData<>();
            fetchWeeklyExpenditureData();
        }
        return weeklyTotalExpenditure;
    }

    public LiveData<Double> getWeeklyTotalCreditSales() {

        if (weeklyTotalCreditSales == null) {
            weeklyTotalCreditSales = new MutableLiveData<>();
            fetchWeeklyCreditSalesData();
        }

        return weeklyTotalCreditSales;
    }

    public LiveData<Double> getMonthlySales() {
        if (monthlySales == null) {

            monthlySales = new MutableLiveData<>();
            fetchMonthlySalesData();
        }
        return monthlySales;
    }

    public LiveData<Integer> getMonthlyTransactions() {
        if (monthlyTransactions == null) {

            monthlyTransactions = new MutableLiveData<>();
        }
        return monthlyTransactions;
    }

    public LiveData<Double> getMonthlyNewGasSales() {
        if (monthlyNewGasSales == null) {
            monthlyNewGasSales = new MutableLiveData<>();
        }
        return monthlyNewGasSales;
    }

    public LiveData<Double> getMonthlyGasRefills() {
        if (monthlyGasRefills == null) {
            monthlyGasRefills = new MutableLiveData<>();
        }
        return monthlyGasRefills;
    }

    public LiveData<Double> getMonthlyAccessorySales() {
        if (monthlyAccessorySales == null) {
            monthlyAccessorySales = new MutableLiveData<>();
        }
        return monthlyAccessorySales;
    }

    public LiveData<Double> getTotalMonthlyExpenditure() {
        if (monthlyTotalExpenditure == null) {
            monthlyTotalExpenditure = new MutableLiveData<>();
            fetchMonthlyExpenditureData();
        }
        return monthlyTotalExpenditure;
    }

    public LiveData<Double> getMonthlyTotalCreditSales() {

        if (monthlyTotalCreditSales == null) {
            monthlyTotalCreditSales = new MutableLiveData<>();
            fetchMonthlyCreditSalesData();
        }

        return monthlyTotalCreditSales;
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

    private void fetchWeeklySalesData() {
        DatabaseReference ref = reference.child("Sales");
        Query query = ref.orderByChild("date").startAt(firstDayOfWeek).endAt(dateToday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                weeklyTransactions.clear();
                for (DataSnapshot weeklySnapshot : snapshot.getChildren()) {

                    Transaction transaction = weeklySnapshot.getValue(Transaction.class);
                    weeklyTransactions.add(transaction);

                    double myWeeklySales = 0;

                    for (Transaction trans : weeklyTransactions) {
                        myWeeklySales = myWeeklySales + trans.getTotalPrice();
                    }

                    if (transaction.getTransactionType().equals(constants.NEW_GAS_CATEGORY)) {
                        weeklyNewGasList.add(transaction);
                        double weeklyGasSales = 0;

                        for (Transaction trans : weeklyNewGasList) {

                            weeklyGasSales = weeklyGasSales + trans.getTotalPrice();
                            weeklyNewGasSales.postValue(weeklyGasSales);

                        }
                    }

                    if (transaction.getTransactionType().equals(constants.GAS_REFILL_CATEGORY)) {

                        weeklyGasRefillList.add(transaction);

                        double weeklyRefills = 0;
                        for (Transaction trans : weeklyGasRefillList) {
                            weeklyRefills = weeklyRefills + trans.getTotalPrice();
                            weeklyGasRefills.postValue(weeklyRefills);
                        }
                    }

                    if (transaction.getTransactionType().equals(constants.ACCESSORY_CATEGORY)) {
                        weeklyAccessoryList.add(transaction);

                        double weeklyAccessories = 0;

                        for (Transaction trans : weeklyAccessoryList) {
                            weeklyAccessories = weeklyAccessories + trans.getTotalPrice();
                            weeklyAccessorySales.postValue(weeklyAccessories);
                        }
                    }

                    weeklySales.postValue(myWeeklySales);
                    weeklyTotalTransactions.postValue(weeklyTransactions.size());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchWeeklyExpenditureData() {
        DatabaseReference ref = reference.child("Expenditure");
        Query query = ref.orderByChild("date").startAt(firstDayOfWeek).endAt(dateToday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                weeklyExpenseList.clear();
                for (DataSnapshot weeklyExpenseSnapshot : snapshot.getChildren()) {

                    Expense expense = weeklyExpenseSnapshot.getValue(Expense.class);
                    weeklyExpenseList.add(expense);

                    double myWeeklySales = 0;

                    for (Expense exp : weeklyExpenseList) {
                        myWeeklySales = myWeeklySales + exp.getPrice();
                    }

                    weeklyTotalExpenditure.postValue(myWeeklySales);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchWeeklyCreditSalesData() {
        DatabaseReference ref = reference.child("Creditors");
        Query query = ref.orderByChild("date").startAt(firstDayOfWeek).endAt(dateToday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                weeklyCreditList.clear();
                for (DataSnapshot weeklyCreditSaleSnapshot : snapshot.getChildren()) {

                    Credit credit = weeklyCreditSaleSnapshot.getValue(Credit.class);
                    weeklyCreditList.add(credit);

                    double myWeeklyCreditSales = 0;

                    for (Credit cred : weeklyCreditList) {
                        myWeeklyCreditSales = myWeeklyCreditSales + cred.getAmount();
                    }

                    weeklyTotalCreditSales.postValue(myWeeklyCreditSales);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchMonthlySalesData() {
        DatabaseReference ref = reference.child("Sales");
        Query query = ref.orderByChild("date").startAt(firstDayOfMonth).endAt(dateToday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                monthlyTransactionsList.clear();
                for (DataSnapshot weeklySnapshot : snapshot.getChildren()) {

                    Transaction transaction = weeklySnapshot.getValue(Transaction.class);
                    monthlyTransactionsList.add(transaction);

                    double myMonthlySales = 0;

                    for (Transaction trans : monthlyTransactionsList) {
                        myMonthlySales = myMonthlySales + trans.getTotalPrice();
                    }

                    if (transaction.getTransactionType().equals(constants.NEW_GAS_CATEGORY)) {
                        monthlyNewGasList.add(transaction);

                        double monthlyGasSales = 0;

                        for (Transaction trans : monthlyNewGasList) {
                            monthlyGasSales = monthlyGasSales + trans.getTotalPrice();
                            monthlyNewGasSales.postValue(monthlyGasSales);
                        }
                    }

                    if (transaction.getTransactionType().equals(constants.GAS_REFILL_CATEGORY)) {
                        monthlyGasRefillList.add(transaction);

                        double monthlyRefills = 0;

                        for (Transaction trans : monthlyGasRefillList) {
                            monthlyRefills = monthlyRefills + trans.getTotalPrice();
                            monthlyGasRefills.postValue(monthlyRefills);
                        }
                    }

                    if (transaction.getTransactionType().equals(constants.ACCESSORY_CATEGORY)) {
                        monthlyAccessoryList.add(transaction);

                        double monthlySales = 0;

                        for (Transaction trans : monthlyAccessoryList) {
                            monthlySales = monthlySales + trans.getTotalPrice();
                            monthlyAccessorySales.postValue(monthlySales);
                        }
                    }

                    monthlySales.postValue(myMonthlySales);
                    monthlyTransactions.postValue(monthlyTransactionsList.size());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchMonthlyExpenditureData() {
        DatabaseReference ref = reference.child("Expenditure");
        Query query = ref.orderByChild("date").startAt(firstDayOfMonth).endAt(dateToday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                monthlyExpenseList.clear();
                for (DataSnapshot weeklyExpenseSnapshot : snapshot.getChildren()) {

                    Expense expense = weeklyExpenseSnapshot.getValue(Expense.class);
                    monthlyExpenseList.add(expense);

                    double myMonthlySales = 0;

                    for (Expense exp : monthlyExpenseList) {
                        myMonthlySales = myMonthlySales + exp.getPrice();
                    }

                    monthlyTotalExpenditure.postValue(myMonthlySales);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchMonthlyCreditSalesData() {
        DatabaseReference ref = reference.child("Creditors");
        Query query = ref.orderByChild("date").startAt(firstDayOfMonth).endAt(dateToday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                monthlyCreditList.clear();
                for (DataSnapshot weeklyCreditSaleSnapshot : snapshot.getChildren()) {

                    Credit credit = weeklyCreditSaleSnapshot.getValue(Credit.class);
                    monthlyCreditList.add(credit);

                    double myMonthlyCreditSales = 0;

                    for (Credit cred : monthlyCreditList) {
                        myMonthlyCreditSales = myMonthlyCreditSales + cred.getAmount();
                    }

                    monthlyTotalCreditSales.postValue(myMonthlyCreditSales);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

  private void getCustomPeriodSales(long startDate, long endDate){

        double totalCustomSales=0;

        DatabaseReference ref = reference.child("Sales");
        Query query = ref.orderByChild("date").startAt(startDate).endAt(endDate);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                customPeriodSalesList.clear();
                for (DataSnapshot customPeriodSaleSnapshot:snapshot.getChildren()){

                    Transaction transaction=customPeriodSaleSnapshot.getValue(Transaction.class);
                    customPeriodSalesList.add(transaction);

                    double customSales=0;
                    for (Transaction trans: customPeriodSalesList){
                        customSales=customSales+trans.getTotalPrice();

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
