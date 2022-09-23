package ke.co.ideagalore.olyxadmin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.models.Transaction;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {

    List<Transaction> transactionList;

    public TransactionsAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Transaction transaction=transactionList.get(position);
        holder.transactionId.setText(transaction.getTransactionId());
        holder.product.setText(transaction.getProduct()+ " *" + transaction.getQuantity()+ "@ "+transaction.getSellingPrice());
        holder.price.setText("Received : KES "+transaction.getTotalPrice());
        holder.store.setText(transaction.getStore());

        if (getDate().equals(transaction.getDate())) {
            holder.time.setText(transaction.getTime());
        } else {
            holder.time.setText(transaction.getDate() + " " + transaction.getTime());
        }

    }

    String getDate() {
        String dateString;
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        dateString = formatter.format(date);
        return dateString;

    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView transactionId,time,product,price, store;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionId=itemView.findViewById(R.id.tv_transaction_id);
            time=itemView.findViewById(R.id.tv_time);
            product=itemView.findViewById(R.id.tv_product);
            price=itemView.findViewById(R.id.tv_price);
            store=itemView.findViewById(R.id.tv_store);
        }
    }
}
