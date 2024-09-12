package com.abmtech.trading.ui;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abmtech.trading.R;
import com.abmtech.trading.adapter.TransactionAdapter;
import com.abmtech.trading.databinding.FragmentTransactionBinding;
import com.abmtech.trading.model.TransactionModel;
import com.abmtech.trading.model.UserModel;
import com.abmtech.trading.utils.ProgressDialog;
import com.abmtech.trading.utils.Session;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;

public class TransactionFragment extends Fragment {
    private FragmentTransactionBinding binding;
    private Session session;
    private ProgressDialog pd;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTransactionBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        session = new Session(getContext());
        pd = new ProgressDialog(getContext());
        db = FirebaseFirestore.getInstance();

        getTransaction();
    }

    private void getTransaction() {
        pd.show();

        Query query = db.collection("transactions").orderBy("time", Query.Direction.ASCENDING);
        query
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(getContext(), "No Transaction Found!", Toast.LENGTH_SHORT).show();
                        } else {
                            List<TransactionModel> data = task.getResult().toObjects(TransactionModel.class);

                            if (data.size() > 0) {

                                List<TransactionModel> finalList = new ArrayList<>();
                                for (TransactionModel datum : data) {
                                    if (datum.getUserId().equals(session.getUserId()))
                                        finalList.add(datum);
                                }

                                if (finalList.size() > 0) {
                                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                    binding.recyclerView.setAdapter(new TransactionAdapter(getContext(), finalList));
                                } else {
                                    Toast.makeText(getContext(), "No transaction found!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "No transaction found!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Log.e(TAG, "Error =>", task.getException());
                        pd.dismiss();
                    }
                });
    }

}