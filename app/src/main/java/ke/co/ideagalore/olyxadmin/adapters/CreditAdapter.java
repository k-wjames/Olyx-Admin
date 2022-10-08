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
import ke.co.ideagalore.olyxadmin.models.Catalogue;
import ke.co.ideagalore.olyxadmin.models.Credit;


public class CreditAdapter extends RecyclerView.Adapter<CreditAdapter.ViewHolder> {

    List<Credit> creditList;

    public interface OnItemClickListener {
        void onItemClick(Credit item);
    }
    private final CreditAdapter.OnItemClickListener listener;

    public CreditAdapter(List<Credit> creditList,OnItemClickListener listener) {
        this.creditList = creditList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.credit_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.bind(creditList.get(position), listener);

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

        public void bind(final Credit credit, final CreditAdapter.OnItemClickListener listener) {
            name.setText(credit.getName());
            product.setText(credit.getProduct());
            amount.setText("KES "+credit.getAmount());
            date.setText(credit.getDate()+" "+credit.getTime());
            ivCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String phone=credit.getPhone();
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + phone));
                    view.getContext().startActivity(callIntent);
                }
            });
            itemView.setOnClickListener(v -> listener.onItemClick(credit));
        }
    }


}
