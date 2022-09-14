package ke.co.ideagalore.olyxadmin.ui.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.hardware.lights.LightState;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.adapters.CatalogueAdapter;
import ke.co.ideagalore.olyxadmin.common.CustomDialogs;
import ke.co.ideagalore.olyxadmin.common.ValidateFields;
import ke.co.ideagalore.olyxadmin.databinding.FragmentViewCategoryProductsBinding;
import ke.co.ideagalore.olyxadmin.models.Catalogue;

public class ViewCategoryProductsFragment extends Fragment implements View.OnClickListener {

    String title, category, terminal;

    FragmentViewCategoryProductsBinding binding;

    CustomDialogs customDialogs = new CustomDialogs();
    ValidateFields validator = new ValidateFields();
/*
    List<Catalogue> catalogueList = new ArrayList<>();
    List<Catalogue> categoryList = new ArrayList<>();*/

    List<Catalogue>catalogueList=new ArrayList<>();
    List<Catalogue>categoryList=new ArrayList<>();

    DatabaseReference reference;

    public ViewCategoryProductsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentViewCategoryProductsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPreferenceData();
        getBundleData();
        getCategoryData();

        binding.ivBack.setOnClickListener(this);
        binding.ivAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view == binding.ivBack) {
            Navigation.findNavController(view).navigate(R.id.catalogueItemsFragment);
        }

    }

    private void getBundleData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            category = arguments.get("category").toString();
            title = arguments.get("title").toString();
            binding.title.setText(title);
        }
    }

    private void getCategoryData() {
        catalogueList.clear();
        categoryList.clear();
        customDialogs.showProgressDialog(getActivity(), "Fetching category data");
        reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Catalogue");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot catalogueSnapshot : snapshot.getChildren()) {
                    Catalogue catalogue = catalogueSnapshot.getValue(Catalogue.class);
                    catalogueList.add(0, catalogue);
                    customDialogs.dismissProgressDialog();
                }

                for (int i = 0; i < catalogueList.size(); i++) {
                    Catalogue catalogueProduct = catalogueList.get(i);
                    if (catalogueProduct.getCategory().equals(category)) {
                        categoryList.add(0, catalogueProduct);
                        binding.tvTotalItems.setText(String.valueOf(categoryList.size()));
                        CatalogueAdapter adapter=new CatalogueAdapter(getActivity(),categoryList);
                        binding.rvProducts.setLayoutManager(new LinearLayoutManager(getActivity()));
                        binding.rvProducts.setHasFixedSize(true);
                        binding.rvProducts.setAdapter(adapter);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                customDialogs.showSnackBar(getActivity(), error.getMessage());
            }
        });
    }

    public void getPreferenceData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Terminal", MODE_PRIVATE);
        terminal = sharedPreferences.getString("terminal", null);

    }

}