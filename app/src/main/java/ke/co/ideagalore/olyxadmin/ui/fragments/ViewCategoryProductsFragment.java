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
import ke.co.ideagalore.olyxadmin.common.CustomDialogs;
import ke.co.ideagalore.olyxadmin.common.ValidateFields;
import ke.co.ideagalore.olyxadmin.databinding.FragmentViewCategoryProductsBinding;
import ke.co.ideagalore.olyxadmin.models.Catalogue;

public class ViewCategoryProductsFragment extends Fragment implements View.OnClickListener {

    String title, category, terminal;

    FragmentViewCategoryProductsBinding binding;

    CustomDialogs customDialogs = new CustomDialogs();
    ValidateFields validator = new ValidateFields();

    List<Catalogue> catalogueList = new ArrayList<>();
    List<Catalogue> categoryList = new ArrayList<>();

    DatabaseReference reference;

    public ViewCategoryProductsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentViewCategoryProductsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPreferenceData();
        getBundleData();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Catalogue");
        getCategoryData();

        binding.ivBack.setOnClickListener(this);
        binding.ivAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view == binding.ivBack) {
            Navigation.findNavController(view).navigate(R.id.catalogueItemsFragment);
        } else {
            showAddNewCategoryItem();
        }

    }

    private void getBundleData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            category = arguments.get("category").toString();
            title = arguments.get("title").toString();
            binding.title.setText(title);
        }
    }

    private void getCategoryData() {
        catalogueList.clear();
        categoryList.clear();
        customDialogs.showProgressDialog(getActivity(), "Fetching category data");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot catalogueSnapshot : snapshot.getChildren()) {
                    Catalogue catalogue = catalogueSnapshot.getValue(Catalogue.class);
                    catalogueList.add(0, catalogue);
                    customDialogs.dismissProgressDialog();
                }

                for (int i = 0; i < catalogueList.size(); i++) {
                    Catalogue catalogueProduct = catalogueList.get(i);
                    if (catalogueProduct.getCategory().equals(category)) {
                        categoryList.add(0, catalogueProduct);
                        binding.tvTotalItems.setText(String.valueOf(categoryList.size()));
                        CatalogueAdapter adapter = new CatalogueAdapter(getActivity(), categoryList);
                        binding.rvProducts.setLayoutManager(new LinearLayoutManager(getActivity()));
                        binding.rvProducts.setHasFixedSize(true);
                        binding.rvProducts.setAdapter(adapter);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                customDialogs.showSnackBar(getActivity(), error.getMessage());
            }
        });
    }


    private void showAddNewCategoryItem() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.set_refill_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        EditText productCategory=dialog.findViewById(R.id.tv_category);
        EditText productName = dialog.findViewById(R.id.edt_item);
        EditText buyingPrice = dialog.findViewById(R.id.edt_buying_price);
        EditText markedPrice = dialog.findViewById(R.id.edt_selling_price);
        EditText stokedItems=dialog.findViewById(R.id.edt_stoked_items);

        productCategory.setText(category);

        ProgressBar progressBar =dialog.findViewById(R.id.progress_bar);

        TextView cancel = dialog.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(view -> dialog.dismiss());

        Button add=dialog.findViewById(R.id.btn_add);
        add.setOnClickListener(view -> {
            if (validator.validateEditTextFields(getActivity(), productCategory, "Product category")
            &&validator.validateEditTextFields(getActivity(),productName,"Product")
            &&validator.validateEditTextFields(getActivity(),buyingPrice, "Buying price")
            &&validator.validateEditTextFields(getActivity(), markedPrice, "Marked price")
            &&validator.validateEditTextFields(getActivity(), stokedItems,"Items stoked")){
                String category=productCategory.getText().toString();
                String product=productName.getText().toString();
                int buying=Integer.parseInt(buyingPrice.getText().toString());
                int marked=Integer.parseInt(markedPrice.getText().toString());
                int stock=Integer.parseInt(stokedItems.getText().toString());
                progressBar.setVisibility(View.VISIBLE);
                saveNewCategoryItem(category,product,buying,marked,stock, dialog);
            }
        });
    }

    private void saveNewCategoryItem(String category, String product, int buying, int marked, int stock, Dialog dialog) {

        String prodId=reference.push().getKey();
        Catalogue catalogue=new Catalogue();
        catalogue.setProdId(prodId);
        catalogue.setCategory(category);
        catalogue.setProduct(product);
        catalogue.setBuyingPrice(buying);
        catalogue.setMarkedPrice(marked);
        catalogue.setStockedQuantity(stock);
        reference.child(prodId).setValue(catalogue).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    dialog.dismiss();
                    customDialogs.showSnackBar(getActivity(), "New "+product+" successfully added to your catalogue");
                    getCategoryData();
                }

            }
        }).addOnFailureListener(e -> {
            customDialogs.showSnackBar(getActivity(), e.getMessage());
        });

    }

    public void getPreferenceData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Terminal", MODE_PRIVATE);
        terminal = sharedPreferences.getString("terminal", null);

    }

}