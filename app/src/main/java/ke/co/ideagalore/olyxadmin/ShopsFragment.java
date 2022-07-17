package ke.co.ideagalore.olyxadmin;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import ke.co.ideagalore.olyxadmin.databinding.FragmentShopsBinding;

public class ShopsFragment extends Fragment implements View.OnClickListener {

    FragmentShopsBinding binding;

    public ShopsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentShopsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.ivBack.setOnClickListener(this);
        binding.fabShop.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.ivBack) {
            Navigation.findNavController(view).navigate(R.id.homeFragment);
        } else if (view == binding.fabShop) {

            showAddStockDialog();
        }
    }

    private void showAddStockDialog() {

        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.add_shop);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        TextView cancel = dialog.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(view -> dialog.dismiss());

    }
}