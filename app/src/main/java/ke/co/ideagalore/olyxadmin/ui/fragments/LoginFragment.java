package ke.co.ideagalore.olyxadmin.ui.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        binding.btnLogin.setOnClickListener(this);
        binding.tvSignup.setOnClickListener(this);
        binding.tvForgotPass.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnLogin) {
            loginUserWithEmailPassword();
        } else if (view == binding.tvForgotPass) {
            showForgotPasswordDialog();
        } else {
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

        EditText mail = dialog.findViewById(R.id.edt_email);

        TextView cancel = dialog.findViewById(R.id.tv_cancel);
        TextView logout = dialog.findViewById(R.id.tv_logout);
        cancel.setOnClickListener(view -> dialog.dismiss());
        logout.setOnClickListener(view -> {

            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (validateFields.validateEmailAddress(requireActivity(), mail)) {
                dialogs.showProgressDialog(requireActivity(), "Sending password reset link");
                String emailAddress =mail.getText().toString();
                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                dialogs.dismissProgressDialog();
                                dialogs.showSnackBar(requireActivity(), "Password reset link sent to " + emailAddress);
                            }
                        }).addOnFailureListener(e -> {
                            dialog.dismiss();
                            dialogs.dismissProgressDialog();
                            dialogs.showSnackBar(requireActivity(), e.getMessage());
                        });
            }

        });
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
                getPreferenceData();

            });


        }
    }

    public void savePreferencesData(String name, String businessName, String terminal) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Terminal", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("business", businessName);
        editor.putString("terminal", terminal);
        editor.commit();

        dialogs.dismissProgressDialog();
        startActivity(new Intent(getActivity(), Home.class));
        getActivity().finish();
    }

    private void getPreferenceData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Terminal", MODE_PRIVATE);
        name = sharedPreferences.getString("name", null);
        terminal = sharedPreferences.getString("terminal", null);

        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(terminal)){
            FirebaseAuth auth=FirebaseAuth.getInstance();
            terminal=auth.getUid();
            getTerminalData(terminal);
        }else {

            dialogs.dismissProgressDialog();
            startActivity(new Intent(getActivity(), Home.class));
            getActivity().finish();
        }
    }

    private void getTerminalData(String terminal) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(terminal);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    String business=snapshot.child("business").getValue().toString();
                    String businessId=terminal;
                    String owner=snapshot.child("name").getValue().toString();

                    savePreferencesData(owner,business, businessId);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}