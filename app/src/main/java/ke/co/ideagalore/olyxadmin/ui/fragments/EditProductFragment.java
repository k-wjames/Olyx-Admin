package ke.co.ideagalore.olyxadmin.ui.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.common.CustomDialogs;
import ke.co.ideagalore.olyxadmin.common.ValidateFields;
import ke.co.ideagalore.olyxadmin.databinding.FragmentEditProductBinding;
import ke.co.ideagalore.olyxadmin.models.Refill;

public class EditProductFragment extends Fragment implements View.OnClickListener {
    FragmentEditProductBinding binding;
    String terminal, productCategory, product, productId;
    int bPrice, sPrice, numberStoked;
    DatabaseReference reference;

    CustomDialogs customDialogs = new CustomDialogs();
    ValidateFields validator = new ValidateFields();

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

        reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("GasRefill");
        binding.ivDelete.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);
        binding.btnEditItem.setOnClickListener(this);
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
            setViews(productCategory, product, bPrice, sPrice, numberStoked);
        }
    }

    private void setViews(String productCategory, String product, int bPrice, int sPrice, int numberStoked) {
        binding.tvCategory.setText(productCategory);
        binding.edtProduct.setText(product);
        binding.edtBuyingPrice.setText(bPrice + "");
        binding.edtMarkedPrice.setText(sPrice + "");
        binding.edtStocked.setText(numberStoked + "");
    }

    @Override
    public void onClick(View view) {
        if (view == binding.ivDelete) {
            deleteProduct(productId);
        } else if (view == binding.ivBack) {
            Navigation.findNavController(view).navigate(R.id.gasRefillItemsFragment);
        } else {
            updateItemData(productId);
        }
    }

    private void deleteProduct(String productId) {
        reference.child(productId).setValue(null).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                customDialogs.showSnackBar(getActivity(), "Item successfully delete.");
                resetViews();
            }

        }).addOnFailureListener(e -> {
            customDialogs.showSnackBar(getActivity(), e.getMessage());
        });
    }

    private void updateItemData(String productId) {

        if (validator.validateEditTextFields(getActivity(), binding.edtProduct, "Product")
                && validator.validateEditTextFields(getActivity(), binding.edtBuyingPrice, "Buying price")
                && validator.validateEditTextFields(getActivity(), binding.edtMarkedPrice, "Selling price")
                && validator.validateEditTextFields(getActivity(), binding.edtStocked, "Items stocked")) {
            Refill refill = new Refill();
            refill.setProdId(productId);
            refill.setBuyingPrice(Integer.valueOf(binding.edtBuyingPrice.getText().toString()));
            refill.setMarkedPrice(Integer.valueOf(binding.edtMarkedPrice.getText().toString()));
            refill.setProduct(binding.edtProduct.getText().toString());
            refill.setNumberStocked(Integer.valueOf(binding.edtStocked.getText().toString()));
            reference.child(productId).setValue(refill).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    customDialogs.showSnackBar(getActivity(), "Item successfully updated.");
                    resetViews();
                }

            }).addOnFailureListener(e -> customDialogs.showSnackBar(getActivity(), e.getMessage()));
        }
    }

    private void resetViews() {

        binding.tvCategory.setText(null);
        binding.edtProduct.setText(null);
        binding.edtBuyingPrice.setText(0 + "");
        binding.edtMarkedPrice.setText(0 + "");
        binding.edtStocked.setText(0 + "");

    }
}