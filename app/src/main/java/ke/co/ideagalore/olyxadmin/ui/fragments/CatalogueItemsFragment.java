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
import ke.co.ideagalore.olyxadmin.models.Refill;

public class CatalogueItemsFragment extends Fragment implements View.OnClickListener {

    FragmentCatalogueItemsBinding binding;

    DatabaseReference reference;

    List<Refill> accessoriesList = new ArrayList<>();
    List<Refill> refillList = new ArrayList<>();
    List<Refill> newGasList = new ArrayList<>();

    String name, business, terminal;

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

        } else {
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

                }
                binding.progressBarRefill.setVisibility(View.GONE);
                binding.tvRefillItems.setText(refillList.size() + "");
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

                }

                binding.progressBarAccessories.setVisibility(View.GONE);
                binding.tvAccessoriesItems.setText(accessoriesList.size() + "");
                binding.tvAccessoriesItems.setVisibility(View.VISIBLE);
                binding.tvAccessories.setVisibility(View.VISIBLE);
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

                }

                binding.progressBarGas.setVisibility(View.GONE);
                binding.tvGasItems.setText(newGasList.size() + "");
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
}