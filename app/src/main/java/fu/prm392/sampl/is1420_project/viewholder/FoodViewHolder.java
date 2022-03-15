package fu.prm392.sampl.is1420_project.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fu.prm392.sampl.is1420_project.R;
import fu.prm392.sampl.is1420_project.dto.FoodDTO;
import fu.prm392.sampl.is1420_project.listener.OnItemClickListener;
import fu.prm392.sampl.is1420_project.listener.OnItemFoodClickListener;

public class FoodViewHolder extends RecyclerView.ViewHolder {

    private TextView txtFoodName, txtDescription, txtPrice;
    private ImageView imgFood;

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);
        txtFoodName = itemView.findViewById(R.id.txtFoodName);
        txtDescription = itemView.findViewById(R.id.txtDescription);
        txtPrice = itemView.findViewById(R.id.txtPrice);
        imgFood = itemView.findViewById(R.id.imgFood);
    }

    public TextView getTxtFoodName() {
        return txtFoodName;
    }

    public TextView getTxtDescription() {
        return txtDescription;
    }

    public TextView getTxtPrice() {
        return txtPrice;
    }

    public ImageView getImgFood() {
        return imgFood;
    }

    public void bind(FoodDTO foodDTO, OnItemFoodClickListener listener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(foodDTO);
            }
        });
    }
}
