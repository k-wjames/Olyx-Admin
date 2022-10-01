package ke.co.ideagalore.olyxadmin.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.models.Credit;


public class CreditAdapter extends RecyclerView.Adapter<CreditAdapter.ViewHolder> {

    List<Credit> creditList;
    Context context;
    String phone;

    public CreditAdapter(List<Credit> creditList, Context context) {
        this.creditList = creditList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.credit_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Credit credit = creditList.get(position);

        holder.name.setText(credit.getName());
        holder.product.setText(credit.getProduct());
        holder.amount.setText("KES " + credit.getAmount());
        holder.date.setText(credit.getDate() + " " + credit.getTime());

        phone = credit.getPhone();

        holder.ivCall.setOnClickListener(view -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phone));
            context.startActivity(callIntent);
        });

    }

    @Override
    public int getItemCount() {
        return creditList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, product, amount, date;
        ImageView ivCall;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_customer);
            product = itemView.findViewById(R.id.tv_product);
            amount = itemView.findViewById(R.id.tv_amount);
            date = itemView.findViewById(R.id.tv_date_time);
            ivCall = itemView.findViewById(R.id.iv_call);
        }
    }


}
