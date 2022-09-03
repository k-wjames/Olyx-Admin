package ke.co.ideagalore.olyxadmin.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.activities.Home;
import ke.co.ideagalore.olyxadmin.common.ValidateFields;
import ke.co.ideagalore.olyxadmin.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment implements View.OnClickListener {

    FragmentLoginBinding binding;
    ValidateFields validateFields = new ValidateFields();

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

        binding.btnLogin.setOnClickListener(this);
        binding.tvSignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnLogin) {
            loginUserWithEmailPassword();
        } else {
            Navigation.findNavController(view).navigate(R.id.signUpFragment);
        }
    }

    private void loginUserWithEmailPassword() {

        if (validateFields.validateEmailAddress(getActivity(), binding.edtEmail)
                && validateFields.validateEditTextFields(getActivity(), binding.edtPassword, "Password")) {

            String email = binding.edtEmail.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();

            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

                if (!task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(getActivity(), Home.class));
                getActivity().finish();

            });


        }
    }

}