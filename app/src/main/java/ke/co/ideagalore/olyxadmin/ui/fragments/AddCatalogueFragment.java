package ke.co.ideagalore.olyxadmin.ui.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.common.CustomDialogs;
import ke.co.ideagalore.olyxadmin.common.ValidateFields;
import ke.co.ideagalore.olyxadmin.databinding.FragmentAddCatalogueBinding;
import ke.co.ideagalore.olyxadmin.models.Catalogue;
import ke.co.ideagalore.olyxadmin.models.Stores;
import ke.co.ideagalore.olyxadmin.viewmodels.AddCatalogueViewModel;

public class AddCatalogueFragment extends Fragment implements View.OnClickListener {

    FragmentAddCatalogueBinding binding;
    ValidateFields validator = new ValidateFields();
    CustomDialogs customDialogs = new CustomDialogs();
    String terminal, selectedItem, selectedShop;

    AddCatalogueViewModel viewModel;

    public AddCatalogueFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddCatalogueBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPreferenceData();

        viewModel = new ViewModelProvider(this).get(AddCatalogueViewModel.class);

        getStoresData();
        setCategorySpinner();

        binding.btnAddItem.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view == binding.btnAddItem) {
            saveNewCategoryItem();
        }
    }

    private void setCategorySpinner() {
        String[] category = new String[]{"Gas Refill", "New Gas", "Accessories"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                category);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        binding.spinnerCategory.setAdapter(arrayAdapter);
        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = binding.spinnerCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        binding.spinnerCategory.setAdapter(arrayAdapter);
    }

    public void getPreferenceData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Terminal", MODE_PRIVATE);
        terminal = sharedPreferences.getString("terminal", null);
        selectedShop=sharedPreferences.getString("business", null);
    }

    private void getStoresData() {
        List<String> storesList = new ArrayList<>();
        customDialogs.showProgressDialog(requireActivity(), "Fetching stores");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Stores");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                storesList.clear();

                for (DataSnapshot storeSnapshot : snapshot.getChildren()) {

                    Stores store = storeSnapshot.getValue(Stores.class);
                    String storeName = store.getStore();
                    storesList.add(0, storeName);
                    if (storesList.isEmpty()){
                        getStoresData();
                        return;
                    }

                    customDialogs.dismissProgressDialog();
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireActivity(),
                        android.R.layout.simple_spinner_item,
                        storesList);
                arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                binding.spinnerShop.setAdapter(arrayAdapter);
                binding.spinnerShop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedShop = binding.spinnerShop.getSelectedItem().toString();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                binding.spinnerShop.setAdapter(arrayAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                customDialogs.dismissProgressDialog();
                customDialogs.showSnackBar(requireActivity(), error.getMessage());

            }
        });

    }

    private void saveNewCategoryItem() {

        if (validator.validateEditTextFields(requireActivity(), binding.edtProduct, "")
                && validator.validateEditTextFields(requireActivity(), binding.edtBuyingPrice, "")
                && validator.validateEditTextFields(requireActivity(), binding.edtMarkedPrice, "")
                && validator.validateEditTextFields(requireActivity(), binding.edtStocked, "")) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Catalogue");
            String prodId = reference.push().getKey();
            Catalogue catalogue = new Catalogue();
            catalogue.setProdId(prodId);
            catalogue.setCategory(selectedItem);
            catalogue.setProduct(binding.edtProduct.getText().toString().trim());
            catalogue.setBuyingPrice(Integer.parseInt(binding.edtBuyingPrice.getText().toString().trim()));
            catalogue.setMarkedPrice(Integer.parseInt(binding.edtMarkedPrice.getText().toString().trim()));
            catalogue.setStockedQuantity(Integer.parseInt(binding.edtStocked.getText().toString().trim()));
            catalogue.setSoldItems(0);
            catalogue.setShop(selectedShop);
            assert prodId != null;
            reference.child(prodId).setValue(catalogue).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    customDialogs.showSnackBar(requireActivity(), "New " + binding.edtProduct.getText().toString().trim() + " successfully added to your catalogue");
                    resetFields();
                }

            }).addOnFailureListener(e -> {
                customDialogs.showSnackBar(getActivity(), e.getMessage());
            });

        }
    }

    private void resetFields() {
        binding.spinnerCategory.setSelection(0);
        binding.spinnerShop.setSelection(0);
        binding.edtProduct.setText("");
        binding.edtBuyingPrice.setText("");
        binding.edtMarkedPrice.setText("");
        binding.edtStocked.setText("00");
    }

}