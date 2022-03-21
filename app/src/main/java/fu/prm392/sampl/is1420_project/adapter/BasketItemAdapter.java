package fu.prm392.sampl.is1420_project.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import fu.prm392.sampl.is1420_project.R;
import fu.prm392.sampl.is1420_project.dto.BasketItemDocument;
import fu.prm392.sampl.is1420_project.listener.OnItemBasketItemClickListener;
import fu.prm392.sampl.is1420_project.viewholder.BasketItemViewHolder;

public class BasketItemAdapter extends RecyclerView.Adapter<BasketItemViewHolder> {
    private final OnItemBasketItemClickListener listener;
    private List<BasketItemDocument> list;
    private LayoutInflater inflater;
    private Context context;

    public BasketItemAdapter(List<BasketItemDocument> list, Context context, OnItemBasketItemClickListener listener) {
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BasketItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_basket, parent, false);
        BasketItemViewHolder viewHolder = new BasketItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BasketItemViewHolder holder, int position) {
        holder.bind(list.get(position), listener);
        holder.getTxtFoodName().setText(list.get(position).getFoodsInfo().getName());
        holder.getTxtPrice().setText(list.get(position).getBasketItemsInfo().getPrice() + "đ");
        holder.getTxtQuantity().setText("x" + list.get(position).getBasketItemsInfo().getQuantity());
        if (list.get(position).getFoodsInfo().getImage() != null) {
            Uri uri = Uri.parse(list.get(position).getFoodsInfo().getImage());
            Glide.with(holder.getImgFood().getContext())
                    .load(uri)
                    .into(holder.getImgFood());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
