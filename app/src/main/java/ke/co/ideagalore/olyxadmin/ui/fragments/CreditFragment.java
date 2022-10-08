package ke.co.ideagalore.olyxadmin.ui.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.adapters.CreditAdapter;
import ke.co.ideagalore.olyxadmin.common.CustomDialogs;
import ke.co.ideagalore.olyxadmin.common.ValidateFields;
import ke.co.ideagalore.olyxadmin.databinding.FragmentCreditBinding;
import ke.co.ideagalore.olyxadmin.models.Credit;
import ke.co.ideagalore.olyxadmin.models.CreditRepayment;
import ke.co.ideagalore.olyxadmin.models.Transaction;

public class CreditFragment extends Fragment implements View.OnClickListener {

    FragmentCreditBinding binding;

    ValidateFields validator = new ValidateFields();

    List<Credit> creditList = new ArrayList<>();

    String dateToday, time, store, terminal, username;

    CustomDialogs customDialogs = new CustomDialogs();

    public CreditFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreditBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        dateToday = formatter.format(date);

        DateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
        time = timeFormat.format(new Date());

        getPreferenceData();
        getCreditorsData();

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchProduct(newText);
                return true;
            }
        });

        binding.fabCredit.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.fabCredit) {
            showCreditDialog();
        } else {
            Navigation.findNavController(view).navigate(R.id.mainFragment);
        }
    }

    private void searchProduct(String item) {
        ArrayList<Credit> filteredList = new ArrayList<>();
        for (Credit object : creditList) {
            if (object.getName().toLowerCase().contains(item.toLowerCase())) {
                filteredList.add(object);
            }
        }
        displayData(filteredList);
    }

    private void getCreditorsData() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Transactions").child("Creditors");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                creditList.clear();

                for (DataSnapshot creditSnapshot : snapshot.getChildren()) {

                    Credit credit = creditSnapshot.getValue(Credit.class);
                    assert credit != null;
                    if (credit.getAmount()>0)
                    creditList.add(0, credit);
                }

                displayData(creditList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void displayData(List<Credit> creditList) {
        binding.rvCreditors.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvCreditors.setHasFixedSize(true);
        CreditAdapter adapter = new CreditAdapter(creditList, item -> {

            String id = item.getCreditId();
            int amount = item.getAmount();
            int quantity = item.getQuantity();
            String name = item.getName();
            String product = item.getProduct();
            String phone=item.getPhone();
            showRepayCreditDialog(id, amount, quantity, name, product, phone);

        });
        binding.rvCreditors.setAdapter(adapter);
    }

    private void showRepayCreditDialog(String id, int amount, int quantity, String name, String product, String phone) {

        Dialog myDialog = new Dialog(getActivity());
        myDialog.setContentView(R.layout.repay_credit_dialog);
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        myDialog.show();

        TextView customer, creditProduct, balance;
        customer = myDialog.findViewById(R.id.tv_customer);
        customer.setText(name);
        creditProduct = myDialog.findViewById(R.id.tv_product);
        creditProduct.setText(product + " *" + quantity);
        balance = myDialog.findViewById(R.id.tv_amount);
        balance.setText("KES " + amount);

        EditText receivedAmount = myDialog.findViewById(R.id.edt_amount_received);

        TextView cancel=myDialog.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(view -> myDialog.dismiss());

        Button updateCredit=myDialog.findViewById(R.id.btn_update_credit);
        updateCredit.setOnClickListener(view -> {
            if (validateEditTextFields(receivedAmount)) {
                int received = Integer.parseInt(receivedAmount.getText().toString().trim());
                int creditBalance=amount-received;
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Transactions").child("Creditors");
                reference.child(id).child("amount").setValue(creditBalance).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){

                        RecordCreditPaid(name,phone,product,received,creditBalance, myDialog);

                    }
                });
            }
        });

    }

    private void RecordCreditPaid(String name, String phone, String product, int received, int creditBalance, Dialog myDialog) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Transactions").child("Repayments");
        String key=reference.push().getKey();
        CreditRepayment repayment=new CreditRepayment();
        repayment.setRepaymentId(key);
        repayment.setCustomer(name);
        repayment.setAttendant(username);
        repayment.setPhone(phone);
        repayment.setProduct(product);
        repayment.setAttendant(username);
        repayment.setAmount(received);
        repayment.setStore(store);
        repayment.setTime(time);
        repayment.setDate(dateToday);
        repayment.setBalance(creditBalance);

        reference.child(key).setValue(repayment).addOnCompleteListener(task1 -> myDialog.dismiss()).addOnFailureListener(e -> {
            customDialogs.showSnackBar(requireActivity(), e.getMessage());
        });

    }

    private void showCreditDialog() {
        Dialog myDialog = new Dialog(getActivity());
        myDialog.setContentView(R.layout.credit_dialog);
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        myDialog.show();

        EditText product, quantity, amount, name, phone;
        product = myDialog.findViewById(R.id.edt_product);
        quantity = myDialog.findViewById(R.id.edt_quantity);
        amount = myDialog.findViewById(R.id.edt_amount);
        name = myDialog.findViewById(R.id.edt_customer);
        phone = myDialog.findViewById(R.id.edt_phone);

        ProgressBar progressBar = myDialog.findViewById(R.id.progress_bar);

        Button save = myDialog.findViewById(R.id.btn_add_credit);
        TextView cancel = myDialog.findViewById(R.id.tv_cancel);

        cancel.setOnClickListener(view -> myDialog.dismiss());

        save.setOnClickListener(view -> {

            if (validateEditTextFields(product)
                    && validateEditTextFields(quantity)
                    && validateEditTextFields(amount)
                    && validateEditTextFields(name)
                    && validateEditTextFields(phone)) {
                progressBar.setVisibility(View.VISIBLE);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Transactions").child("Creditors");
                String key = reference.push().getKey();
                Credit credit = new Credit();
                credit.setCreditId(key);
                credit.setDate(dateToday);
                credit.setTime(time);
                credit.setProduct(product.getText().toString().trim());
                credit.setQuantity(Integer.parseInt(quantity.getText().toString().trim()));
                credit.setAmount(Integer.parseInt(amount.getText().toString().trim()));
                credit.setName(name.getText().toString().trim());
                credit.setPhone(phone.getText().toString().trim());
                credit.setStore(store);
                credit.setAttendant(username);

                assert key != null;
                reference.child(key).setValue(credit).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        myDialog.dismiss();
                    }
                }).addOnFailureListener(e -> {

                    progressBar.setVisibility(View.GONE);
                    customDialogs.showSnackBar(requireActivity(), "Oops! Something went wrong. Please try again.");

                });
            }
        });
    }

    public boolean validateEditTextFields(EditText editText) {
        String input = editText.getText().toString();
        if (!input.isEmpty()) {
            return true;
        } else {
            editText.setHint("This field cannot be empty ");
            editText.setHintTextColor(getResources().getColor(R.color.accent));
            return false;
        }
    }

    private void getPreferenceData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Terminal", MODE_PRIVATE);
        store = sharedPreferences.getString("store", null);
        terminal = sharedPreferences.getString("terminal", null);
        username = sharedPreferences.getString("name", null);

    }

}