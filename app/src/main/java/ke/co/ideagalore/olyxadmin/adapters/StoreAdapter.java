package ke.co.ideagalore.olyxadmin.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.models.Attendant;
import ke.co.ideagalore.olyxadmin.models.Stores;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {
    List<Stores>stores;

    public interface OnItemClickListener {
        void onItemClick(Stores store);
    }

    private final OnItemClickListener listener;
    public StoreAdapter(List<Stores> stores, OnItemClickListener listener) {
        this.stores = stores;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.store_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos=position+1;
        holder.bind(pos,stores.get(position), listener);

    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView store, location;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            store=itemView.findViewById(R.id.tv_store);
            location=itemView.findViewById(R.id.tv_location);
        }

        public void bind(int pos, final Stores selectedStore, final OnItemClickListener listener) {

            store.setText(pos+". "+selectedStore.getStore()+", ");
            location.setText(selectedStore.getLocation());
            itemView.setOnClickListener(v -> listener.onItemClick(selectedStore));
        }
    }

}
