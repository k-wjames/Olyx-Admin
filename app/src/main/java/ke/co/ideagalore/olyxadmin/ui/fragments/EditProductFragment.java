package ke.co.ideagalore.olyxadmin.ui.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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
import ke.co.ideagalore.olyxadmin.databinding.FragmentEditProductBinding;
import ke.co.ideagalore.olyxadmin.models.Catalogue;
import ke.co.ideagalore.olyxadmin.models.Stores;

public class EditProductFragment extends Fragment implements View.OnClickListener {
    FragmentEditProductBinding binding;
    String terminal, productCategory, product, productId, selectedItem,selectedShop,shop;
    int bPrice, sPrice, numberStoked;
    DatabaseReference reference;

    CustomDialogs customDialogs = new CustomDialogs();
    ValidateFields validator = new ValidateFields();

    List<String> storesList = new ArrayList<>();

    public EditProductFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPreferenceData();
        getBundleData();
        getStoresData();

        String[] category = new String[]{"Gas Refill", "New Gas", "Accessories"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                category);
        categoryAdapter.setDropDownViewResource(R.layout.spinner_item);
        binding.spinnerCategory.setAdapter(categoryAdapter);
        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = binding.spinnerCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        binding.spinnerCategory.setAdapter(categoryAdapter);

        reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Catalogue");
        binding.tvDelete.setOnClickListener(this);
        binding.btnEditItem.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view == binding.btnEditItem) {
            updateItemData(productId);
        }else if (view==binding.tvDelete){
            deleteProduct(productId);
        }
    }

    private void getPreferenceData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Terminal", MODE_PRIVATE);
        terminal = sharedPreferences.getString("terminal", null);
    }

    private void getBundleData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            productCategory = arguments.get("category").toString();
            product = arguments.get("product").toString();
            bPrice = arguments.getInt("buyingPrice");
            sPrice = arguments.getInt("sellingPrice");
            numberStoked = arguments.getInt("stockedItems");
            productId = arguments.get("productId").toString();
            shop=arguments.get("shop").toString();
            setViews(productCategory,shop,product, bPrice, sPrice, numberStoked);
        }
    }

    private void setViews(String productCategory,String shop,String product, int bPrice, int sPrice, int numberStoked) {

        binding.spinnerCategory.setSelection(binding.spinnerCategory.getSelectedItemPosition());
        binding.spinnerShop.setSelection(binding.spinnerShop.getSelectedItemPosition());
        binding.edtProduct.setText(product);
        binding.edtBuyingPrice.setText(String.valueOf(bPrice));
        binding.edtMarkedPrice.setText(String.valueOf(sPrice));
        binding.edtStocked.setText(String.valueOf(numberStoked));
    }

    private void updateItemData(String productId) {

        if (validator.validateEditTextFields(getActivity(), binding.edtProduct, "Product")
                && validator.validateEditTextFields(getActivity(), binding.edtBuyingPrice, "Buying price")
                && validator.validateEditTextFields(getActivity(), binding.edtMarkedPrice, "Selling price")
                && validator.validateEditTextFields(getActivity(), binding.edtStocked, "Items stocked")) {
            Catalogue catalogue = new Catalogue();
            catalogue.setShop(selectedShop);
            catalogue.setCategory(selectedItem);
            catalogue.setProdId(productId);
            catalogue.setBuyingPrice(Integer.parseInt(binding.edtBuyingPrice.getText().toString()));
            catalogue.setMarkedPrice(Integer.parseInt(binding.edtMarkedPrice.getText().toString()));
            catalogue.setProduct(binding.edtProduct.getText().toString());
            catalogue.setAvailableItems(0);
            catalogue.setSoldItems(0);
            catalogue.setStockedQuantity(Integer.parseInt(binding.edtStocked.getText().toString()));
            reference.child(productId).setValue(catalogue).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    customDialogs.showSnackBar(getActivity(), "Item successfully updated.");
                    resetViews();
                    Navigation.findNavController(requireView()).navigate(R.id.catalogueItemsFragment);
                }

            }).addOnFailureListener(e -> customDialogs.showSnackBar(getActivity(), e.getMessage()));
        }
    }

    private void getStoresData() {
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

                    ArrayAdapter<String> shopAdapter = new ArrayAdapter<>(requireActivity(),
                            android.R.layout.simple_spinner_item,
                            storesList);
                    shopAdapter.setDropDownViewResource(R.layout.spinner_item);
                    binding.spinnerShop.setAdapter(shopAdapter);
                    binding.spinnerShop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedShop = binding.spinnerShop.getSelectedItem().toString();

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    binding.spinnerShop.setAdapter(shopAdapter);
                    customDialogs.dismissProgressDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                customDialogs.dismissProgressDialog();
                customDialogs.showSnackBar(requireActivity(), error.getMessage());

            }
        });

    }

    private void deleteProduct(String productId) {
        reference.child(productId).setValue(null).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                customDialogs.showSnackBar(requireActivity(), "Item successfully deleted.");
                resetViews();
            }

        }).addOnFailureListener(e -> {
            customDialogs.showSnackBar(requireActivity(), e.getMessage());
        });
    }

    private void resetViews() {
        binding.spinnerCategory.setSelection(0);
        binding.spinnerShop.setSelection(0);
        binding.edtProduct.setText(null);
        binding.edtBuyingPrice.setText(0 + "");
        binding.edtMarkedPrice.setText(0 + "");
        binding.edtStocked.setText(0 + "");

    }

}