package ke.co.ideagalore.olyxadmin.ui.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.echo.holographlibrary.PieSlice;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.adapters.ExpenseAdapter;
import ke.co.ideagalore.olyxadmin.adapters.TransactionsAdapter;
import ke.co.ideagalore.olyxadmin.databinding.FragmentMainBinding;
import ke.co.ideagalore.olyxadmin.models.Catalogue;
import ke.co.ideagalore.olyxadmin.models.Expense;
import ke.co.ideagalore.olyxadmin.models.Stores;
import ke.co.ideagalore.olyxadmin.models.Transaction;

public class MainFragment extends Fragment implements View.OnClickListener {

    FragmentMainBinding binding;
    List<Stores> storesList = new ArrayList<>();
    List<Catalogue> catalogueList = new ArrayList<>();
    List<Transaction> transactionList = new ArrayList<>();
    List<Expense> expenseList = new ArrayList<>();

    List<Transaction> refillList = new ArrayList<>();
    List<Transaction> gasSales = new ArrayList<>();
    List<Transaction> accessories = new ArrayList<>();

    String terminal, name, businessName, terminalId, dateToday;

    int profits = 0;
    int netProfit = 0;
    int totalAccessoriesSales = 0;
    int totalNewGasSales = 0;
    int totalRefillGasSales = 0;

    SimpleDateFormat formatter;

    public MainFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getCurrentDate();
        getPreferenceData();
        Date date = new Date();
        formatter = new SimpleDateFormat("dd/MM/yyyy");
        dateToday = formatter.format(date);

        binding.tvViewTransactions.setOnClickListener(this);
        binding.btnSales.setOnClickListener(this);
        binding.btnExpenditure.setOnClickListener(this);
        binding.cvTransact.setOnClickListener(this);
        binding.cvCatalogue.setOnClickListener(this);
        binding.btnAddExpense.setOnClickListener(this);
        binding.btnAddCredit.setOnClickListener(this);

        binding.ivFilter.setOnClickListener(this);

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
            showRangePickerDialog();
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
            if (transaction.getDate().equals(dateToday)) {

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

    private void showRangePickerDialog() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select range");
        MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();
        materialDatePicker.show(requireActivity().getSupportFragmentManager(), "Range Picker");
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            if (selection.first != null && selection.second != null) {

                Long start = selection.first;
                Long end = selection.second;
                binding.tvPeriod.setText(start + " to " + end);


            }
        });

    }

    private void getCatalogueData(String myTerminal) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(myTerminal).child("Catalogue");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                catalogueList.clear();
                for (DataSnapshot storeSnapshot : snapshot.getChildren()) {
                    Catalogue catalogue = storeSnapshot.getValue(Catalogue.class);
                    catalogueList.add(catalogue);
                    /*if (catalogueList.size() < 10) {
                        binding.tvCatalogueItems.setText(0 + "" + catalogueList.size());
                    } else
                        binding.tvCatalogueItems.setText(String.valueOf(catalogueList.size()));*/

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getStoresData(String myTerminal) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(myTerminal).child("Stores");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                storesList.clear();
                for (DataSnapshot storeSnapshot : snapshot.getChildren()) {
                    Stores store = storeSnapshot.getValue(Stores.class);
                    storesList.add(store);
                    /*if (storesList.size() < 10) {
                        binding.tvStores.setText(0 + "" + storesList.size());
                    } else
                        binding.tvStores.setText(String.valueOf(storesList.size()));*/

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                        //showPieChart(totalAccessoriesSales, totalNewGasSales, totalRefillGasSales);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

   /* private void showPieChart(int accessories, int newGas, int refill) {
        binding.graph.removeSlices();
        PieSlice sliceRefill = new PieSlice();
        sliceRefill.setColor(requireActivity().getResources().getColor(R.color.accent));
        sliceRefill.setValue(refill);
        binding.graph.addSlice(sliceRefill);
        PieSlice sliceNewGas = new PieSlice();
        sliceNewGas.setColor(requireActivity().getResources().getColor(R.color.accentTwo));
        sliceNewGas.setValue(newGas);
        binding.graph.addSlice(sliceNewGas);
        PieSlice sliceAccessories = new PieSlice();
        sliceAccessories.setColor(requireActivity().getResources().getColor(R.color.accentThree));
        sliceAccessories.setValue(accessories);
        binding.graph.addSlice(sliceAccessories);
    }*/

    private void getExpenditureData(String myTerminal) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(myTerminal).child("Transactions").child("Expenditure");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                expenseList.clear();

                for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                    Expense expense = expenseSnapshot.getValue(Expense.class);

                    if (expense.getDate().equals(dateToday)) {
                        expenseList.add(0, expense);

                        int exp = 0;
                        for (Expense myExpense : expenseList) {
                            exp = exp + myExpense.getPrice();

                            if (exp < 10) {

                                binding.tvExpenses.setText("KES 00");

                            } else if (exp>0 && exp < 10) {

                                binding.tvExpenses.setText("KES 0" + exp);

                            } else {
                                binding.tvExpenses.setText("KES " + exp);
                                binding.tvNetProfits.setText("KES " + netProfit);
                            }
                            netProfit = profits - exp;

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
            binding.tvName.setText(name + ",");
            getCatalogueData(terminal);
            getStoresData(terminal);
            getTransactionsData(terminal);
            getExpenditureData(terminal);
        }
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

    public void savePreferencesData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Terminal", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("business", businessName);
        editor.putString("terminal", terminalId);
        editor.commit();
        getPreferenceData();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceData();
    }
}