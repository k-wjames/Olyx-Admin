package ke.co.ideagalore.olyxadmin.adapters;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.models.Credit;


public class CreditAdapter extends RecyclerView.Adapter<CreditAdapter.ViewHolder> {

    List<Credit> creditList;

    public interface OnItemClickListener {
        void onItemClick(Credit item);
    }

    private final CreditAdapter.OnItemClickListener listener;

    public CreditAdapter(List<Credit> creditList, OnItemClickListener listener) {
        this.creditList = creditList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.credit_item, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bind(final Credit credit, final CreditAdapter.OnItemClickListener listener) {

            LocalDate localDate = LocalDate.now(ZoneOffset.UTC);
            long dateToday = localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String creditDate = sdf.format(new Date(credit.getDate()));

            name.setText(credit.getName());
            product.setText("Product: "+credit.getProduct());
            amount.setText("KES " + credit.getAmount());

            if (credit.getDate() == dateToday) {
                date.setText(credit.getTime());
            } else {
                date.setText(creditDate + " " + credit.getTime());
            }
            ivCall.setOnClickListener(view -> {
                String phone = credit.getPhone();
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phone));
                view.getContext().startActivity(callIntent);
            });
            itemView.setOnClickListener(v -> listener.onItemClick(credit));
        }
    }


}
