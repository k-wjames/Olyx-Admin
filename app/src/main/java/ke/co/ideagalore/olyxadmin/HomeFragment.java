package ke.co.ideagalore.olyxadmin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ke.co.ideagalore.olyxadmin.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment implements View.OnClickListener{
    FragmentHomeBinding binding;
    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding=FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.cvSales.setOnClickListener(this);
        binding.cvStatistics.setOnClickListener(this);
        binding.cvShops.setOnClickListener(this);
        binding.cvStaff.setOnClickListener(this);
        binding.cvStock.setOnClickListener(this);
        binding.cvAccessories.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view==binding.cvSales){
            Navigation.findNavController(view).navigate(R.id.salesFragment);
        }

    }
}