package fu.prm392.sampl.is1420_project.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fu.prm392.sampl.is1420_project.R;
import fu.prm392.sampl.is1420_project.dto.OrderDTO;
import fu.prm392.sampl.is1420_project.listener.OnItemOrderClickListener;

public class OwnerOrderViewHolder extends RecyclerView.ViewHolder {
    private TextView txtCustomerName, txtAddress, txtOrderTime, txtPrice, txtStatus;

    public OwnerOrderViewHolder(@NonNull View itemView) {
        super(itemView);
        txtCustomerName = itemView.findViewById(R.id.txtCustomerName);
        txtAddress = itemView.findViewById(R.id.txtAddress);
        txtOrderTime = itemView.findViewById(R.id.txtOrderTime);
        txtPrice = itemView.findViewById(R.id.txtPrice);
        txtStatus = itemView.findViewById(R.id.txtStatus);
    }

    public TextView getTxtCustomerName() {
        return txtCustomerName;
    }

    public TextView getTxtAddress() {
        return txtAddress;
    }

    public TextView getTxtOrderTime() {
        return txtOrderTime;
    }

    public TextView getTxtPrice() {
        return txtPrice;
    }

    public TextView getTxtStatus() {
        return txtStatus;
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
