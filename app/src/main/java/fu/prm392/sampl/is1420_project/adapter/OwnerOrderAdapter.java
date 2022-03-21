package fu.prm392.sampl.is1420_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fu.prm392.sampl.is1420_project.R;
import fu.prm392.sampl.is1420_project.dto.OrderDTO;
import fu.prm392.sampl.is1420_project.listener.OnItemOrderClickListener;
import fu.prm392.sampl.is1420_project.viewholder.OwnerOrderViewHolder;

public class OwnerOrderAdapter extends RecyclerView.Adapter<OwnerOrderViewHolder> {
    private final OnItemOrderClickListener listener;
    private List<OrderDTO> list;
    private LayoutInflater inflater;
    private Context context;

    public OwnerOrderAdapter(List<OrderDTO> list, Context context, OnItemOrderClickListener listener) {
        this.listener = listener;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public OwnerOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_order, parent, false);
        OwnerOrderViewHolder viewHolder = new OwnerOrderViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OwnerOrderViewHolder holder, int position) {
        holder.bind(list.get(position), listener);
        holder.getTxtCustomerName().setText(list.get(position).getUserInfo().getName());
        holder.getTxtOrderTime().setText(list.get(position).getOrderTime().toString());
        holder.getTxtStatus().setText(list.get(position).getStatus());
        holder.getTxtAddress().setText(list.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
