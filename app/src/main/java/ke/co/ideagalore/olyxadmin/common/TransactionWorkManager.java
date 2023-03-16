package ke.co.ideagalore.olyxadmin.common;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import ke.co.ideagalore.olyxadmin.models.Transaction;

public class TransactionWorkManager extends Worker {
    public TransactionWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        ContextCompat.getMainExecutor(getApplicationContext()).execute(new Runnable() {
            @Override
            public void run() {
                List<Transaction>transactionList=new ArrayList<>();
                recordTransaction(transactionList);
            }
        });
        return null;
    }

    private void recordTransaction(List<Transaction> transactionList) {
        if (transactionList.size()>0){
            for (Transaction transaction:transactionList){
                String key = transaction.getTransactionId();
                String terminal = transaction.getTerminalId();
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users").
                        child(terminal).child("Transactions").child("Sales");

            }
        }
    }


}
