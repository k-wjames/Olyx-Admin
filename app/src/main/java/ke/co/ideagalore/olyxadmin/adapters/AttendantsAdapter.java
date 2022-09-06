package ke.co.ideagalore.olyxadmin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.models.Attendant;

public class AttendantsAdapter extends RecyclerView.Adapter<AttendantsAdapter.ViewHolder> {

    List<Attendant> attendants;

    public AttendantsAdapter(List<Attendant> attendants) {
        this.attendants = attendants;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.attendant_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Attendant attendant=attendants.get(position);
        holder.attendant.setText(attendant.getAttendant());
        holder.store.setText(attendant.getStore());

    }

    @Override
    public int getItemCount() {
        return attendants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView attendant, store;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            attendant=itemView.findViewById(R.id.tv_attendant);
            store=itemView.findViewById(R.id.tv_store);
        }
    }
}
