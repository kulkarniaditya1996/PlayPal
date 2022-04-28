package com.example.playpal;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    private ItemClickListner itemClickListner;

    ArrayList<PlayActivity> list;
    StorageReference storageReference;



    public MyAdapter(Context context, ArrayList<PlayActivity> list,ItemClickListner itemClickListner) {
        this.context = context;
        this.list = list;
        this.itemClickListner = itemClickListner;
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.home_item,parent,false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        PlayActivity playActivity = list.get(position);

        try{
            holder.fullName.setText(playActivity.getOwner().getFullName());
            holder.sports.setText(playActivity.getSport());
            holder.loc.setText(playActivity.getLocation());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
            formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
            String dateInString = playActivity.getDate()+ " "+playActivity.getTime();
            Date check = null;
            check = formatter.parse(dateInString);
            String dt = check.toString();
            holder.date.setText(dt);
            holder.count.setText(""+playActivity.getUsers().size());
            StorageReference profileRef = storageReference.child(playActivity.getOwner().getuID()+".jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(holder.img);
                }
            });
        }catch (ParseException e) {
            e.printStackTrace();
        }
        //holder.time1.setText(playActivity.getTime());

        holder.itemView.setOnClickListener(view -> {
            itemClickListner.onItemClick(list.get(position));
        });
    }


    public interface ItemClickListner{
        void onItemClick(PlayActivity playActivity);
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView fullName,sports,date,loc,count;
        CircleImageView img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fullName = itemView.findViewById(R.id.name_tv);
            sports = itemView.findViewById(R.id.sport);
            date = itemView.findViewById(R.id.date_tv);
            loc = itemView.findViewById(R.id.address_tv);
            img = itemView.findViewById(R.id.image);
            count = itemView.findViewById(R.id.going_count);
        }




    }


}
