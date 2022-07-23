package ke.co.ideagalore.olyxadmin;

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

import ke.co.ideagalore.olyxadmin.adapters.CatalogueAdapter;
import ke.co.ideagalore.olyxadmin.databinding.FragmentCatalogueBinding;
import ke.co.ideagalore.olyxadmin.models.Catalogue;
import ke.co.ideagalore.olyxadmin.models.Product;

public class CatalogueFragment extends Fragment implements View.OnClickListener {

    FragmentCatalogueBinding binding;

    EditText edtProduct, edtDescription, edtQuantity, edtBuyingPrice, edtSellingPrice;
    Dialog dialog;
    ProgressBar progressBar;

    String selectedCategory;

    ArrayList<Catalogue> catalogueArrayList;

    public CatalogueFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCatalogueBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        catalogueArrayList = new ArrayList<>();

        binding.fabCatalogue.setOnClickListener(this);

        getCatalogueData();
    }

    @Override
    public void onClick(View view) {

        if (view == binding.fabCatalogue) {
            showAddCatalogueItemDialog();
        }

    }

    private void getCatalogueData() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Catalogue");
        binding.progressBar.setVisibility(View.VISIBLE);
        catalogueArrayList.clear();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot catalogueSnapshot : snapshot.getChildren()) {
                    Catalogue catalogue = catalogueSnapshot.getValue(Catalogue.class);
                    catalogueArrayList.add(catalogue);

                    if (catalogueArrayList.size() < 1) {

                        binding.animationView.setVisibility(View.VISIBLE);
                    }

                    binding.progressBar.setVisibility(View.GONE);
                }

                binding.rvCatalogue.setLayoutManager(new LinearLayoutManager(getActivity()));
                binding.rvCatalogue.setHasFixedSize(true);
                CatalogueAdapter adapter = new CatalogueAdapter(getActivity(), catalogueArrayList);
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
        dialog.setContentView(R.layout.add_catalogue_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        progressBar = dialog.findViewById(R.id.progress_bar);

        edtProduct = dialog.findViewById(R.id.edt_item);
        edtDescription = dialog.findViewById(R.id.edt_description);
        edtQuantity = dialog.findViewById(R.id.edt_quantity);
        edtBuyingPrice = dialog.findViewById(R.id.edt_buying_price);
        edtSellingPrice = dialog.findViewById(R.id.edt_selling_price);

        Spinner spinner = dialog.findViewById(R.id.spinner_category);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.stockItems, R.layout.spinner_item);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategory = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner.setAdapter(adapter);

        TextView cancel = dialog.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(view -> dialog.dismiss());

        Button save = dialog.findViewById(R.id.btn_add_catalogue);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectedCategory.equals("Select category")) {
                    addNewCatalogueItem();
                }else {
                    Toast.makeText(getActivity(), "Please select product category", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void addNewCatalogueItem() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Catalogue");
        String id = reference.push().getKey();

        if (validateEditTextFields(edtProduct) && validateEditTextFields(edtDescription)
                && validateEditTextFields(edtQuantity) && validateEditTextFields(edtBuyingPrice)
                && validateEditTextFields(edtSellingPrice)) {

            String item = edtProduct.getText().toString();
            String description = edtDescription.getText().toString();
            int quantity = Integer.parseInt(edtQuantity.getText().toString());
            int buying = Integer.parseInt(edtBuyingPrice.getText().toString());
            int selling = Integer.parseInt(edtSellingPrice.getText().toString());

            Catalogue catalogue = new Catalogue();
            catalogue.setProdId(id);
            catalogue.setProduct(item);
            catalogue.setDescription(description);
            catalogue.setQuantity(quantity);
            catalogue.setBuyingPrice(buying);
            catalogue.setSellingPrice(selling);
            catalogue.setCategory(selectedCategory);

            progressBar.setVisibility(View.VISIBLE);


            reference.child(id).setValue(catalogue).addOnCompleteListener(new OnCompleteListener<Void>() {
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


    private void addStock() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Stock");
        String id = reference.push().getKey();

        Product product = new Product();
        product.setName("Gas");
        product.setProdId(id);
        product.setBrand("OilCom");
        product.setCapacity(6);
        product.setShop("Mwimuto");
        product.setQuantity(17);

        reference.child(id).setValue(product).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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