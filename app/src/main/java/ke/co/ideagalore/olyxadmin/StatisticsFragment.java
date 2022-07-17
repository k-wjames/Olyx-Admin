package ke.co.ideagalore.olyxadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import ke.co.ideagalore.olyxadmin.databinding.FragmentStatisticsBinding;

public class StatisticsFragment extends Fragment implements View.OnClickListener {

    FragmentStatisticsBinding binding;

    public StatisticsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view == binding.ivBack) {
            Navigation.findNavController(view).navigate(R.id.homeFragment);
        }
    }
}