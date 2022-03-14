package fu.prm392.sampl.is1420_project.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fu.prm392.sampl.is1420_project.listener.OnItemClickListener;
import fu.prm392.sampl.is1420_project.R;
import fu.prm392.sampl.is1420_project.dto.RestaurantDTO;

public class RestaurantViewHolder extends RecyclerView.ViewHolder {
    private TextView txtRestaurantName, txtLocation, txtRate;
    private ImageView imgRestaurant;

    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        txtRestaurantName = itemView.findViewById(R.id.txtRestaurantName);
        txtLocation = itemView.findViewById(R.id.txtLocation);
        txtRate = itemView.findViewById(R.id.txtRate);
        imgRestaurant = itemView.findViewById(R.id.imgRestaurant);
    }

    public ImageView getImgRestaurant() {
        return imgRestaurant;
    }

    public TextView getTxtRestaurantName() {
        return txtRestaurantName;
    }

    public TextView getTxtLocation() {
        return txtLocation;
    }

    public TextView getTxtRate() {
        return txtRate;
    }


    public void bind(RestaurantDTO restaurantDTO, OnItemClickListener listener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(restaurantDTO);
            }
        });
    }
}
