package fu.prm392.sampl.is1420_project.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fu.prm392.sampl.is1420_project.R;
import fu.prm392.sampl.is1420_project.dto.OrderDTO;
import fu.prm392.sampl.is1420_project.listener.OnItemOrderClickListener;

public class OrderViewHolder extends RecyclerView.ViewHolder {
    private TextView txtRestaurantName, txtBasketPrice, txtBasketQuantity, txtOrderTime, txtStaus;
    private ImageView imgRestaurant;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        txtRestaurantName = itemView.findViewById(R.id.txtRestaurantName);
        imgRestaurant = itemView.findViewById(R.id.imgRestaurant);
        txtBasketPrice = itemView.findViewById(R.id.txtBasketPrice);
        txtBasketQuantity = itemView.findViewById(R.id.txtBasketQuantity);
        txtOrderTime = itemView.findViewById(R.id.txtOrderTime);
        txtStaus = itemView.findViewById(R.id.txtStatus);
    }

    public TextView getTxtStaus() {
        return txtStaus;
    }

    public void setTxtStaus(TextView txtStaus) {
        this.txtStaus = txtStaus;
    }

    public TextView getTxtRestaurantName() {
        return txtRestaurantName;
    }

    public void setTxtRestaurantName(TextView txtRestaurantName) {
        this.txtRestaurantName = txtRestaurantName;
    }

    public TextView getTxtBasketPrice() {
        return txtBasketPrice;
    }

    public void setTxtBasketPrice(TextView txtBasketPrice) {
        this.txtBasketPrice = txtBasketPrice;
    }

    public TextView getTxtBasketQuantity() {
        return txtBasketQuantity;
    }

    public void setTxtBasketQuantity(TextView txtBasketQuantity) {
        this.txtBasketQuantity = txtBasketQuantity;
    }

    public TextView getTxtOrderTime() {
        return txtOrderTime;
    }

    public void setTxtOrderTime(TextView txtOrderTime) {
        this.txtOrderTime = txtOrderTime;
    }

    public ImageView getImgRestaurant() {
        return imgRestaurant;
    }

    public void setImgRestaurant(ImageView imgRestaurant) {
        this.imgRestaurant = imgRestaurant;
    }

    public void bind(OrderDTO orderDTO, OnItemOrderClickListener listener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(orderDTO);
            }
        });
    }
}
