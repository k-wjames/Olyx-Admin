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
import ke.co.ideagalore.olyxadmin.common.MySharedPreferences;
import ke.co.ideagalore.olyxadmin.common.ValidateFields;
import ke.co.ideagalore.olyxadmin.databinding.FragmentEditStoreBinding;
import ke.co.ideagalore.olyxadmin.models.Stores;

public class EditStoreFragment extends Fragment implements View.OnClickListener {

    FragmentEditStoreBinding binding;
    String storeName, storeId, storeLocation, terminal;
    ValidateFields validator = new ValidateFields();
    CustomDialogs customDialogs = new CustomDialogs();
    MySharedPreferences preferences=new MySharedPreferences();

    public EditStoreFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditStoreBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getBundleData();
        getPreferenceData();
        binding.btnEditStore.setOnClickListener(this);
        binding.tvDeleteStore.setOnClickListener(this);
    }

    private void getBundleData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            storeName = arguments.get("store").toString();
            storeId = arguments.get("storeId").toString();
            storeLocation = arguments.get("storeLocation").toString();

            setViews(storeName, storeLocation);
        }
    }

    private void setViews(String storeName, String storeLocation) {
        binding.edtStore.setText(storeName);
        binding.edtLocation.setText(storeLocation);
        binding.tvTitle.setText("Edit " + storeName);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnEditStore) {

            if (validator.validateEditTextFields(getActivity(), binding.edtStore, "")
                    && validator.validateEditTextFields(getActivity(), binding.edtLocation, "")) {
                String store = binding.edtStore.getText().toString().trim();
                String location = binding.edtLocation.getText().toString().trim();

                Stores stores = new Stores();
                stores.setStore(store);
                stores.setLocation(location);
                stores.setStoreId(storeId);

                binding.tvNotify.setText("Updating store details");

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Stores");
                reference.child(storeId).setValue(stores).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        binding.edtStore.setText("");
                        binding.edtLocation.setText("");
                        binding.tvTitle.setText("Edit");
                        binding.tvNotify.setText("");
                        Navigation.findNavController(view).navigate(R.id.settingsFragment);
                    }
                }).addOnFailureListener(e -> customDialogs.showSnackBar(getActivity(), e.getMessage()));
            }

        }else if (view==binding.tvDeleteStore){
            binding.tvNotify.setText("Removing store details");
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Stores");
            reference.child(storeId).setValue(null).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    binding.edtStore.setText("");
                    binding.edtLocation.setText("");
                    binding.tvTitle.setText("Edit");
                    binding.tvNotify.setText("");
                    Navigation.findNavController(view).navigate(R.id.settingsFragment);
                }
            }).addOnFailureListener(e -> customDialogs.showSnackBar(getActivity(), e.getMessage()));
        }

    }

    private void getPreferenceData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Terminal", MODE_PRIVATE);
        terminal = sharedPreferences.getString("terminal", null);
    }
}