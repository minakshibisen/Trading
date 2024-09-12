package com.abmtech.trading.ui;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.abmtech.trading.R;
import com.abmtech.trading.databinding.ActivityAboutUsBinding;
import com.abmtech.trading.model.ServiceModel;
import com.abmtech.trading.utils.ProgressDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AboutUsActivity extends AppCompatActivity {
    private ActivityAboutUsBinding binding;
    private FirebaseFirestore db;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(this);

        binding.icBack.setOnClickListener(v -> onBackPressed());

        getAboutUs();
    }

    private void getAboutUs() {
        CollectionReference ref = db.collection("about_us");

        ref.document("about")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        if (task.isSuccessful()) {
                            ServiceModel data = task.getResult().toObject(ServiceModel.class);

                            if (data != null)
                                binding.edtSubHeading.setText(data.getDescription());
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