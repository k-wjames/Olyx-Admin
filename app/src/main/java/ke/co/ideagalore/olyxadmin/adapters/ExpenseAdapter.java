package ke.co.ideagalore.olyxadmin.adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.models.Expense;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
    List<Expense> expenseList;

    public ExpenseAdapter( List<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_item, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Expense expense=expenseList.get(position);
        int pos=position+1;
        String itemPosition;
        if (pos<10){
            itemPosition=0+""+pos+".";
        }else {
            itemPosition=pos+".";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date=sdf.format(new Date(expense.getDate()));

        LocalDate localDate = LocalDate.now(ZoneOffset.UTC);
        long dateToday = localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        holder.expense.setText(itemPosition+" "+expense.getCategory());
        holder.description.setText(expense.getDescription());
        holder.cost.setText("KES "+expense.getPrice());

        if (expense.getDate()==dateToday) {
            holder.time.setText(expense.getTime());
        } else {
            holder.time.setText( date+" "+expense.getTime());
        }



    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView expense, description,cost,time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            expense=itemView.findViewById(R.id.tv_expense);
            description=itemView.findViewById(R.id.tv_description);
            cost=itemView.findViewById(R.id.tv_cost);
            time=itemView.findViewById(R.id.tv_date_time);
        }
    }
}
