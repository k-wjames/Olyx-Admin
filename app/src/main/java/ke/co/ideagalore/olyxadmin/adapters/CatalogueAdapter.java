package ke.co.ideagalore.olyxadmin.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.models.Catalogue;

public class CatalogueAdapter extends RecyclerView.Adapter<CatalogueAdapter.ViewHolder> {

    Context context;
    Catalogue catalogue;
    List<Catalogue> catalogueList;

    public CatalogueAdapter(Context context, List<Catalogue> catalogueList) {
        this.context = context;
        this.catalogueList = catalogueList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        catalogue = catalogueList.get(position);
        holder.product.setText(catalogue.getProduct());
        holder.selling.setText(String.valueOf(catalogue.getMarkedPrice()));
        holder.layout.setOnClickListener(view -> {

            Bundle bundle=new Bundle();
            bundle.putString("category", catalogue.getCategory());
            bundle.putString("product", catalogue.getProduct());
            bundle.putInt("buyingPrice", catalogue.getBuyingPrice());
            bundle.putInt("sellingPrice", catalogue.getMarkedPrice());
            bundle.putInt("stockedItems", catalogue.getStockedQuantity());
            bundle.putString("productId", catalogue.getProdId());
            Navigation.findNavController(view).navigate(R.id.editProductFragment, bundle);

        });

    }

    @Override
    public int getItemCount() {
        return catalogueList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView product, selling;
        RelativeLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            product = itemView.findViewById(R.id.tv_product);
            selling = itemView.findViewById(R.id.tv_price);
            layout=itemView.findViewById(R.id.layout);
        }
    }
}
