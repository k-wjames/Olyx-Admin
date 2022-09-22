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

    String terminal, name, businessName, terminalId, selectedItem, dateToday;

    int profits,totalExpenses = 0;

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
        binding.cvTransactions.setOnClickListener(this);
        binding.cvCatalogue.setOnClickListener(this);

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
        } else if (view == binding.cvTransactions) {
            Navigation.findNavController(view).navigate(R.id.transactionsFragment);
        } else if (view == binding.ivFilter) {
            showFilterPeriodDialog();
        } else {
            Navigation.findNavController(view).navigate(R.id.catalogueItemsFragment);
        }

    }

    private void filterTodayData(String day) {
        for (Transaction transaction : transactionList) {
            String date = transaction.getDate();
            List<Transaction> todayTransactions = new ArrayList<>();
            if (date.equals(day)) {
                todayTransactions.add(0, transaction);
            }
            displayTransactionsList(todayTransactions);
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

            }
            binding.tvPeriod.setText("Today");
            displayTransactionsList(todayList);
        }
    }

    private void filterDataBySpecificDate(String selectedDate) {
        List<Transaction> list = new ArrayList<>();
        for (Transaction transaction : transactionList) {
            if (selectedDate.equals(transaction.getDate())) {

                list.add(0, transaction);

            }
            displayTransactionsList(list);
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
        catalogueList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(myTerminal).child("Catalogue");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot storeSnapshot : snapshot.getChildren()) {
                    Catalogue catalogue = storeSnapshot.getValue(Catalogue.class);
                    catalogueList.add(catalogue);
                    if (catalogueList.size() < 10) {
                        binding.tvCatalogueItems.setText(0 + "" + catalogueList.size());
                    } else
                        binding.tvCatalogueItems.setText(String.valueOf(catalogueList.size()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getStoresData(String myTerminal) {
        storesList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(myTerminal).child("Stores");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot storeSnapshot : snapshot.getChildren()) {
                    Stores store = storeSnapshot.getValue(Stores.class);
                    storesList.add(store);
                    if (storesList.size() < 10) {
                        binding.tvStores.setText(0 + "" + storesList.size());
                    } else
                        binding.tvStores.setText(String.valueOf(storesList.size()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTransactionsData(String myTerminal) {
        transactionList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(myTerminal).child("Transactions").child("Sales");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot transactionSnapshot : snapshot.getChildren()) {

                    Transaction transaction = transactionSnapshot.getValue(Transaction.class);
                    transactionList.add(0, transaction);

                    displayTransactionsList(transactionList);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getExpenditureData(String myTerminal) {
        expenseList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(myTerminal).child("Transactions").child("Expenditure");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                    Expense expense = expenseSnapshot.getValue(Expense.class);
                    expenseList.add(expense);
                    for (Expense item : expenseList) {
                        int exp = item.getPrice();
                        totalExpenses = totalExpenses + exp;
                        binding.tvExpenses.setText("KES " + totalExpenses);

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
        binding.tvDate.setText(day + " " + month.toUpperCase() + " " + year);

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

        int sales = 0;
        int totalProfit = 0;

        if (list.size() < 10) {
            binding.tvTransactions.setText("0" + list.size());
        } else {
            binding.tvTransactions.setText(String.valueOf(list.size()));
        }

        for (Transaction item : list) {
            sales = sales + item.getTotalPrice();
            binding.tvSales.setText("KES " + sales);
            totalProfit = totalProfit + item.getProfit();
            profits = totalProfit;
            binding.tvTotalProfits.setText("KES " + profits);
        }

        binding.tvNetProfits.setText("KES " + (profits - totalExpenses));

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

    public void savePreferencesData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Terminal", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("business", businessName);
        editor.putString("terminal", terminalId);
        editor.commit();
        getPreferenceData();
    }


}