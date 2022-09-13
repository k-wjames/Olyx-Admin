package ke.co.ideagalore.olyxadmin.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Text;

import java.util.List;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.models.Stores;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {
    List<Stores>stores;

    public StoreAdapter(List<Stores> stores) {
        this.stores = stores;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.store_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Stores store=stores.get(position);
        int pos=position+1;
        holder.store.setText(pos+". "+store.getStore()+",");
        holder.location.setText(store.getLocation()+".");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putString("store", store.getStore());
                bundle.putString("storeId", store.getStoreId());
                bundle.putString("storeLocation", store.getLocation());
                Navigation.findNavController(view).navigate(R.id.editStoreFragment,bundle);

            }
        });
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView store, location;
        ImageView more;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            store=itemView.findViewById(R.id.tv_store);
            location=itemView.findViewById(R.id.tv_location);
            more=itemView.findViewById(R.id.iv_more);
        }
    }

}
