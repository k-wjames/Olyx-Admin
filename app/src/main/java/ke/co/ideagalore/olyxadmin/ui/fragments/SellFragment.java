package ke.co.ideagalore.olyxadmin.ui.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.adapters.SaleAdapter;
import ke.co.ideagalore.olyxadmin.common.CustomDialogs;
import ke.co.ideagalore.olyxadmin.databinding.FragmentSellBinding;
import ke.co.ideagalore.olyxadmin.models.Catalogue;
import ke.co.ideagalore.olyxadmin.models.CatalogueUpdate;
import ke.co.ideagalore.olyxadmin.models.Transaction;
import ke.co.ideagalore.olyxadmin.models.TransactionItem;


public class SellFragment extends Fragment implements View.OnClickListener {

    FragmentSellBinding binding;

    ArrayList<TransactionItem> myGasArray, myAccessoriesArray, myGasRefillArray;
    static ArrayList<Transaction> myTransactionArray = new ArrayList<>();
    ArrayList<Catalogue> catalogueArrayList = new ArrayList<>();

    TransactionItem transactionItem;

    int price, markedPrice, buyingPrice;
    static int  stokedCatalogueItem;

    String transactionType, selectedItem, dateToday, store, name, terminal, businessName;

    Transaction transaction;

    Dialog dialog;

    CustomDialogs customDialogs = new CustomDialogs();

    public SellFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSellBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myGasArray = new ArrayList<>();
        myAccessoriesArray = new ArrayList<>();
        myGasRefillArray = new ArrayList<>();

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        dateToday = formatter.format(date);

        getPreferenceData();
        getCatalogueData();

