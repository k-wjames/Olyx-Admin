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

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.adapters.RefillAdapter;
import ke.co.ideagalore.olyxadmin.common.CustomDialogs;
import ke.co.ideagalore.olyxadmin.common.ValidateFields;
import ke.co.ideagalore.olyxadmin.databinding.FragmentAccessoriesBinding;
import ke.co.ideagalore.olyxadmin.models.Product;
import ke.co.ideagalore.olyxadmin.models.Refill;

public class AccessoriesFragment extends Fragment implements View.OnClickListener {

    FragmentAccessoriesBinding binding;
    CustomDialogs customDialogs=new CustomDialogs();
    ValidateFields validator=new ValidateFields();
    List<Refill>accessoriesList=new ArrayList<>();

    String name, business, terminal;

    DatabaseReference reference;

    ProgressBar progressBar;

    public AccessoriesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccessoriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getPreferenceData();

        reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Accessories");
        getAccessoriesItems();

        binding.ivBack.setOnClickListener(this);
        binding.ivAdd.setOnClickListener(this);
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
        TextView title=dialog.findViewById(R.id.tv_title);
        title.setText("Add New Accessory");
        dialog.show();

        EditText productName = dialog.findViewById(R.id.edt_item);
        EditText buyingPrice = dialog.findViewById(R.id.edt_buying_price);
        EditText markedPrice = dialog.findViewById(R.id.edt_selling_price);

        progressBar=dialog.findViewById(R.id.progress_bar);

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

                saveNewGasRefillItem(item, bPrice, mPrice, dialog);
            }
        });
    }

    private void saveNewGasRefillItem(String item, int bPrice, int mPrice, Dialog dialog) {

        String itemId = reference.push().getKey();

        Refill refill = new Refill();
        refill.setProdId(itemId);
        refill.setBuyingPrice(bPrice);
        refill.setMarkedPrice(mPrice);
        refill.setProduct(item);

        //customDialogs.showProgressDialog(getActivity(), "Adding accessory...");
        progressBar.setVisibility(View.VISIBLE);
        reference.child(itemId).setValue(refill).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                //customDialogs.dismissProgressDialog();
                dialog.dismiss();
                customDialogs.showSnackBar(getActivity(), "Accessory successfully added.");
                getAccessoriesItems();
            }

        }).addOnFailureListener(e -> customDialogs.showSnackBar(requireActivity(), e.getMessage()));

    }

    public void getPreferenceData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Terminal", MODE_PRIVATE);
        business = sharedPreferences.getString("business", null);
        terminal = sharedPreferences.getString("terminal", null);
        name = sharedPreferences.getString("name", null);

    }

    private void getAccessoriesItems() {

        customDialogs.showProgressDialog(getActivity(),"Fetching data");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                accessoriesList.clear();

                for (DataSnapshot refillSnapshot : snapshot.getChildren()) {

                    Refill refill = refillSnapshot.getValue(Refill.class);
                    accessoriesList.add(0, refill);

                }

                RefillAdapter adapter = new RefillAdapter(accessoriesList);
                binding.rvAccessories.setLayoutManager(new LinearLayoutManager(getActivity()));
                binding.rvAccessories.setHasFixedSize(true);
                customDialogs.dismissProgressDialog();
                binding.rvAccessories.setAdapter(adapter);
                binding.tvTotalItems.setText(accessoriesList.size()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}