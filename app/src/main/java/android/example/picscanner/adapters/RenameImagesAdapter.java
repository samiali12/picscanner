package android.example.picscanner.adapters;

import android.content.Context;
import android.example.picscanner.R;
import android.example.picscanner.models.RenameImagesModel;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RenameImagesAdapter extends RecyclerView.Adapter<RenameImagesAdapter.ViewHolder> {

    List<RenameImagesModel> renameImagesModelList;
    Context context;

    public RenameImagesAdapter(Context context, List<RenameImagesModel> renameImagesAdapterList){
        this.renameImagesModelList = renameImagesAdapterList;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.capture_image_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RenameImagesModel renameImagesModel = renameImagesModelList.get(position);
        holder.imageView.setImageURI(renameImagesModel.getImagePath());
        //String imageUrl = renameImagesModel.getImageUrl();
        //Picasso.get().load(imageUrl).into(holder.imageView);
    }


    @Override
    public int getItemCount() {
        return renameImagesModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textView1;
        TextView textView2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView2);
        }
    }
}
