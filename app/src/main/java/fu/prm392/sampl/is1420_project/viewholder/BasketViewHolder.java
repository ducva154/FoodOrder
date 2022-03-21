package fu.prm392.sampl.is1420_project.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fu.prm392.sampl.is1420_project.R;
import fu.prm392.sampl.is1420_project.dto.BasketDTO;
import fu.prm392.sampl.is1420_project.listener.OnItemBasketClickListener;

public class BasketViewHolder extends RecyclerView.ViewHolder {
    private TextView txtRestaurantName, txtBasketPrice, txtBasketQuantity;
    private ImageView imgRestaurant;
    private LinearLayout lnlDistance;

    public BasketViewHolder(@NonNull View itemView) {
        super(itemView);
        txtRestaurantName = itemView.findViewById(R.id.txtRestaurantName);
        imgRestaurant = itemView.findViewById(R.id.imgRestaurant);
        txtBasketPrice = itemView.findViewById(R.id.txtBasketPrice);
        lnlDistance = itemView.findViewById(R.id.lnlDistance);
        txtBasketQuantity = itemView.findViewById(R.id.txtBasketQuantity);
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

    public ImageView getImgRestaurant() {
        return imgRestaurant;
    }

    public void setImgRestaurant(ImageView imgRestaurant) {
        this.imgRestaurant = imgRestaurant;
    }

    public LinearLayout getLnlDistance() {
        return lnlDistance;
    }

    public void setLnlDistance(LinearLayout lnlDistance) {
        this.lnlDistance = lnlDistance;
    }

    public void bind(BasketDTO basketDTO, OnItemBasketClickListener listener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(basketDTO);
            }
        });
    }
}
