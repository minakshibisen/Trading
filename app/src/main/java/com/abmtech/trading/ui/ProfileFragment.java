package com.abmtech.trading.ui;

import static com.abmtech.trading.utils.Constants.bitmapToBase64;
import static com.abmtech.trading.utils.Constants.decodeImage;
import static com.abmtech.trading.utils.Constants.getCurrentTimeStamp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.abmtech.trading.databinding.DialogPaymentLayBinding;
import com.abmtech.trading.databinding.FragmentProfileBinding;
import com.abmtech.trading.utils.ProgressDialog;
import com.abmtech.trading.utils.Session;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private Session session;
    private FirebaseFirestore db;
    private ProgressDialog pd;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    pd.show();


                    binding.profileImage.setImageURI(uri);
                    Bitmap bitmap = ((BitmapDrawable) binding.profileImage.getDrawable()).getBitmap();
                    String image = bitmapToBase64(bitmap);
                    session.setUserImage(image);
                    pd.dismiss();
                } else {
                    Toast.makeText(getContext(), "No media selected", Toast.LENGTH_SHORT).show();
                }
            });


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        session = new Session(getContext());
        pd = new ProgressDialog(getContext());

        binding.rlAddFunds.setOnClickListener(v -> addFunds());
        binding.rlLogout.setOnClickListener(v -> session.logout());
        binding.rlCompliance.setOnClickListener(v -> startActivity(new Intent(getContext(), ComplianceActivity.class)));
        binding.rlContactUs.setOnClickListener(v -> startActivity(new Intent(getContext(), ContactUsActivity.class)));
        binding.idAboutUs.setOnClickListener(v -> startActivity(new Intent(getContext(), AboutUsActivity.class)));

        binding.textEmail.setText(session.getEmail());
        binding.textName.setText(session.getUserName());
        if (!session.getUserImage().isEmpty())
            binding.profileImage.setImageBitmap(decodeImage(session.getUserImage()));

        binding.profileImage.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder().build()));
    }

    private void addFunds() {
        DialogPaymentLayBinding bb = DialogPaymentLayBinding.inflate(getLayoutInflater());

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(bb.getRoot());

        bb.cardSubmit.setOnClickListener(v -> {
            if (bb.edtTransactionAmount.getText().toString().isEmpty()) {
                bb.edtTransactionAmount.setError("Enter Transaction Amount!");
                bb.edtTransactionAmount.requestFocus();
            } else if (bb.edtTransactionId.getText().toString().isEmpty()) {
                bb.edtTransactionId.setError("Enter Transaction Id!");
                bb.edtTransactionId.requestFocus();
            } else {
                addTransaction(bb.edtTransactionId.getText().toString(), bb.edtTransactionAmount.getText().toString(), bottomSheetDialog);
            }
        });

        bottomSheetDialog.show();
    }

    private void addTransaction(String transactionId, String transactionAmount, BottomSheetDialog dialog) {
        pd.show();

        Map<String, Object> map = new HashMap<>();

        map.put("userId", session.getUserId());
        map.put("date", getCurrentTimeStamp());
        map.put("amount", transactionAmount);
        map.put("type", "Paid");
        map.put("transactionId", transactionId);
        map.put("message", "Fund added by " + session.getUserName());
        map.put("status", "PENDING");


        String id = db.collection("transactions").document().getId();

        map.put("id", id);

        DocumentReference userRef = db.collection("transactions").document(id);

        userRef.set(map).addOnCompleteListener(task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Funds added!", Toast.LENGTH_SHORT).show();
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