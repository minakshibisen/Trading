package com.abmtech.trading.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.abmtech.trading.R;
import com.abmtech.trading.databinding.ActivitySignupBinding;
import com.abmtech.trading.utils.ProgressDialog;
import com.abmtech.trading.utils.Session;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private String gender = "";
    private FirebaseFirestore db;
    private Session session;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        db = FirebaseFirestore.getInstance();

        session = new Session(this);
        pd = new ProgressDialog(this);


        binding.imageBack.setOnClickListener(v -> onBackPressed());

        binding.textContinue.setOnClickListener(v -> validate());

        binding.radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (radioGroup.getCheckedRadioButtonId() == R.id.radio_male) {
                gender = "male";
            } else if (radioGroup.getCheckedRadioButtonId() == R.id.radio_female) {
                gender = "female";
            } else if (radioGroup.getCheckedRadioButtonId() == R.id.radio_other) {
                gender = "other";
            }
        });

        binding.textDob.setOnClickListener(view -> showDatePickerDialog(binding.textDob));
        binding.textNomineeDob.setOnClickListener(view -> showDatePickerDialog(binding.textNomineeDob));
    }

    private void validate() {
        if (binding.edtFName.getText().toString().isEmpty()) {
            binding.edtFName.setError("Field Can't be empty!");
            binding.edtFName.requestFocus();
        } else if (binding.edtLName.getText().toString().isEmpty()) {
            binding.edtLName.setError("Field Can't be empty!");
            binding.edtLName.requestFocus();
        } else if (binding.edtEmail.getText().toString().isEmpty()) {
            binding.edtEmail.setError("Field Can't be empty!");
            binding.edtEmail.requestFocus();
        } else if (binding.edtPhoneNumber.getText().toString().isEmpty()) {
            binding.edtPhoneNumber.setError("Field Can't be empty!");
            binding.edtPhoneNumber.requestFocus();
        } else if (binding.edtAadharNumber.getText().toString().isEmpty()) {
            binding.edtAadharNumber.setError("Field Can't be empty!");
            binding.edtAadharNumber.requestFocus();
        } else if (binding.textDob.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter Date of Birth!", Toast.LENGTH_SHORT).show();
        } else if (binding.edtNomineeName.getText().toString().isEmpty()) {
            binding.edtNomineeName.setError("Field Can't be empty!");
            binding.edtNomineeName.requestFocus();
        } else if (binding.textNomineeDob.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter Nominee Date of Birth!", Toast.LENGTH_SHORT).show();
        } else if (binding.edtNomineeRelation.getText().toString().isEmpty()) {
            binding.edtNomineeRelation.setError("Field Can't be empty!");
            binding.edtNomineeRelation.requestFocus();
        } else if (binding.edtPanNumber.getText().toString().isEmpty()) {
            binding.edtPanNumber.setError("Field Can't be empty!");
            binding.edtPanNumber.requestFocus();
        } else if (binding.edtPassword.getText().toString().isEmpty()) {
            binding.edtPassword.setError("Field Can't be empty!");
            binding.edtPassword.requestFocus();
        } else if (gender.isEmpty()) {
            Toast.makeText(this, "Select Gender!", Toast.LENGTH_SHORT).show();
        } else if (binding.edtAddress.getText().toString().isEmpty()) {
            binding.edtAddress.setError("Field Can't be empty!");
            binding.edtAddress.requestFocus();
        } else if (binding.edtBankName.getText().toString().isEmpty()) {
            binding.edtBankName.setError("Field Can't be empty!");
            binding.edtBankName.requestFocus();
        } else if (binding.edtAccNumber.getText().toString().isEmpty()) {
            binding.edtAccNumber.setError("Field Can't be empty!");
            binding.edtAccNumber.requestFocus();
        } else if (binding.edtAccHolder.getText().toString().isEmpty()) {
            binding.edtAccHolder.setError("Field Can't be empty!");
            binding.edtAccHolder.requestFocus();
        } else if (binding.edtIfscCode.getText().toString().isEmpty()) {
            binding.edtIfscCode.setError("Field Can't be empty!");
            binding.edtIfscCode.requestFocus();
        } else {
            signup();
        }
    }

    private void signup() {
        pd.show();

        String edtFName = binding.edtFName.getText().toString();
        String edtLName = binding.edtLName.getText().toString();
        String edtEmail = binding.edtEmail.getText().toString();
        String edtPhoneNumber = binding.edtPhoneNumber.getText().toString();
        String edtAadharNumber = binding.edtAadharNumber.getText().toString();
        String textDob = binding.textDob.getText().toString();
        String edtNomineeName = binding.edtNomineeName.getText().toString();
        String textNomineeDob = binding.textNomineeDob.getText().toString();
        String edtNomineeRelation = binding.edtNomineeRelation.getText().toString();
        String edtPanNumber = binding.edtPanNumber.getText().toString();
        String edtPassword = binding.edtPassword.getText().toString();
        String edtAddress = binding.edtAddress.getText().toString();
        String edtBankName = binding.edtBankName.getText().toString();
        String edtAccNumber = binding.edtAccNumber.getText().toString();
        String edtAccHolder = binding.edtAccHolder.getText().toString();
        String edtIfscCode = binding.edtIfscCode.getText().toString();

        String name = edtFName + " " + edtLName;

        Map<String, Object> map = new HashMap<>();

        map.put("name", name);
        map.put("email", edtEmail);
        map.put("phone", edtPhoneNumber);
        map.put("aadhar", edtAadharNumber);
        map.put("dateOfBirth", textDob);
        map.put("nomineeName", edtNomineeName);
        map.put("nomineeDob", textNomineeDob);
        map.put("nomineeRelation", edtNomineeRelation);
        map.put("panNumber", edtPanNumber);
        map.put("password", edtPassword);
        map.put("gender", gender);
        map.put("address", edtAddress);
        map.put("bankName", edtBankName);
        map.put("accountNumber", edtAccNumber);
        map.put("accountHolder", edtAccHolder);
        map.put("investedAmount", "0");
        map.put("marketValue", "0");
        map.put("todayLoss", "0");
        map.put("overallGain", "0");
        map.put("ifscCode", edtIfscCode);


        String id = db.collection("users").document().getId();

        map.put("id", id);

        DocumentReference userRef = db.collection("users").document(id);

        userRef.set(map).addOnCompleteListener(task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        session.setUserId(id);

                        session.setLogin(true);
                        session.setEmail(edtEmail);
                        session.setMobile(edtPhoneNumber);
                        session.setUserName(name);
                        session.setType("user");

                        startActivity(new Intent(SignupActivity.this, DashboardActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));

                        Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("TAG", "Error adding document", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Log.e("TAG", "onFailure: Signup", e);
                });
    }

    public void showDatePickerDialog(TextView textView) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year1;
                    textView.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }
}