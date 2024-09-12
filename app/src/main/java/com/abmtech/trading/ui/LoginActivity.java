package com.abmtech.trading.ui;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.abmtech.trading.databinding.ActivityLoginBinding;
import com.abmtech.trading.model.UserModel;
import com.abmtech.trading.utils.ProgressDialog;
import com.abmtech.trading.utils.Session;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private ProgressDialog pd;
    private Session session;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        session = new Session(this);
        pd = new ProgressDialog(this);

        binding.textLogin.setOnClickListener(v -> validate());
    }

    private void validate() {
        if (binding.edtEmail.getText().toString().isEmpty()) {
            binding.edtEmail.setError("Email can't be empty!");
            binding.edtEmail.requestFocus();
        } else if (binding.edtPassword.getText().toString().isEmpty()) {
            binding.edtPassword.setError("Password can't be empty!");
            binding.edtPassword.requestFocus();
        } else {
            getUser(binding.edtEmail.getText().toString());
        }
    }

    private void getUser(String value) {
        pd.show();
        CollectionReference ref = db.collection("users");

        ref.whereEqualTo("email", value)
                .whereEqualTo("password", binding.edtPassword.getText().toString().trim())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(LoginActivity.this, "Invalid User or Password!", Toast.LENGTH_SHORT).show();
                        } else {
                            List<UserModel> data = task.getResult().toObjects(UserModel.class);

                            if (data.size() > 0) {
                                UserModel model = data.get(0);

                                session.setUserId(model.getId());
                                session.setLogin(true);
                                session.setEmail(model.getEmail());
                                session.setUserName(model.getName());
                                session.setMobile(model.getPhone());
                                session.setAccountNumber(model.getAccountNumber());
                                session.setIfscCode(model.getIfscCode());

                                startActivity(new Intent(this, DashboardActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid User or Password!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Log.e(TAG, "Error =>", task.getException());
                        pd.dismiss();
                    }
                });
    }

}