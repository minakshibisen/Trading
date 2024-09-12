package com.abmtech.trading.ui;

import static android.content.ContentValues.TAG;

import static com.abmtech.trading.utils.Constants.getCurrentTimeStamp;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abmtech.trading.R;
import com.abmtech.trading.databinding.FragmentLoanBinding;
import com.abmtech.trading.model.LoanModel;
import com.abmtech.trading.utils.ProgressDialog;
import com.abmtech.trading.utils.Session;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class LoanFragment extends Fragment {
    private FragmentLoanBinding binding;
    private Session session;
    private FirebaseFirestore db;
    private ProgressDialog pd;
    private LoanModel data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoanBinding.inflate(inflater, container, false);
        session = new Session(getContext());
        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(getContext());

        getLoanDetails();

        binding.cardComplete.setOnClickListener(v -> acceptLoan());

        return binding.getRoot();
    }

    private void getLoanDetails() {
        CollectionReference ref = db.collection("loan");

        ref.document(session.getUserId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        if (task.isSuccessful()) {
                            data = task.getResult().toObject(LoanModel.class);

                            if (data != null) {

                                if (data.getStatus().equals("Accepted")) {
                                    binding.cardComplete.setOnClickListener(v -> Toast.makeText(getContext(), "Loan Already Accepted!", Toast.LENGTH_SHORT).show());
                                    binding.textCard.setText("Accepted");
                                }

                                binding.llLoan.setVisibility(View.VISIBLE);
                                binding.textEmpty.setVisibility(View.GONE);
                                binding.textAmount.setText("Loan Amount: " + data.getAmount());
                            } else
                                binding.textEmpty.setVisibility(View.VISIBLE);
                        } else {
                            binding.textEmpty.setVisibility(View.VISIBLE);
                        }
                    } else {
                        binding.textEmpty.setVisibility(View.VISIBLE);
                        Log.e(TAG, "Error =>", task.getException());
                        pd.dismiss();
                    }
                });
    }

    private void acceptLoan() {
        pd.show();

        Map<String, Object> map1 = new HashMap<>();

        map1.put("status", "Accepted");

        db.collection("loan").document(session.getUserId()).update(map1);

        Map<String, Object> map = new HashMap<>();

        map.put("userId", session.getUserId());
        map.put("date", getCurrentTimeStamp());
        map.put("amount", data.getAmount());
        map.put("type", "Loan");
        map.put("transactionId", "FxGrowLoans");
        map.put("message", "Loan Issued by FxGrow to " + session.getUserName());
        map.put("status", "PENDING");


        String id = db.collection("withdraw").document().getId();

        map.put("id", id);

        DocumentReference userRef = db.collection("withdraw").document(id);

        userRef.set(map).addOnCompleteListener(task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Loan Accepted!", Toast.LENGTH_SHORT).show();
                        binding.cardComplete.setOnClickListener(v -> Toast.makeText(getContext(), "Loan Already Accepted!", Toast.LENGTH_SHORT).show());
                        binding.textCard.setText("Accepted");
                    } else {
                        Toast.makeText(getContext(), "Error! Try Again", Toast.LENGTH_SHORT).show();
                        Log.e("TAG", "Error adding document", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error! Try Again", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    Log.e("TAG", "onFailure: Signup", e);
                });

    }


}