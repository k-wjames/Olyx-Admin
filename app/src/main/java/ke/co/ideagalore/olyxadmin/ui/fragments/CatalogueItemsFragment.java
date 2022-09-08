package ke.co.ideagalore.olyxadmin.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.databinding.FragmentCatalogueItemsBinding;

public class CatalogueItemsFragment extends Fragment implements View.OnClickListener {

    FragmentCatalogueItemsBinding binding;

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
}