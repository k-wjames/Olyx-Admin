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
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.adapters.RefillAdapter;
import ke.co.ideagalore.olyxadmin.common.CustomDialogs;
import ke.co.ideagalore.olyxadmin.common.ValidateFields;
import ke.co.ideagalore.olyxadmin.databinding.FragmentGasRefillItemsBinding;
import ke.co.ideagalore.olyxadmin.models.Catalogue;
import ke.co.ideagalore.olyxadmin.models.Refill;

public class GasRefillItemsFragment extends Fragment implements View.OnClickListener {

    FragmentGasRefillItemsBinding binding;

    ValidateFields validator = new ValidateFields();
    CustomDialogs customDialogs = new CustomDialogs();

    String terminal, name, business;

    DatabaseReference reference;

    List<Refill> refillList = new ArrayList<>();

    public GasRefillItemsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGasRefillItemsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        getPreferenceData();

        reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("GasRefill");
        addCatalogueData();
        getGasRefillItems();

        binding.ivBack.setOnClickListener(this);
        binding.ivAdd.setOnClickListener(this);
    }

    private void addCatalogueData() {
        reference=FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Catalogue");
        String prodId=reference.push().getKey();
        Catalogue catalogue=new Catalogue();
        catalogue.setCategory("New Gas");
        catalogue.setProduct("Pro Gas 6Kg");
        catalogue.setBuyingPrice(2500);
        catalogue.setMarkedPrice(3800);
        catalogue.setStockedQuantity(30);
        catalogue.setProdId(prodId);

        reference.child(prodId).setValue(catalogue);
    }

    @Override
    public void onClick(View view) {

        if (view == binding.ivBack) {

            Navigation.findNavController(view).navigate(R.id.catalogueItemsFragment);
        } else {
            showAddNewRefillGasItem();
        }

    }

    private void showAddNewRefillGasItem() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.set_refill_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        EditText productName = dialog.findViewById(R.id.edt_item);
        EditText buyingPrice = dialog.findViewById(R.id.edt_buying_price);
        EditText markedPrice = dialog.findViewById(R.id.edt_selling_price);

        ProgressBar progressBar =dialog.findViewById(R.id.progress_bar);

        TextView cancel = dialog.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(view -> dialog.dismiss());

        Button add = dialog.findViewById(R.id.btn_add);
        add.setOnClickListener(view -> {
            if (validator.validateEditTextFields(getActivity(), productName, "Item name")
                    && validator.validateEditTextFields(getActivity(), buyingPrice, "Buying price")
                    && validator.validateEditTextFields(getActivity(), markedPrice, "Marked price")) {

                String item = productName.getText().toString().trim();
                int bPrice = Integer.parseInt(buyingPrice.getText().toString().trim());
                int mPrice = Integer.parseInt(markedPrice.getText().toString().trim());

                saveNewGasRefillItem(item, bPrice, mPrice, dialog, progressBar);
            }
        });
    }

    private void saveNewGasRefillItem(String item, int bPrice, int mPrice, Dialog dialog, ProgressBar progress) {

        String itemId = reference.push().getKey();

        Refill refill = new Refill();
        refill.setProdId(itemId);
        refill.setBuyingPrice(bPrice);
        refill.setMarkedPrice(mPrice);
        refill.setProduct(item);

        progress.setVisibility(View.VISIBLE);
        reference.child(itemId).setValue(refill).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                dialog.dismiss();
                customDialogs.showSnackBar(getActivity(), "Products successfully added.");
                getGasRefillItems();
            }

        }).addOnFailureListener(e -> customDialogs.showSnackBar(requireActivity(), e.getMessage()));

    }

    public void getPreferenceData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Terminal", MODE_PRIVATE);
        business = sharedPreferences.getString("business", null);
        terminal = sharedPreferences.getString("terminal", null);
        name = sharedPreferences.getString("name", null);

    }

    private void getGasRefillItems() {
        binding.tvNotify.setText("Fetching data");
        binding.progressBar.setVisibility(View.VISIBLE);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                refillList.clear();

                for (DataSnapshot refillSnapshot : snapshot.getChildren()) {

                    Refill refill = refillSnapshot.getValue(Refill.class);
                    refillList.add(0, refill);

                }

                RefillAdapter adapter = new RefillAdapter(refillList);
                binding.rvProducts.setLayoutManager(new LinearLayoutManager(getActivity()));
                binding.rvProducts.setHasFixedSize(true);
                binding.tvNotify.setText("Products");
                binding.progressBar.setVisibility(View.GONE);
                binding.rvProducts.setAdapter(adapter);
                binding.tvTotalItems.setText(refillList.size()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}