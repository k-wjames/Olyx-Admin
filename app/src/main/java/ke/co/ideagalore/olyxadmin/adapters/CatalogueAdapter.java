package ke.co.ideagalore.olyxadmin.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.models.Catalogue;

public class CatalogueAdapter extends RecyclerView.Adapter<CatalogueAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Catalogue item);
    }
    List<Catalogue> catalogueList;
    private final OnItemClickListener listener;

    public CatalogueAdapter(List<Catalogue> catalogueList, OnItemClickListener listener) {
        this.catalogueList = catalogueList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(catalogueList.get(position), listener);

    }

    @Override
    public int getItemCount() {
        return catalogueList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView product, sellingPrice, buyingPrice, shop, availableItems;
        CardView layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            product = itemView.findViewById(R.id.tv_product);
            sellingPrice = itemView.findViewById(R.id.tv_selling_price);
            buyingPrice=itemView.findViewById(R.id.tv_buying_price);
            shop=itemView.findViewById(R.id.tv_shop);
            availableItems=itemView.findViewById(R.id.tv_available);
            layout = itemView.findViewById(R.id.layout);

        }

        public void bind(final Catalogue catalogue, final OnItemClickListener listener) {
            product.setText(catalogue.getProduct());
            sellingPrice.setText("KES "+catalogue.getMarkedPrice());
            buyingPrice.setText("KES "+catalogue.getBuyingPrice());
            shop.setText("Outlet: "+catalogue.getShop());
            availableItems.setText(catalogue.getStockedQuantity()-catalogue.getSoldItems()+"/"+catalogue.getStockedQuantity());
            itemView.setOnClickListener(v -> listener.onItemClick(catalogue));
        }
    }
}
