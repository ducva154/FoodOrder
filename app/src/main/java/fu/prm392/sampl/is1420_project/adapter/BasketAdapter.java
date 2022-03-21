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
import fu.prm392.sampl.is1420_project.dto.BasketDTO;
import fu.prm392.sampl.is1420_project.listener.OnItemBasketClickListener;
import fu.prm392.sampl.is1420_project.viewholder.BasketViewHolder;

public class BasketAdapter extends RecyclerView.Adapter<BasketViewHolder> {

    private final OnItemBasketClickListener listener;
    private List<BasketDTO> list;
    private LayoutInflater inflater;
    private Context context;

    public BasketAdapter(List<BasketDTO> list, Context context, OnItemBasketClickListener listener) {
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.listener = listener;
    }


    @NonNull
    @Override
    public BasketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_cart, parent, false);
        BasketViewHolder viewHolder = new BasketViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BasketViewHolder holder, int position) {
        holder.bind(list.get(position), listener);
        holder.getTxtRestaurantName().setText(list.get(position).getRestaurantsInfo().getName());
        holder.getTxtBasketQuantity().setText(list.get(position).getBasketQuantity() + " items");
        holder.getTxtBasketPrice().setText(list.get(position).getBasketPrice() + "đ");
        if (list.get(position).getRestaurantsInfo().getImage() != null) {
            Uri uri = Uri.parse(list.get(position).getRestaurantsInfo().getImage());
            Glide.with(holder.getImgRestaurant().getContext())
                    .load(uri)
                    .into(holder.getImgRestaurant());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
