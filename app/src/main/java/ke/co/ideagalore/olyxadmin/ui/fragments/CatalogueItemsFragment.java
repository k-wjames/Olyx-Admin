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
import android.widget.Spinner;
import android.widget.TextView;

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
import ke.co.ideagalore.olyxadmin.databinding.FragmentCatalogueItemsBinding;
import ke.co.ideagalore.olyxadmin.models.Catalogue;
import ke.co.ideagalore.olyxadmin.models.Refill;
import ke.co.ideagalore.olyxadmin.models.Stores;

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
        getCatalogueData();

        binding.ivBack.setOnClickListener(this);
        binding.tvViewRefillItems.setOnClickListener(this);
        binding.tvViewNewCylinders.setOnClickListener(this);
        binding.tvViewAccessories.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        Bundle bundle = new Bundle();

        if (view == binding.tvViewRefillItems) {

            bundle.putString("category", "Gas Refill");
            bundle.putString("title", "Gas Refill");
            Navigation.findNavController(view).navigate(R.id.viewCategoryProductsFragment, bundle);

        } else if (view == binding.tvViewNewCylinders) {
            bundle.putString("category", "New Gas");
            bundle.putString("title", "New Gas Cylinders");
            Navigation.findNavController(view).navigate(R.id.viewCategoryProductsFragment, bundle);

        } else if (view == binding.tvViewAccessories) {
            bundle.putString("category", "Accessories");
            bundle.putString("title", "Accessories");
            Navigation.findNavController(view).navigate(R.id.viewCategoryProductsFragment, bundle);

        } else {
            Navigation.findNavController(view).navigate(R.id.mainFragment);
        }

    }

    private void getCatalogueData() {
        catalogueList.clear();
        refillCatalogueItems.clear();
        newGasCylinderItems.clear();
        accessories.clear();
        customDialogs.showProgressDialog(getActivity(), "Fetching catalogue data");
        reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Catalogue");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot catalogueSnapshot : snapshot.getChildren()) {
                    Catalogue catalogue = catalogueSnapshot.getValue(Catalogue.class);
                    catalogueList.add(0, catalogue);
                    if (catalogueList.size()<10){
                        binding.tvAll.setText(0+""+catalogueList.size());
                    }else {
                        binding.tvAll.setText(String.valueOf(catalogueList.size()));
                    }
                    customDialogs.dismissProgressDialog();
                }

                for (int i = 0; i < catalogueList.size(); i++) {
                    Catalogue catalogueProduct = catalogueList.get(i);
                    if (catalogueProduct.getCategory().equals("Gas Refill")) {
                        refillCatalogueItems.add(0, catalogueProduct);
                        binding.tvRefillItems.setText(String.valueOf(refillCatalogueItems.size()));


                    } else if (catalogueProduct.getCategory().equals("New Gas")) {
                        newGasCylinderItems.add(0, catalogueProduct);
                        int length=newGasCylinderItems.size();
                        binding.tvGasItems.setText(String.valueOf(length));


                    } else {
                        accessories.add(0, catalogueProduct);
                        binding.tvAccessoriesItems.setText(String.valueOf(accessories.size()));
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
}