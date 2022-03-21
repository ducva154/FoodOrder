package fu.prm392.sampl.is1420_project.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fu.prm392.sampl.is1420_project.R;
import fu.prm392.sampl.is1420_project.dto.BasketItemDocument;
import fu.prm392.sampl.is1420_project.listener.OnItemBasketItemClickListener;

public class BasketItemViewHolder extends RecyclerView.ViewHolder {
    private TextView txtFoodName, txtPrice, txtQuantity;
    private ImageView imgFood;

    public BasketItemViewHolder(@NonNull View itemView) {
        super(itemView);
        txtFoodName = itemView.findViewById(R.id.txtFoodName);
        imgFood = itemView.findViewById(R.id.imgFood);
        txtPrice = itemView.findViewById(R.id.txtPrice);
        txtQuantity = itemView.findViewById(R.id.txtQuantity);
    }

    public TextView getTxtFoodName() {
        return txtFoodName;
    }

    public TextView getTxtPrice() {
        return txtPrice;
    }

    public TextView getTxtQuantity() {
        return txtQuantity;
    }

    public ImageView getImgFood() {
        return imgFood;
    }

    public void bind(BasketItemDocument basketItemDocument, OnItemBasketItemClickListener listener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(basketItemDocument);
            }
        });
    }
}
