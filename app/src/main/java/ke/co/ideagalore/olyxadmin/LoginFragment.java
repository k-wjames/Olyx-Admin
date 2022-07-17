package ke.co.ideagalore.olyxadmin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ke.co.ideagalore.olyxadmin.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment implements View.OnClickListener{

    FragmentLoginBinding binding;

    public LoginFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        /*Bundle bundle = new Bundle();
        bundle.putString("phone", binding.edtPhone.getText().toString());
        bundle.putString("access", binding.edtCode.getText().toString());
        Navigation.findNavController(view).navigate(R.id.codeVerificationFragment, bundle);*/
        startActivity(new Intent(getActivity(),Home.class));
    }


}