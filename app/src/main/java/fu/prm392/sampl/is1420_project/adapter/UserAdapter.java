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
import fu.prm392.sampl.is1420_project.dto.UserDTO;
import fu.prm392.sampl.is1420_project.listener.OnItemUserClickListener;
import fu.prm392.sampl.is1420_project.viewholder.UserViewHolder;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {

    private final OnItemUserClickListener listener;
    private List<UserDTO> list;
    private LayoutInflater inflater;
    private Context context;

    public UserAdapter(List<UserDTO> list, Context context, OnItemUserClickListener listener) {
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_user, parent, false);
        UserViewHolder viewHolder = new UserViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(list.get(position), listener);
        holder.getTxtEmail().setText(list.get(position).getEmail());
        holder.getTxtName().setText(list.get(position).getName());
        holder.getTxtPhone().setText(list.get(position).getPhone());
        holder.getTxtStatus().setText(list.get(position).getStatus());
        holder.getTxtRole().setText(list.get(position).getRole());

        if (list.get(position).getPhotoUri() != null) {
            Uri uri = Uri.parse(list.get(position).getPhotoUri());
            Glide.with(holder.getImgUser().getContext())
                    .load(uri)
                    .into(holder.getImgUser());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
