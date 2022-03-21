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
import fu.prm392.sampl.is1420_project.dto.RestaurantDTO;
import fu.prm392.sampl.is1420_project.listener.OnItemClickListener;
import fu.prm392.sampl.is1420_project.viewholder.RestaurantViewHolder;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {

    private List<RestaurantDTO> list;
    private LayoutInflater inflater;
    private Context context;
    private final OnItemClickListener listener;

    public RestaurantAdapter(List<RestaurantDTO> list, Context context, OnItemClickListener listener) {
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_restaurant, parent, false);
        RestaurantViewHolder viewHolder = new RestaurantViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        holder.bind(list.get(position), listener);
        holder.getTxtRestaurantName().setText(list.get(position).getName());
        holder.getTxtLocation().setText(list.get(position).getLocation());
        holder.getTxtRate().setText(String.format("%s", list.get(position).getRate()));
        if (list.get(position).getImage() != null) {
            Uri uri = Uri.parse(list.get(position).getImage());
            Glide.with(holder.getImgRestaurant().getContext())
                    .load(uri)
                    .into(holder.getImgRestaurant());
        }

        if (list.get(position).getDistance() != null) {
            holder.getLnlDistance().setVisibility(View.VISIBLE);
            holder.getTxtDistance().setText(list.get(position).getDistance() + " km");
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}
