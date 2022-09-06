package ke.co.ideagalore.olyxadmin.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.databinding.FragmentSalesBinding;

public class SalesFragment extends Fragment implements View.OnClickListener{

    FragmentSalesBinding binding;

    public SalesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding=FragmentSalesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view==binding.ivBack){
            Navigation.findNavController(view).navigate(R.id.homeFragment);
        }

    }
}