package ke.co.ideagalore.olyxadmin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.models.Refill;

public class RefillAdapter extends RecyclerView.Adapter<RefillAdapter.ViewHolder> {
    List<Refill>refillList;

    public RefillAdapter(List<Refill> refillList) {
        this.refillList = refillList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Refill refill=refillList.get(position);
        holder.product.setText(refill.getProduct());
        holder.buying.setText("KES "+refill.getMarkedPrice());

    }

    @Override
    public int getItemCount() {
        return refillList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView product, buying;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            product=itemView.findViewById(R.id.tv_product);
            buying=itemView.findViewById(R.id.tv_price);
        }
    }
}
