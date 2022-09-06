package ke.co.ideagalore.olyxadmin.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.databinding.ActivityOnboardBinding;

public class Onboard extends AppCompatActivity {

    private NavController navController;
    ActivityOnboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        binding=ActivityOnboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navController = Navigation.findNavController(this, R.id.navigation_host);
    }
}