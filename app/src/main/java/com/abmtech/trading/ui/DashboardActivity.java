package com.abmtech.trading.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.abmtech.trading.R;
import com.abmtech.trading.databinding.ActivityDashboardBinding;
import com.abmtech.trading.utils.Session;

public class DashboardActivity extends AppCompatActivity {
    private ActivityDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Session session = new Session(this);

        binding.bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                changeFragment(new HomeFragment());
                return true;
            } else if (item.getItemId() == R.id.nav_transaction) {
                if (session.isLoggedIn()) changeFragment(new TransactionFragment());
                else login();
                return true;
            }  else if (item.getItemId() == R.id.nav_loan) {
                if (session.isLoggedIn()) changeFragment(new LoanFragment());
                else login();
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                if (session.isLoggedIn()) changeFragment(new ProfileFragment());
                else login();
                return true;
            } else return false;
        });

        changeFragment(new HomeFragment());

        binding.imageWhatsapp.setOnClickListener(v -> {
            String contact = "+91 9977218548";
            String url = "https://api.whatsapp.com/send?phone=" + contact;
            try {
                PackageManager pm = getPackageManager();
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(this, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    private void login() {
        Toast.makeText(this, "Login required for accessing this!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void changeFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(binding.container.getId(), fragment);

        ft.commit();
    }
}