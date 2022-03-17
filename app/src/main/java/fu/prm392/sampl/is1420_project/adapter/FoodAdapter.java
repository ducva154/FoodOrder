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
import fu.prm392.sampl.is1420_project.dto.FoodDTO;
import fu.prm392.sampl.is1420_project.dto.RestaurantDTO;
import fu.prm392.sampl.is1420_project.listener.OnItemClickListener;
import fu.prm392.sampl.is1420_project.listener.OnItemFoodClickListener;
import fu.prm392.sampl.is1420_project.viewholder.FoodViewHolder;
import fu.prm392.sampl.is1420_project.viewholder.RestaurantViewHolder;

public class FoodAdapter extends RecyclerView.Adapter<FoodViewHolder> {
    private List<FoodDTO> list;
    private LayoutInflater inflater;
    private Context context;
    private final OnItemFoodClickListener listener;

    public FoodAdapter(List<FoodDTO> list, Context context, OnItemFoodClickListener listener) {
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_menu, parent, false);
        FoodViewHolder viewHolder = new FoodViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        holder.bind(list.get(position), listener);
        holder.getTxtFoodName().setText(list.get(position).getName());
        holder.getTxtDescription().setText(list.get(position).getDescription());
        holder.getTxtPrice().setText(String.format("%s", list.get(position).getPrice()));
        if (list.get(position).getImage() != null) {
            Uri uri = Uri.parse(list.get(position).getImage());
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
