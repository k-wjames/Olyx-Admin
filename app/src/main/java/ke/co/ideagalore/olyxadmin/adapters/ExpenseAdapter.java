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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Expense expense=expenseList.get(position);
        holder.expense.setText(expense.getExpense());
        holder.description.setText(expense.getDescription());
        holder.quantity.setText("Quantity : *" + expense.getQuantity());
        holder.cost.setText("Total cost : KES " + expense.getPrice());
        holder.time.setText( expense.getDate()+":"+expense.getTime());

    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView expense, description,quantity, cost,time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            expense=itemView.findViewById(R.id.tv_expense);
            description=itemView.findViewById(R.id.tv_description);
            quantity=itemView.findViewById(R.id.tv_quantity);
            cost=itemView.findViewById(R.id.tv_cost);
            time=itemView.findViewById(R.id.tv_date_time);
        }
    }
}
