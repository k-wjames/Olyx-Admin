package ke.co.ideagalore.olyxadmin.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import javax.xml.namespace.NamespaceContext;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.common.ValidateFields;
import ke.co.ideagalore.olyxadmin.databinding.FragmentBusinessBinding;

public class BusinessFragment extends Fragment implements View.OnClickListener{

    FragmentBusinessBinding binding;
    ValidateFields validator = new ValidateFields();

    public BusinessFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentBusinessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (validator.validateEditTextFields(getActivity(), binding.edtBusiness, "Business name")){
            Bundle bundle=new Bundle();
            bundle.putString("business", binding.edtBusiness.getText().toString().trim());
            Navigation.findNavController(view).navigate(R.id.signUpFragment, bundle);
        }

    }
}