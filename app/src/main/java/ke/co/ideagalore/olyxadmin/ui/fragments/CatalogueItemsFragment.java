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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.adapters.StoreAdapter;
import ke.co.ideagalore.olyxadmin.databinding.FragmentCatalogueItemsBinding;
import ke.co.ideagalore.olyxadmin.models.Refill;
import ke.co.ideagalore.olyxadmin.models.Stores;

public class CatalogueItemsFragment extends Fragment implements View.OnClickListener {

    FragmentCatalogueItemsBinding binding;

    DatabaseReference reference;

    List<Refill> accessoriesList = new ArrayList<>();
    List<Refill> refillList = new ArrayList<>();
    List<Refill> newGasList = new ArrayList<>();

    int refillItems, newGasItems, accessoriesItems, allItems;

    List<String> storesList=new ArrayList<>();
    List<String> productsList=new ArrayList<>();

    String name, business, terminal;

    String selectedStore,selectedCategory, selectedProduct,myStore, myProduct, myCategory;

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
        getGasRefillItems();
        getNewGasData();
        getAccessoriesItems();

        binding.ivBack.setOnClickListener(this);
        binding.tvViewRefillItems.setOnClickListener(this);
        binding.tvViewNewCylinders.setOnClickListener(this);
        binding.tvViewAccessories.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view == binding.tvViewRefillItems) {

            Navigation.findNavController(view).navigate(R.id.gasRefillItemsFragment);

        } else if (view == binding.tvViewNewCylinders) {

            Navigation.findNavController(view).navigate(R.id.newGasCylindersFragment);

        } else if (view == binding.tvViewAccessories) {

            Navigation.findNavController(view).navigate(R.id.accessoriesFragment);

        }  else {
            Navigation.findNavController(view).navigate(R.id.mainFragment);
        }

    }

    private void getGasRefillItems() {

        reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("GasRefill");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                refillList.clear();

                for (DataSnapshot refillSnapshot : snapshot.getChildren()) {

                    Refill refill = refillSnapshot.getValue(Refill.class);
                    refillList.add(0, refill);
                    refillItems=refillList.size();


                }
                binding.progressBarRefill.setVisibility(View.GONE);
                binding.tvRefillItems.setText(refillItems + "");
                binding.tvRefillItems.setVisibility(View.VISIBLE);
                binding.tvRefill.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getAccessoriesItems() {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Accessories");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                accessoriesList.clear();

                for (DataSnapshot refillSnapshot : snapshot.getChildren()) {

                    Refill refill = refillSnapshot.getValue(Refill.class);
                    accessoriesList.add(0, refill);
                    accessoriesItems=accessoriesList.size();

                }

                binding.progressBarAccessories.setVisibility(View.GONE);
                binding.tvAccessoriesItems.setText(accessoriesItems + "");
                binding.tvAccessoriesItems.setVisibility(View.VISIBLE);
                binding.tvAccessories.setVisibility(View.VISIBLE);

                allItems=refillItems+newGasItems+accessoriesItems;
                binding.tvAll.setText(String.valueOf(allItems));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getNewGasData() {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("NewGas");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                newGasList.clear();

                for (DataSnapshot refillSnapshot : snapshot.getChildren()) {

                    Refill refill = refillSnapshot.getValue(Refill.class);
                    newGasList.add(0, refill);
                    newGasItems=newGasList.size();

                }

                binding.progressBarGas.setVisibility(View.GONE);
                binding.tvGasItems.setText(newGasItems + "");
                binding.tvGasItems.setVisibility(View.VISIBLE);
                binding.tvGas.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getPreferenceData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Terminal", MODE_PRIVATE);
        business = sharedPreferences.getString("business", null);
        terminal = sharedPreferences.getString("terminal", null);
        name = sharedPreferences.getString("name", null);

    }


    private void showAddStockDialog() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.add_stock_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        Spinner spinnerShops = dialog.findViewById(R.id.spinner_shop);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, storesList);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerShops.setAdapter(arrayAdapter);
        spinnerShops.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                myStore=spinnerShops.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerShops.setAdapter(arrayAdapter);

        Spinner spinnerProducts = dialog.findViewById(R.id.spinner_product);

        ArrayAdapter<String> productsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, productsList);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerProducts.setAdapter(arrayAdapter);
        spinnerProducts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                myProduct=spinnerProducts.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerProducts.setAdapter(productsAdapter);

        TextView cancel=dialog.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(view -> dialog.dismiss());
    }

    private void getStoresData() {
        storesList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Stores");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot storeSnapshot : snapshot.getChildren()) {
                    Stores store = storeSnapshot.getValue(Stores.class);
                    selectedStore=store.getStore();
                    storesList.add(selectedStore);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getProductsData() {
    }



}