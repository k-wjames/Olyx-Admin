package ke.co.ideagalore.olyxadmin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.models.Transaction;


public class SaleAdapter extends RecyclerView.Adapter<SaleAdapter.ViewHolder> {
    Context context;
    ArrayList<Transaction> saleItemsList;

    Transaction item;

    public SaleAdapter() {
    }

    public SaleAdapter(Context context, ArrayList<Transaction> saleItemsList) {
        this.context = context;
        this.saleItemsList = saleItemsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.sales_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        item=saleItemsList.get(position);
        holder.product.setText(item.getProduct()+" @ KES "+item.getSellingPrice());
        holder.quantity.setText("*"+item.getQuantity());
        holder.price.setText("KES "+item.getTotalPrice());

    }

    @Override
    public int getItemCount() {
        return saleItemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView product, quantity, price;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            product=itemView.findViewById(R.id.tv_product);
            quantity=itemView.findViewById(R.id.tv_quantity);
            price=itemView.findViewById(R.id.tv_total_price);
        }
    }
}
