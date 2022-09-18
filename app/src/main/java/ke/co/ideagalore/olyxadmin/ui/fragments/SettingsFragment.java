package ke.co.ideagalore.olyxadmin.ui.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.adapters.AttendantsAdapter;
import ke.co.ideagalore.olyxadmin.adapters.StoreAdapter;
import ke.co.ideagalore.olyxadmin.common.CustomDialogs;
import ke.co.ideagalore.olyxadmin.common.ValidateFields;
import ke.co.ideagalore.olyxadmin.databinding.FragmentSettingsBinding;
import ke.co.ideagalore.olyxadmin.models.Attendant;
import ke.co.ideagalore.olyxadmin.models.Stores;
import ke.co.ideagalore.olyxadmin.models.Terminal;
import ke.co.ideagalore.olyxadmin.ui.activities.Onboard;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    FragmentSettingsBinding binding;
    ValidateFields validator = new ValidateFields();
    CustomDialogs customDialogs = new CustomDialogs();
    String business, terminal, name;
    Dialog myDialog;
    List<Stores> storesList = new ArrayList<>();
    List<Attendant> attendantList = new ArrayList<>();

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

        getPreferenceData();
        getStoresData();
        getAttendantsData();


        binding.ivExpand.setOnClickListener(this);
        binding.ivAddStore.setOnClickListener(this);
        binding.ivMinimise.setOnClickListener(this);
        binding.ivExpandAttendants.setOnClickListener(this);
        binding.ivMinimiseAttendants.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);
        binding.ivCopy.setOnClickListener(this);
        binding.ivSignOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.ivAddStore) {
            showAddStoreDialog();
        } else if (view == binding.ivExpand) {
            binding.ivExpand.setVisibility(View.GONE);
            binding.ivMinimise.setVisibility(View.VISIBLE);
            binding.ivAddStore.setVisibility(View.VISIBLE);
            binding.rvStores.setVisibility(View.VISIBLE);
        } else if (view == binding.ivMinimise) {
            binding.ivExpand.setVisibility(View.VISIBLE);
            binding.ivMinimise.setVisibility(View.GONE);
            binding.ivAddStore.setVisibility(View.GONE);
            binding.rvStores.setVisibility(View.GONE);
        } else if (view == binding.ivExpandAttendants) {
            binding.ivExpandAttendants.setVisibility(View.GONE);
            binding.ivMinimiseAttendants.setVisibility(View.VISIBLE);
            binding.rvAttendants.setVisibility(View.VISIBLE);
        } else if (view == binding.ivMinimiseAttendants) {
            binding.ivMinimiseAttendants.setVisibility(View.GONE);
            binding.ivExpandAttendants.setVisibility(View.VISIBLE);
            binding.rvAttendants.setVisibility(View.GONE);
        } else if (view == binding.ivCopy) {
            String id = binding.tvTerminal.getText().toString();
            if (!id.isEmpty()) {
                ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("data", id);
                clipboardManager.setPrimaryClip(clipData);
                customDialogs.showSnackBar(getActivity(), id + " copied");

            }

        } else if (view == binding.ivSignOut) {

            signOut();

        } else {
            Navigation.findNavController(view).navigate(R.id.mainFragment);
        }
    }

    private void signOut() {

        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.sign_out_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        TextView cancel = dialog.findViewById(R.id.tv_cancel);
        TextView logout = dialog.findViewById(R.id.tv_logout);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        cancel.setOnClickListener(view -> dialog.dismiss());
        logout.setOnClickListener(view -> {
            auth.signOut();
            startActivity(new Intent(getActivity(), Onboard.class));
            getActivity().finish();
        });

    }

    public void showAddStoreDialog() {
        myDialog = new Dialog(getActivity());
        myDialog.setContentView(R.layout.add_shop_dialog);
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        myDialog.show();
        TextView cancel = myDialog.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(view -> myDialog.dismiss());

        ProgressBar progressBar = myDialog.findViewById(R.id.progress_bar);

        EditText edtStore = myDialog.findViewById(R.id.edt_store);
        EditText edtLocation = myDialog.findViewById(R.id.edt_location);

        Button add = myDialog.findViewById(R.id.btn_add_store);
        add.setOnClickListener(view -> {

            if (validator.validateEditTextFields(getActivity(), edtStore, "Store name") &&
                    validator.validateEditTextFields(getActivity(), edtLocation, "Store location")) {
                progressBar.setVisibility(View.VISIBLE);
                addNewStore(edtStore.getText().toString().trim(), edtLocation.getText().toString().trim());

            }

        });
    }

    private void addNewStore(String store, String location) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Stores");
        String shopId = reference.push().getKey();
        Stores myStore = new Stores();
        myStore.setStore(store);
        myStore.setLocation(location);
        myStore.setStoreId(shopId);
        Terminal myTerminal = new Terminal();
        myTerminal.setBusiness(business);
        myTerminal.setProprietor(name);
        myTerminal.setStore(myStore);
        reference.child(shopId).setValue(myStore).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                getStoresData();
                myDialog.dismiss();
            }

        });

    }

    private void getPreferenceData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Terminal", MODE_PRIVATE);
        business = sharedPreferences.getString("business", null);
        terminal = sharedPreferences.getString("terminal", null);
        name = sharedPreferences.getString("name", null);
        binding.tvBusiness.setText(business);
        binding.tvProprietor.setText(name);
        binding.tvTerminal.setText(terminal);

    }

    private void getStoresData() {
        storesList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Stores");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot storeSnapshot : snapshot.getChildren()) {
                    Stores store = storeSnapshot.getValue(Stores.class);
                    storesList.add(store);

                    if (storesList.size() >= 1) {

                        StoreAdapter adapter = new StoreAdapter(storesList);
                        binding.rvStores.setLayoutManager(new LinearLayoutManager(getActivity()));
                        binding.rvStores.setHasFixedSize(true);
                        binding.rvStores.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    } else {
                        return;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAttendantsData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Attendants");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot attendantsSnapshot : snapshot.getChildren()) {
                    Attendant attendant = attendantsSnapshot.getValue(Attendant.class);
                    attendantList.add(attendant);
                }
                AttendantsAdapter adapter = new AttendantsAdapter(attendantList);
                binding.rvAttendants.setLayoutManager(new LinearLayoutManager(getActivity()));
                binding.rvAttendants.setHasFixedSize(true);
                binding.rvAttendants.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}