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
import fu.prm392.sampl.is1420_project.viewholder.OrderViewHolder;

public class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder> {

    private final OnItemOrderClickListener listener;
    private List<OrderDTO> list;
    private LayoutInflater inflater;
    private Context context;

    public OrderAdapter(List<OrderDTO> list, Context context, OnItemOrderClickListener listener) {
        this.listener = listener;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_cart, parent, false);
        OrderViewHolder viewHolder = new OrderViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(list.get(position), listener);
        holder.getTxtRestaurantName().setText(list.get(position).getBasketsInfo().getRestaurantsInfo().getName());
        holder.getTxtBasketQuantity().setText(list.get(position).getBasketsInfo().getBasketQuantity() + " items");
        holder.getTxtBasketPrice().setText(list.get(position).getBasketsInfo().getBasketPrice() + "đ");
        holder.getTxtOrderTime().setVisibility(View.VISIBLE);
        holder.getTxtOrderTime().setText(list.get(position).getOrderTime().toString());
        holder.getTxtStaus().setText(list.get(position).getStatus());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
