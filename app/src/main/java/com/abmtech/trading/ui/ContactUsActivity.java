package com.abmtech.trading.ui;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.abmtech.trading.R;
import com.abmtech.trading.databinding.ActivityContactUsBinding;
import com.abmtech.trading.model.ContactUsModel;
import com.abmtech.trading.utils.ProgressDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ContactUsActivity extends AppCompatActivity {
    private ActivityContactUsBinding binding;
    private FirebaseFirestore db;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(this);

        getAboutUs();

        binding.icBack.setOnClickListener(v -> onBackPressed());
    }

    private void getAboutUs() {
        CollectionReference ref = db.collection("contact_us");

        ref.document("contact")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        if (task.isSuccessful()) {
                            ContactUsModel data = task.getResult().toObject(ContactUsModel.class);

                            if (data != null) {
                                binding.edtPhoneNumber.setText(data.getPhone());
                                binding.edtEmail.setText(data.getEmail());
                                binding.edtAddress.setText(data.getAddress());
                            }
                        } else {
                            Toast.makeText(this, "No Text found!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Error =>", task.getException());
                        pd.dismiss();
                    }
                });
    }

}