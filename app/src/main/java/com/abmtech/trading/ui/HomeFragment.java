package com.abmtech.trading.ui;

import static android.content.ContentValues.TAG;
import static com.abmtech.trading.utils.Constants.getCurrentTimeStamp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abmtech.trading.R;
import com.abmtech.trading.adapter.LivePricesAdapter;
import com.abmtech.trading.adapter.ServicesAdapter;
import com.abmtech.trading.databinding.DialogAllOrderLayBinding;
import com.abmtech.trading.databinding.DialogPaymentLayBinding;
import com.abmtech.trading.databinding.DialogWithdrawLayBinding;
import com.abmtech.trading.databinding.FragmentHomeBinding;
import com.abmtech.trading.model.AllOrderModel;
import com.abmtech.trading.model.PriceModel;
import com.abmtech.trading.model.ServiceModel;
import com.abmtech.trading.model.UserModel;
import com.abmtech.trading.utils.ProgressDialog;
import com.abmtech.trading.utils.Session;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private FirebaseFirestore db;
    private ProgressDialog pd;

    final int duration = 20;
    final int pixelsToMove = 30;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Runnable SCROLLING_RUNNABLE = new Runnable() {
        @Override
        public void run() {
            binding.priceRecycler.smoothScrollBy(pixelsToMove, 0);
            mHandler.postDelayed(this, duration);
        }
    };
    private Session session;
    private UserModel model;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(getContext());
        session = new Session(getContext());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Session session = new Session(getContext());

        if (session.isLoggedIn()) {
            binding.llLoginRedirect.setVisibility(View.GONE);
            getUser();
        }

        binding.icCertified.setOnClickListener(v -> startActivity(new Intent(getContext(), ComplianceActivity.class)));
        binding.textLogin.setOnClickListener(v -> startActivity(new Intent(getContext(), LoginActivity.class)));
        binding.textRegister.setOnClickListener(v -> startActivity(new Intent(getContext(), SignupActivity.class)));
        binding.textDeposit.setOnClickListener(v -> addFunds());
        binding.textWithdraw.setOnClickListener(v -> withdrawFunds());
        binding.textAllOrders.setOnClickListener(v -> getAllOrder());

        getTransaction();
        getServices();
    }

    private void getAllOrder() {
        CollectionReference ref = db.collection("user_orders");

        ref.document(session.getUserId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        if (task.isSuccessful()) {
                            AllOrderModel data = task.getResult().toObject(AllOrderModel.class);

                            if (data != null)
                                allOrders(data);
                            else
                                Toast.makeText(getContext(), "Orders Not Found!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Orders Not Found!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Error =>", task.getException());
                        pd.dismiss();
                    }
                });
    }

    private void getUser() {
        pd.show();
        CollectionReference ref = db.collection("users");

        ref.document(session.getUserId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        if (!task.isSuccessful()) {
                            Toast.makeText(getContext(), "User Not Found!", Toast.LENGTH_SHORT).show();
                            session.logout();
                        } else {
                            model = task.getResult().toObject(UserModel.class);

                            if (model != null) {
                                session.setUserId(model.getId());
                                session.setLogin(true);
                                session.setEmail(model.getEmail());
                                session.setUserName(model.getName());
                                session.setMobile(model.getPhone());
                                session.setAccountNumber(model.getAccountNumber());
                                session.setIfscCode(model.getIfscCode());

                                binding.textInvestedAmount.setText(String.format("₹%s", model.getInvestedAmount()));
                                binding.textMarketAmount.setText(String.format("₹%s", model.getMarketValue()));
                                binding.textTodayLoss.setText(String.format("₹%s", model.getTodayLoss()));
                                binding.textOverallGain.setText(String.format("₹%s", model.getOverallGain()));

                                binding.llInvestmentDashboard.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(getContext(), "Invalid User!", Toast.LENGTH_SHORT).show();
                                session.logout();
                            }
                        }
                    } else {
                        Log.e(TAG, "Error =>", task.getException());
                        pd.dismiss();
                    }
                });
    }

    private void getTransaction() {
        CollectionReference ref = db.collection("prices");

        ref.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(getContext(), "No Transaction Found!", Toast.LENGTH_SHORT).show();
                        } else {
                            List<PriceModel> data = task.getResult().toObjects(PriceModel.class);

                            if (data.size() > 0) {
                                LivePricesAdapter adapter = new LivePricesAdapter(getContext(), data);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

                                binding.priceRecycler.setLayoutManager(layoutManager);
                                binding.priceRecycler.setAdapter(adapter);

                                binding.priceRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                        super.onScrolled(recyclerView, dx, dy);
                                        int lastItem = layoutManager.findLastCompletelyVisibleItemPosition();
                                        if (lastItem == layoutManager.getItemCount() - 1) {
                                            mHandler.removeCallbacks(SCROLLING_RUNNABLE);
                                            Handler postHandler = new Handler();
                                            postHandler.postDelayed(() -> {
                                                recyclerView.setAdapter(adapter);
                                                mHandler.postDelayed(SCROLLING_RUNNABLE, 0);
                                            }, 0);
                                        }
                                    }
                                });
                                mHandler.postDelayed(SCROLLING_RUNNABLE, 0);

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

    private void getServices() {
        CollectionReference ref = db.collection("services");

        ref.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(getContext(), "No Services Found!", Toast.LENGTH_SHORT).show();
                        } else {
                            List<ServiceModel> data = task.getResult().toObjects(ServiceModel.class);

                            if (data.size() > 0) {
                                binding.serviceRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                binding.serviceRecycler.setAdapter(new ServicesAdapter(data, getContext()));
                            } else {
                                Toast.makeText(getContext(), "No Services found!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Log.e(TAG, "Error =>", task.getException());
                        pd.dismiss();
                    }
                });
    }

    private void allOrders(AllOrderModel model) {
        DialogAllOrderLayBinding bb = DialogAllOrderLayBinding.inflate(getLayoutInflater());

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(bb.getRoot());

        bb.textDate.setText(model.getLiveDate());
        bb.textCurrencyName.setText(model.getLiveCurrencyName());
        bb.textBuyPrice.setText(model.getLiveBuyPrice());
        bb.textSellPrice.setText(model.getLiveSellPrice());
        bb.textQuantity.setText(model.getLiveQuantity());

        bb.textLiveTrade.setOnClickListener(v -> {
            bb.textLiveTrade.setBackgroundResource(R.drawable.custom_bg_gains);
            bb.textLiveTrade.setTextColor(getContext().getColor(R.color.white));

            bb.textCloseTrade.setBackgroundResource(R.drawable.custom_bg_unselected);
            bb.textCloseTrade.setTextColor(getContext().getColor(R.color.black));

            bb.textDate.setText(model.getLiveDate());
            bb.textCurrencyName.setText(model.getLiveCurrencyName());
            bb.textBuyPrice.setText(model.getLiveBuyPrice());
            bb.textSellPrice.setText(model.getLiveSellPrice());
            bb.textQuantity.setText(model.getLiveQuantity());
        });

        bb.textCloseTrade.setOnClickListener(v -> {
            bb.textCloseTrade.setBackgroundResource(R.drawable.custom_bg_gains);
            bb.textCloseTrade.setTextColor(getContext().getColor(R.color.white));

            bb.textLiveTrade.setBackgroundResource(R.drawable.custom_bg_unselected);
            bb.textLiveTrade.setTextColor(getContext().getColor(R.color.black));

            bb.textDate.setText(model.getCloseDate());
            bb.textCurrencyName.setText(model.getCloseCurrencyName());
            bb.textBuyPrice.setText(model.getCloseBuyPrice());
            bb.textSellPrice.setText(model.getCloseSellPrice());
            bb.textQuantity.setText(model.getCloseQuantity());
        });

        bottomSheetDialog.show();
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

    private void withdrawFunds() {
        DialogWithdrawLayBinding bb = DialogWithdrawLayBinding.inflate(getLayoutInflater());

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(bb.getRoot());

        bb.edtBankName.setText(model.getBankName());
        bb.edtAccNumber.setText(model.getAccountNumber());
        bb.edtAccHolder.setText(model.getAccountHolder());
        bb.edtIfscCode.setText(model.getIfscCode());
        bb.edtName.setText(model.getName());

        bb.cardSubmit.setOnClickListener(v -> {
            if (bb.edtBankName.getText().toString().isEmpty()) {
                bb.edtBankName.setError("Enter Bank Name!");
                bb.edtBankName.requestFocus();
            } else if (bb.edtAccNumber.getText().toString().isEmpty()) {
                bb.edtAccNumber.setError("Enter Account Number!");
                bb.edtAccNumber.requestFocus();
            } else if (bb.edtAccHolder.getText().toString().isEmpty()) {
                bb.edtAccHolder.setError("Enter Account Holder!");
                bb.edtAccHolder.requestFocus();
            } else if (bb.edtIfscCode.getText().toString().isEmpty()) {
                bb.edtIfscCode.setError("Enter Ifsc Code!");
                bb.edtIfscCode.requestFocus();
            } else if (bb.edtName.getText().toString().isEmpty()) {
                bb.edtName.setError("Enter Name!");
                bb.edtName.requestFocus();
            } else if (bb.edtTransactionAmount.getText().toString().isEmpty()) {
                bb.edtTransactionAmount.setError("Enter Amount!");
                bb.edtTransactionAmount.requestFocus();
            } else {
                addWithdraw(bb.edtBankName.getText().toString(), bb.edtAccNumber.getText().toString(),
                        bb.edtAccHolder.getText().toString(), bb.edtIfscCode.getText().toString(),
                        bb.edtName.getText().toString(), bb.edtTransactionAmount.getText().toString(), bottomSheetDialog);
            }
        });

        bottomSheetDialog.show();
    }

    private void addWithdraw(String bankName, String accNumber, String accHolder, String ifscCode, String name, String amount, BottomSheetDialog bottomSheetDialog) {
        pd.show();

        Map<String, Object> map = new HashMap<>();

        map.put("userId", session.getUserId());
        map.put("date", getCurrentTimeStamp());
        map.put("amount", amount);
        map.put("type", "Withdraw");
        map.put("time", String.valueOf(System.currentTimeMillis()));
        map.put("message", "Fund added by " + session.getUserName());
        map.put("status", "PENDING");
        map.put("bankName", bankName);
        map.put("accNumber", accNumber);
        map.put("accHolder", accHolder);
        map.put("ifscCode", ifscCode);
        map.put("name", name);

        String id = db.collection("withdraw").document().getId();

        map.put("id", id);

        DocumentReference userRef = db.collection("withdraw").document(id);

        userRef.set(map).addOnCompleteListener(task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        bottomSheetDialog.dismiss();
                        Toast.makeText(getContext(), "Withdraw added!", Toast.LENGTH_SHORT).show();
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


    private void addTransaction(String transactionId, String transactionAmount, BottomSheetDialog dialog) {
        pd.show();

        Map<String, Object> map = new HashMap<>();

        map.put("userId", session.getUserId());
        map.put("date", getCurrentTimeStamp());
        map.put("amount", transactionAmount);
        map.put("type", "Paid");
        map.put("time", String.valueOf(System.currentTimeMillis()));
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