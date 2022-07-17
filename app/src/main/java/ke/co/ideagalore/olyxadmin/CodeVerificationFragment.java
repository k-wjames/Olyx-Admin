package ke.co.ideagalore.olyxadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ke.co.ideagalore.olyxadmin.databinding.FragmentCodeVerificationBinding;
import ke.co.ideagalore.olyxadmin.databinding.FragmentLoginBinding;

public class CodeVerificationFragment extends Fragment implements View.OnClickListener{

    FragmentCodeVerificationBinding binding;

    public CodeVerificationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCodeVerificationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        startActivity(new Intent(getActivity(), Home.class));
        getActivity().finish();

    }
}