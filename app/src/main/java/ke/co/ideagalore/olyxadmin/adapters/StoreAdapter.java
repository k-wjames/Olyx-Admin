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
        holder.store.setText(store.getStore()+",");
        holder.location.setText(store.getLocation()+".");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog(view.getContext(), store.getStore(),store.getLocation(), store.getStoreId(), view);
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


    private void showEditDialog(Context context, String storeName, String storeLocation, String storeId, View viewed) {
        Dialog myDialog = new Dialog(context);
        myDialog.setContentView(R.layout.edit_shop_dialog);
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        myDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        myDialog.show();

        TextView store=myDialog.findViewById(R.id.tv_store);
        TextView location=myDialog.findViewById(R.id.tv_location);
        TextView title=myDialog.findViewById(R.id.tv_title);
        store.setText(storeName);
        location.setText(storeLocation);
        TextView cancel=myDialog.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(view -> myDialog.dismiss());

        ImageView ivDelete=myDialog.findViewById(R.id.iv_delete);
        ivDelete.setOnClickListener(view -> deleteThisStore(view.getContext(),storeId,title,myDialog));

        Button edit=myDialog.findViewById(R.id.btn_edit_store);
        edit.setOnClickListener(view -> {
            myDialog.dismiss();
            Bundle bundle=new Bundle();
            bundle.putString("store", storeName);
            bundle.putString("storeId", storeId);
            bundle.putString("storeLocation", storeLocation);
            Navigation.findNavController(viewed).navigate(R.id.editStoreFragment,bundle);
        });
    }

    private void deleteThisStore(Context context, String storeId, TextView title, Dialog myDialog) {
        String terminal;
        SharedPreferences sharedPreferences = context.getSharedPreferences("Terminal", MODE_PRIVATE);
        terminal = sharedPreferences.getString("terminal", null);

        title.setText("Removing store...");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(terminal).child("Stores");
        reference.child(storeId).removeValue().addOnCompleteListener(task -> {

            if (task.isSuccessful()){
                myDialog.dismiss();
            }

        });
    }

}
