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

    public interface OnItemClickListener {
        void onItemClick(Attendant attendant);
    }
    List<Attendant> attendants;
    private final OnItemClickListener listener;

    public AttendantsAdapter(List<Attendant> attendants, OnItemClickListener listener) {
        this.attendants = attendants;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.attendant_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        int pos=position+1;
        holder.bind(pos,attendants.get(position), listener);

    }

    @Override
    public int getItemCount() {
        return attendants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView attendantName, store;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            attendantName=itemView.findViewById(R.id.tv_attendant);
            store=itemView.findViewById(R.id.tv_store);
        }

        public void bind(int pos, final Attendant attendant, final OnItemClickListener listener) {

            attendantName.setText(pos+". "+attendant.getAttendant());
            store.setText(attendant.getStore());
            itemView.setOnClickListener(v -> listener.onItemClick(attendant));
        }
    }
}
