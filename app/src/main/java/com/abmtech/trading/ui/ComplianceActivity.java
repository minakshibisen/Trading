package com.abmtech.trading.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.abmtech.trading.R;
import com.abmtech.trading.databinding.ActivityComplianceBinding;

public class ComplianceActivity extends AppCompatActivity {
    private ActivityComplianceBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityComplianceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.icBack.setOnClickListener(v -> onBackPressed());
    }
}