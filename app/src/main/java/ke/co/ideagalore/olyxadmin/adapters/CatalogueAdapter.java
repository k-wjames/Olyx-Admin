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
import ke.co.ideagalore.olyxadmin.models.Catalogue;

public class CatalogueAdapter extends RecyclerView.Adapter<CatalogueAdapter.ViewHolder> {

    Context context;
    ArrayList<Catalogue> catalogueArrayList;
    Catalogue catalogue;

    public CatalogueAdapter() {
    }

    public CatalogueAdapter(Context context, ArrayList<Catalogue> catalogueArrayList) {
        this.context = context;
        this.catalogueArrayList = catalogueArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.catalogue_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        catalogue=catalogueArrayList.get(position);
        holder.product.setText(catalogue.getProduct());
        holder.description.setText(catalogue.getDescription());
        holder.quantity.setText(catalogue.getQuantity()+"");
        holder.buying.setText(catalogue.getBuyingPrice()+"");
        holder.selling.setText(catalogue.getSellingPrice()+"");

    }

    @Override
    public int getItemCount() {
        return catalogueArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView product, description, quantity, buying, selling;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            product=itemView.findViewById(R.id.tv_product);
            description=itemView.findViewById(R.id.tv_description);
            quantity=itemView.findViewById(R.id.tv_quantity);
            buying=itemView.findViewById(R.id.tv_buying_price);
            selling=itemView.findViewById(R.id.tv_selling_price);
        }
    }
}
