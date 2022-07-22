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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import ke.co.ideagalore.olyxadmin.databinding.FragmentStockBinding;

public class StockFragment extends Fragment implements View.OnClickListener {

    FragmentStockBinding binding;

    public StockFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentStockBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getStockData();
        binding.ivBack.setOnClickListener(this);
    }



    private void getStockData() {
    }

    @Override
    public void onClick(View view) {
        if (view == binding.ivBack) {
            Navigation.findNavController(view).navigate(R.id.homeFragment);
        }
    }

}