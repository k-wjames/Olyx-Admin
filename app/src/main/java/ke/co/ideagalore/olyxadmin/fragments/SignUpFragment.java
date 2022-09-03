package ke.co.ideagalore.olyxadmin.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.common.ValidateFields;
import ke.co.ideagalore.olyxadmin.databinding.FragmentSignUpBinding;
import ke.co.ideagalore.olyxadmin.models.User;

public class SignUpFragment extends Fragment implements View.OnClickListener {
    FragmentSignUpBinding binding;
    ValidateFields validator = new ValidateFields();

    public SignUpFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnSignup.setOnClickListener(this);
        binding.tvLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view == binding.btnSignup) {
            createUserWithEmailPassword();
        } else Navigation.findNavController(view).navigate(R.id.loginFragment);

    }

    private void createUserWithEmailPassword() {
        if (validator.validateEditTextFields(getActivity(), binding.edtUsername, "Name")
                && validator.validateEmailAddress(getActivity(), binding.edtEmail)
                && validator.validateEditTextFields(getActivity(), binding.edtPassword, "Password")
                && validator.validateEditTextFields(getActivity(), binding.edtConfirmPassword, "Confirm password")) {

            String email = binding.edtEmail.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();
            String confirmPassword = binding.edtConfirmPassword.getText().toString().trim();
            String name = binding.edtUsername.getText().toString().trim();

            if (!password.equals(confirmPassword)) {
                Toast.makeText(getActivity(), "Password mismatch.", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (!task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String userId = auth.getUid();
                    saveUserData(name, userId);
                }
            });
        }
    }

    private void saveUserData(String name, String userId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        User user = new User();
        user.setName(name);
        ref.child(userId).setValue(user).addOnCompleteListener(task -> {

            if (!task.isSuccessful()) {
                Toast.makeText(getActivity(), "Oops! Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                return;
            }
            Navigation.findNavController(getView()).navigate(R.id.loginFragment);

        });

    }

}