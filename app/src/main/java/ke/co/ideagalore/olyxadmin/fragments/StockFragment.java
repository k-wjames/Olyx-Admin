package ke.co.ideagalore.olyxadmin.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.List;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.adapters.CatalogueAdapter;
import ke.co.ideagalore.olyxadmin.adapters.RefillAdapter;
import ke.co.ideagalore.olyxadmin.databinding.FragmentStockBinding;
import ke.co.ideagalore.olyxadmin.models.Catalogue;
import ke.co.ideagalore.olyxadmin.models.Refill;

public class StockFragment extends Fragment implements View.OnClickListener {

    FragmentStockBinding binding;
    EditText edtProduct, edtBuyingPrice, edtSellingPrice;
    Dialog dialog;
    ProgressBar progressBar;

   List<Refill> catalogueRefillList;

    public StockFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentStockBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        catalogueRefillList = new ArrayList<>();

        binding.fabCatalogue.setOnClickListener(this);

      getCatalogueData();

        binding.ivBack.setOnClickListener(this);
        binding.fabCatalogue.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.ivBack) {
            Navigation.findNavController(view).navigate(R.id.homeFragment);
        } else if (view == binding.fabCatalogue) {
            showAddCatalogueItemDialog();
        }
    }

    private void getCatalogueData() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Refill");
        binding.progressBar.setVisibility(View.VISIBLE);
        catalogueRefillList.clear();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot refillSnapshot : snapshot.getChildren()) {
                    Refill refill = refillSnapshot.getValue(Refill.class);
                    catalogueRefillList.add(refill);

                    if (catalogueRefillList.size() < 1) {

                        binding.animationView.setVisibility(View.VISIBLE);
                    }

                    binding.progressBar.setVisibility(View.GONE);
                }

                binding.rvCatalogue.setLayoutManager(new LinearLayoutManager(getActivity()));
                binding.rvCatalogue.setHasFixedSize(true);
                RefillAdapter adapter = new RefillAdapter(getActivity(), catalogueRefillList);
                binding.rvCatalogue.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getActivity(), "Oops! Something went wrong. Be sure it's not you.", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showAddCatalogueItemDialog() {

        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.set_refill_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        progressBar = dialog.findViewById(R.id.progress_bar);

        edtProduct = dialog.findViewById(R.id.edt_item);
        edtBuyingPrice = dialog.findViewById(R.id.edt_buying_price);
        edtSellingPrice = dialog.findViewById(R.id.edt_selling_price);

        TextView cancel = dialog.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(view -> dialog.dismiss());

        Button save = dialog.findViewById(R.id.btn_add_catalogue);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGasRefillPrice();
            }
        });

    }


    private void setGasRefillPrice() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Refill");
        String id = reference.push().getKey();

        if (validateEditTextFields(edtProduct) && validateEditTextFields(edtBuyingPrice)
                && validateEditTextFields(edtSellingPrice)) {

            String item = edtProduct.getText().toString();
            int buying = Integer.parseInt(edtBuyingPrice.getText().toString());
            int selling = Integer.parseInt(edtSellingPrice.getText().toString());

            Refill refill = new Refill();
            refill.setProdId(id);
            refill.setProduct(item);
            refill.setBuyingPrice(buying);
            refill.setMarkedPrice(selling);

            progressBar.setVisibility(View.VISIBLE);


            reference.child(id).setValue(refill).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        getCatalogueData();
                        dialog.dismiss();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getActivity(), "Oops! Operation not successful. Try again", Toast.LENGTH_SHORT).show();

                }
            });

        }

    }

    private boolean validateEditTextFields(EditText editText) {
        String input = editText.getText().toString();
        if (!input.isEmpty()) {
            return true;
        } else {
            editText.setError("Field cannot be empty");
            return false;
        }
    }

}