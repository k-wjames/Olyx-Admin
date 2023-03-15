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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import ke.co.ideagalore.olyxadmin.databinding.FragmentCatalogueItemsBinding;
import ke.co.ideagalore.olyxadmin.models.Catalogue;

public class CatalogueItemsFragment extends Fragment implements View.OnClickListener {

    FragmentCatalogueItemsBinding binding;

    DatabaseReference reference;

    List<Catalogue> refillCatalogueItems = new ArrayList<>();
    List<Catalogue> newGasCylinderItems = new ArrayList<>();
    List<Catalogue> accessories = new ArrayList<>();
    List<Catalogue> catalogueList = new ArrayList<>();

    String name, business, terminal;
    CustomDialogs customDialogs = new CustomDialogs();

    public CatalogueItemsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCatalogueItemsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getPreferenceData();

        reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Catalogue");

        getCatalogueData();

        binding.tvAll.setOnClickListener(this);
        binding.tvRefill.setOnClickListener(this);
        binding.tvNewGas.setOnClickListener(this);
        binding.tvAccessories.setOnClickListener(this);
        binding.ivAdd.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        if (view == binding.tvAll) {

            binding.viewAll.setVisibility(View.VISIBLE);
            binding.viewRefill.setVisibility(View.GONE);
            binding.viewNewGas.setVisibility(View.GONE);
            binding.viewAccessories.setVisibility(View.GONE);

            displayCatalogueList(catalogueList);

        } else if (view == binding.tvRefill) {

            binding.viewAll.setVisibility(View.GONE);
            binding.viewRefill.setVisibility(View.VISIBLE);
            binding.viewNewGas.setVisibility(View.GONE);
            binding.viewAccessories.setVisibility(View.GONE);
            displayCatalogueList(refillCatalogueItems);

        } else if (view == binding.tvNewGas) {

            binding.viewAll.setVisibility(View.GONE);
            binding.viewRefill.setVisibility(View.GONE);
            binding.viewNewGas.setVisibility(View.VISIBLE);
            binding.viewAccessories.setVisibility(View.GONE);
            displayCatalogueList(newGasCylinderItems);

        } else if (view == binding.tvAccessories) {

            binding.viewAll.setVisibility(View.GONE);
            binding.viewRefill.setVisibility(View.GONE);
            binding.viewNewGas.setVisibility(View.GONE);
            binding.viewAccessories.setVisibility(View.VISIBLE);
            displayCatalogueList(accessories);

        }else if (view==binding.ivAdd){
            Navigation.findNavController(view).navigate(R.id.addCatalogueFragment);
        }

    }

    private void getCatalogueData() {
        catalogueList.clear();
        refillCatalogueItems.clear();
        newGasCylinderItems.clear();
        accessories.clear();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot catalogueSnapshot : snapshot.getChildren()) {
                    Catalogue catalogue = catalogueSnapshot.getValue(Catalogue.class);
                    catalogueList.add(0, catalogue);
                    displayCatalogueList(catalogueList);
                }

                for (int i = 0; i < catalogueList.size(); i++) {
                    Catalogue catalogueProduct = catalogueList.get(i);
                    if (catalogueProduct.getCategory().equals("Gas Refill")) {
                        refillCatalogueItems.add(0, catalogueProduct);

                    } else if (catalogueProduct.getCategory().equals("New Gas")) {
                        newGasCylinderItems.add(0, catalogueProduct);


                    } else {
                        accessories.add(0, catalogueProduct);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                customDialogs.showSnackBar(getActivity(), error.getMessage());
            }
        });
    }

    public void getPreferenceData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Terminal", MODE_PRIVATE);
        business = sharedPreferences.getString("business", null);
        terminal = sharedPreferences.getString("terminal", null);
        name = sharedPreferences.getString("name", null);

    }

    private void displayCatalogueList(List<Catalogue> catalogues) {
        if (catalogues.size() < 10) {
            binding.tvFound.setText(0 + "" + catalogues.size());
        } else {
            binding.tvFound.setText(String.valueOf(catalogues.size()));
        }
        binding.rvCatalogue.setLayoutManager(new GridLayoutManager(getActivity(),1));
        binding.rvCatalogue.setHasFixedSize(true);
        CatalogueAdapter adapter = new CatalogueAdapter(catalogues, item -> {

            showActivityDialog(item);

        });
        binding.rvCatalogue.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void showActivityDialog(Catalogue item) {
        Dialog myDialog = new Dialog(requireActivity());
        myDialog.setContentView(R.layout.update_catalogue_dialog);
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        RelativeLayout rlUpdateCatalogue=myDialog.findViewById(R.id.rl_update_item);
        RelativeLayout rlRestockCatalogue=myDialog.findViewById(R.id.rl_restock_item);
        RelativeLayout rlDeleteCatalogue=myDialog.findViewById(R.id.rl_delete_item);

        rlUpdateCatalogue.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("productId", item.getProdId());
            bundle.putString("product", item.getProduct());
            bundle.putString("category", item.getCategory());
            bundle.putInt("stockedItems", item.getStockedQuantity());
            bundle.putInt("buyingPrice", item.getBuyingPrice());
            bundle.putInt("sellingPrice", item.getMarkedPrice());
            bundle.putString("shop", item.getShop());
            myDialog.dismiss();
            Navigation.findNavController(CatalogueItemsFragment.this.requireView()).navigate(R.id.editProductFragment, bundle);
        });

        rlRestockCatalogue.setOnClickListener(view -> myDialog.dismiss());

        rlDeleteCatalogue.setOnClickListener(view -> deleteProduct(item.getProdId(), view));

        myDialog.show();
    }

    private void deleteProduct(String productId, View view) {
        reference.child(productId).setValue(null).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                customDialogs.showSnackBar(requireActivity(), "Item successfully deleted.");
                Navigation.findNavController(view).navigate(R.id.catalogueItemsFragment);
            }

        }).addOnFailureListener(e -> {
            customDialogs.showSnackBar(requireActivity(), e.getMessage());
        });
    }


}