package ke.co.ideagalore.olyxadmin.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.common.CustomDialogs;
import ke.co.ideagalore.olyxadmin.common.ValidateFields;
import ke.co.ideagalore.olyxadmin.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    FragmentSettingsBinding binding;
    ValidateFields validator=new ValidateFields();

    public SettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.ivAddStore.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.ivAddStore) {
            showAddStoreDialog();

        } else {

        }

    }

    public void showAddStoreDialog() {
        Dialog myDialog = new Dialog(getActivity());
        myDialog.setContentView(R.layout.add_shop_dialog);
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        myDialog.show();
        TextView cancel = myDialog.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(view -> myDialog.dismiss());

        EditText edtStore = myDialog.findViewById(R.id.edt_store);
        EditText edtLocation = myDialog.findViewById(R.id.edt_location);

        String store=edtStore.getText().toString().trim();
        String location=edtLocation.getText().toString().trim();

        Button add = myDialog.findViewById(R.id.btn_add_store);
        add.setOnClickListener(view -> {

            if (validator.validateEditTextFields(getActivity(), edtStore, "Store name") &&
                    validator.validateEditTextFields(getActivity(), edtLocation, "Store location")) {
                addNewStore(store, location);

            }

        });
    }

    private void addNewStore(String store, String location) {

    }
}