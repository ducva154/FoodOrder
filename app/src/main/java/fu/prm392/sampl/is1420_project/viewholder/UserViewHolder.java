package fu.prm392.sampl.is1420_project.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fu.prm392.sampl.is1420_project.R;
import fu.prm392.sampl.is1420_project.dto.UserDTO;
import fu.prm392.sampl.is1420_project.listener.OnItemUserClickListener;

public class UserViewHolder extends RecyclerView.ViewHolder {

    private TextView txtName, txtEmail, txtPhone, txtRole, txtStatus;
    private ImageView imgUser;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        txtName = itemView.findViewById(R.id.txtName);
        txtEmail = itemView.findViewById(R.id.txtEmail);
        txtPhone = itemView.findViewById(R.id.txtPhone);
        txtRole = itemView.findViewById(R.id.txtRole);
        txtStatus = itemView.findViewById(R.id.txtStatus);
        imgUser = itemView.findViewById(R.id.imgUser);
    }

    public TextView getTxtName() {
        return txtName;
    }

    public TextView getTxtEmail() {
        return txtEmail;
    }

    public TextView getTxtPhone() {
        return txtPhone;
    }

    public TextView getTxtRole() {
        return txtRole;
    }

    public TextView getTxtStatus() {
        return txtStatus;
    }

    public ImageView getImgUser() {
        return imgUser;
    }

    public void bind(UserDTO userDTO, OnItemUserClickListener listener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(userDTO);
            }
        });
    }
}
