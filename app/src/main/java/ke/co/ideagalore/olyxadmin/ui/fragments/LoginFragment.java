package ke.co.ideagalore.olyxadmin.ui.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.common.CustomDialogs;
import ke.co.ideagalore.olyxadmin.common.ValidateFields;
import ke.co.ideagalore.olyxadmin.databinding.FragmentLoginBinding;
import ke.co.ideagalore.olyxadmin.ui.activities.Home;

public class LoginFragment extends Fragment implements View.OnClickListener {

    FragmentLoginBinding binding;
    ValidateFields validateFields = new ValidateFields();
    CustomDialogs dialogs = new CustomDialogs();

    String terminal, businessName, name;

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

        getPreferenceData();

        binding.btnLogin.setOnClickListener(this);
        binding.tvSignup.setOnClickListener(this);
        binding.tvForgotPass.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnLogin) {
            loginUserWithEmailPassword();
        }else if (view==binding.tvForgotPass){
            showForgotPasswordDialog();
        }else {
            Navigation.findNavController(view).navigate(R.id.businessFragment);
        }
    }

    private void showForgotPasswordDialog() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.password_reset_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        TextView cancel = dialog.findViewById(R.id.tv_cancel);
        TextView logout = dialog.findViewById(R.id.tv_logout);
        cancel.setOnClickListener(view -> dialog.dismiss());
    }

    private void loginUserWithEmailPassword() {

        if (validateFields.validateEmailAddress(getActivity(), binding.edtEmail)
                && validateFields.validateEditTextFields(getActivity(), binding.edtPassword, "Password")) {

            dialogs.showProgressDialog(getActivity(), "Signing in...");
            String email = binding.edtEmail.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();

            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

                if (!task.isSuccessful()) {
                    dialogs.showSnackBar(getActivity(), "Something went wrong. Please try again.");
                    dialogs.dismissProgressDialog();
                    return;
                }
                dialogs.dismissProgressDialog();
                startActivity(new Intent(getActivity(), Home.class));
                getActivity().finish();


            });


        }
    }

    private void getPreferenceData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Terminal", MODE_PRIVATE);
        name = sharedPreferences.getString("name", null);
        terminal = sharedPreferences.getString("terminal", null);
    }
}