        binding.btnRefill.setOnClickListener(this);
        binding.btnBuyGas.setOnClickListener(this);
        binding.btnBuyAccessory.setOnClickListener(this);
        binding.btnCheckOut.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        if (view == binding.btnRefill) {
            transactionType = "Gas refill";
            refillGasDialog(transactionType);
        } else if (view == binding.btnBuyGas) {
            transactionType = "Gas sale";
            sellNewGasDialog(transactionType);

        } else if (view == binding.btnBuyAccessory) {
            transactionType = "Accessory sale";
            sellAnAccessoryDialog(transactionType);
        } else if (view == binding.btnCheckOut) {
            if (myTransactionArray.size() > 0) {

                for (int i = 0; i < myTransactionArray.size(); i++) {

                    Transaction item = myTransactionArray.get(i);
                    CommitNewTransaction commitNewTransaction = new CommitNewTransaction(this);
                    commitNewTransaction.execute(item);
                }
            } else {
                customDialogs.showSnackBar(requireActivity(), "No items have been added to the cart");
            }

        } else {

            Navigation.findNavController(view).navigate(R.id.mainFragment);

        }
    }

    private void getCatalogueData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Catalogue");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                catalogueArrayList.clear();
                for (DataSnapshot catalogueSnapshot : snapshot.getChildren()) {
                    Catalogue catalogue = catalogueSnapshot.getValue(Catalogue.class);

                    int stock = catalogue.getStockedQuantity();

                    if (stock > 0) {
                        catalogueArrayList.add(catalogue);

                        for (int i = 0; i < catalogueArrayList.size(); i++) {

                            String prod = catalogueArrayList.get(i).getProduct();
                            price = catalogueArrayList.get(i).getMarkedPrice();
                            String category = catalogueArrayList.get(i).getCategory();
                            int buyingPrice = catalogueArrayList.get(i).getBuyingPrice();
                            int stockedQuantity=catalogueArrayList.get(i).getStockedQuantity();
                            int markedPrice = catalogueArrayList.get(i).getMarkedPrice();
                            String prodId=catalogueArrayList.get(i).getProdId();


                            transactionItem = new TransactionItem();
                            if (category.equals("New Gas")) {

                                transactionItem.setMarkedPrice(price);
                                transactionItem.setProduct(prod);
                                transactionItem.setBuyingPrice(buyingPrice);
                                transactionItem.setAvailableStock(stockedQuantity);
                                transactionItem.setProductId(prodId);
                                myGasArray.add(transactionItem);

                            } else if (category.equals("Accessories")) {
                                transactionItem.setMarkedPrice(price);
                                transactionItem.setProduct(prod);
                                transactionItem.setBuyingPrice(buyingPrice);
                                transactionItem.setAvailableStock(stockedQuantity);
                                transactionItem.setProductId(prodId);
                                myAccessoriesArray.add(transactionItem);

                            } else {
                                transactionItem.setMarkedPrice(price);
                                transactionItem.setProduct(prod);
                                transactionItem.setBuyingPrice(buyingPrice);
                                transactionItem.setAvailableStock(stockedQuantity);
                                transactionItem.setProductId(prodId);
                                myGasRefillArray.add(transactionItem);
                            }
                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getActivity(), "Oops! Something went wrong. Be sure it's not you.", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void refillGasDialog(String transType) {

        if (myGasRefillArray.size() == 0) {
            customDialogs.showSnackBar(requireActivity(), "No gas refill data found. Please check with your admin");
            return;
        }

        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.refill_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        EditText edtQuantity = dialog.findViewById(R.id.edt_quantity);
        EditText edtPrice = dialog.findViewById(R.id.edt_selling_price);


        Spinner spinner = dialog.findViewById(R.id.spinner_product);

        ArrayAdapter<TransactionItem> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, myGasRefillArray);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = spinner.getSelectedItem().toString();

                TransactionItem item = (TransactionItem) spinner.getSelectedItem();
                markedPrice = item.getMarkedPrice();
                buyingPrice = item.getBuyingPrice();
                stokedCatalogueItem=item.getAvailableStock();
                edtPrice.setText(String.valueOf(markedPrice));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner.setAdapter(arrayAdapter);

        TextView cancel = dialog.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(view -> dialog.dismiss());

        Button save = dialog.findViewById(R.id.btn_add);
        save.setOnClickListener(view -> {


            String quantity = edtQuantity.getText().toString();
            int unitsSold;
            if (TextUtils.isEmpty(quantity)) {
                unitsSold = 1;
            } else {
                unitsSold = Integer.parseInt(quantity);
            }

            int pricePerUnit = Integer.parseInt(edtPrice.getText().toString());
            int totalPrice = unitsSold * pricePerUnit;
            int profit = totalPrice - (buyingPrice * unitsSold);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Transactions").child("Sales");
            String salesKey = ref.push().getKey();

            DateFormat formatter = new SimpleDateFormat("hh:mm:ss a");
            String time = formatter.format(new Date());

            transaction = new Transaction();
            transaction.setTerminalId(terminal);
            transaction.setAttendant(name);
            transaction.setStore(businessName);
            transaction.setProfit(profit);
            transaction.setProduct(selectedItem);
            transaction.setQuantity(unitsSold);
            transaction.setTotalPrice(totalPrice);
            transaction.setTransactionId(salesKey);
            transaction.setTime(time);
            transaction.setDate(dateToday);
            transaction.setBuyingPrice(buyingPrice);
            transaction.setSellingPrice(pricePerUnit);
            transaction.setTransactionType(transType);
            myTransactionArray.add(transaction);

            int totalShillings = 0;
            for (int i = 0; i < myTransactionArray.size(); i++) {

                totalShillings = +totalShillings + myTransactionArray.get(i).getTotalPrice();
                binding.tvTotalSpend.setText(totalShillings + "");

            }
            displayList();

        });
    }

    private void sellNewGasDialog(String transType) {

        if (myGasArray.size() == 0) {
            customDialogs.showSnackBar(requireActivity(), "No new gas data found. Please check with your admin");
            return;
        }

        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.sell_gas_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        EditText edtQuantity = dialog.findViewById(R.id.edt_quantity);
        EditText edtPrice = dialog.findViewById(R.id.edt_selling_price);


        Spinner spinner = dialog.findViewById(R.id.spinner_product);

        ArrayAdapter<TransactionItem> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, myGasArray);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = spinner.getSelectedItem().toString();
                TransactionItem item = (TransactionItem) spinner.getSelectedItem();
                markedPrice = item.getMarkedPrice();
                buyingPrice = item.getBuyingPrice();
                stokedCatalogueItem=item.getAvailableStock();
                edtPrice.setText(markedPrice + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner.setAdapter(arrayAdapter);

        TextView cancel = dialog.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(view -> dialog.dismiss());

        Button save = dialog.findViewById(R.id.btn_add);
        save.setOnClickListener(view -> {


            String quantity = edtQuantity.getText().toString();
            int unitsSold;
            if (TextUtils.isEmpty(quantity)) {
                unitsSold = 1;
            } else {
                unitsSold = Integer.parseInt(quantity);
            }

            int pricePerUnit = Integer.parseInt(edtPrice.getText().toString());
            int totalPrice = unitsSold * pricePerUnit;
            int profit = totalPrice - (buyingPrice * unitsSold);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Transactions").child("Sales");
            String salesKey = ref.push().getKey();

            DateFormat formatter = new SimpleDateFormat("hh:mm:ss a");
            String time = formatter.format(new Date());

            transaction = new Transaction();
            transaction.setTerminalId(terminal);
            transaction.setAttendant(name);
            transaction.setStore(businessName);
            transaction.setProfit(profit);
            transaction.setProduct(selectedItem);
            transaction.setQuantity(unitsSold);
            transaction.setTotalPrice(totalPrice);
            transaction.setTransactionId(salesKey);
            transaction.setTime(time);
            transaction.setDate(dateToday);
            transaction.setBuyingPrice(buyingPrice);
            transaction.setSellingPrice(pricePerUnit);
            transaction.setTransactionType(transType);
            myTransactionArray.add(transaction);

            int totalShillings = 0;
            for (int i = 0; i < myTransactionArray.size(); i++) {

                totalShillings = +totalShillings + myTransactionArray.get(i).getTotalPrice();
                binding.tvTotalSpend.setText(totalShillings + "");
            }
            displayList();

        });
    }

    private void sellAnAccessoryDialog(String transType) {
        if (myAccessoriesArray.size() == 0) {
            customDialogs.showSnackBar(requireActivity(), "No accessories data found. Please check with you admin");
            return;
        }
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.sell_accessory_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        EditText edtQuantity = dialog.findViewById(R.id.edt_quantity);
        EditText edtPrice = dialog.findViewById(R.id.edt_selling_price);


        Spinner spinner = dialog.findViewById(R.id.spinner_product);

        ArrayAdapter<TransactionItem> arrayAdapter = new ArrayAdapter<TransactionItem>(getActivity(), android.R.layout.simple_spinner_item, myAccessoriesArray);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = spinner.getSelectedItem().toString();
                TransactionItem item = (TransactionItem) spinner.getSelectedItem();
                markedPrice = item.getMarkedPrice();
                buyingPrice = item.getBuyingPrice();
                stokedCatalogueItem=item.getAvailableStock();
                edtPrice.setText(markedPrice + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner.setAdapter(arrayAdapter);

        TextView cancel = dialog.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(view -> dialog.dismiss());

        Button save = dialog.findViewById(R.id.btn_add);
        save.setOnClickListener(view -> {


            String quantity = edtQuantity.getText().toString();
            int unitsSold;
            if (TextUtils.isEmpty(quantity)) {
                unitsSold = 1;
            } else {
                unitsSold = Integer.parseInt(quantity);
            }

            int pricePerUnit = Integer.parseInt(edtPrice.getText().toString());
            int totalPrice = unitsSold * pricePerUnit;
            int profit = totalPrice - (buyingPrice * unitsSold);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Transactions").child("Sales");
            String salesKey = ref.push().getKey();

            DateFormat formatter = new SimpleDateFormat("hh:mm:ss a");
            String time = formatter.format(new Date());

            transaction = new Transaction();
            transaction.setTerminalId(terminal);
            transaction.setAttendant(name);
            transaction.setStore(businessName);
            transaction.setProfit(profit);
            transaction.setProduct(selectedItem);
            transaction.setQuantity(unitsSold);
            transaction.setTotalPrice(totalPrice);
            transaction.setTransactionId(salesKey);
            transaction.setTime(time);
            transaction.setDate(dateToday);
            transaction.setBuyingPrice(buyingPrice);
            transaction.setSellingPrice(pricePerUnit);
            transaction.setTransactionType(transType);
            myTransactionArray.add(transaction);

            int totalShillings = 0;
            for (int i = 0; i < myTransactionArray.size(); i++) {

                totalShillings = totalShillings + myTransactionArray.get(i).getTotalPrice();
                binding.tvTotalSpend.setText(totalShillings + "");
            }
            displayList();

        });
    }

    private static class CommitNewTransaction extends AsyncTask<Transaction, Void, Void> {

        CustomDialogs dialogs = new CustomDialogs();
        private WeakReference<SellFragment> weakReference;

        CommitNewTransaction(SellFragment fragment) {
            weakReference = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SellFragment fragment = weakReference.get();
            if (fragment == null || fragment.isRemoving()) return;
            dialogs.showProgressDialog(fragment.getActivity(), fragment.getResources().getString(R.string.transaction_in_progress));
        }

        @Override
        protected Void doInBackground(Transaction... transactions) {
            for (int i = 0; i < transactions.length; i++) {

                Transaction transaction = transactions[i];
                String key = transactions[i].getTransactionId();
                String terminal = transactions[i].getTerminalId();
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Transactions").child("Sales");
                myRef.child(key).setValue(transaction).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            dialogs.dismissProgressDialog();

                        }
                    }
                });

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            SellFragment fragment = weakReference.get();
            if (fragment == null || fragment.isRemoving()) return;

            Navigation.findNavController(weakReference.get().requireView()).navigate(R.id.mainFragment);
        }
    }

    private void displayList() {
        binding.rvSales.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvSales.setHasFixedSize(true);
        SaleAdapter adapter = new SaleAdapter(getActivity(), myTransactionArray);
        binding.rvSales.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        dialog.dismiss();
    }

    private void getPreferenceData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Terminal", MODE_PRIVATE);
        store = sharedPreferences.getString("store", null);
        terminal = sharedPreferences.getString("terminal", null);
        name = sharedPreferences.getString("name", null);
        businessName = sharedPreferences.getString("business", null);

    }
}