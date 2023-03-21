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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.common.CustomDialogs;
import ke.co.ideagalore.olyxadmin.common.ValidateFields;
import ke.co.ideagalore.olyxadmin.databinding.FragmentSignUpBinding;
import ke.co.ideagalore.olyxadmin.models.User;

public class SignUpFragment extends Fragment implements View.OnClickListener {
    FragmentSignUpBinding binding;
    ValidateFields validator = new ValidateFields();
    CustomDialogs dialogs = new CustomDialogs();
    String businessName, name, terminal;

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
        getBundleData();
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
            name = binding.edtUsername.getText().toString().trim();

            if (!password.equals(confirmPassword)) {
                dialogs.showSnackBar(getActivity(), "Password mismatch.");
                return;
            }

            dialogs.showProgressDialog(getActivity(), "Setting up user account.");

            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        String userId = auth.getUid();
                        terminal = userId;
                        saveUserData(name, userId);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialogs.dismissProgressDialog();
                    dialogs.showSnackBar(getActivity(), e.getMessage());
                    return;
                }
            });
        }
    }

    private void saveUserData(String name, String userId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        User user = new User();
        user.setName(name);
        user.setBusiness(businessName);
        ref.child(userId).setValue(user).addOnCompleteListener(task -> {

            if (!task.isSuccessful()) {
                dialogs.dismissProgressDialog();
                dialogs.showSnackBar(getActivity(), "Oops! Something went wrong. Please try again.");
                return;
            }
            dialogs.dismissProgressDialog();
            savePreferencesData();
            Navigation.findNavController(requireView()).navigate(R.id.loginFragment);

        });

    }

    private void getBundleData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            businessName = arguments.get("business").toString();
        }
    }

    public void savePreferencesData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Terminal", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("business", businessName);
        editor.putString("terminal", terminal);
        editor.commit();
    }

}