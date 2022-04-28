package com.example.playpal;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    Context context;
    private ItemClickListner itemClickListner;

    ArrayList<Messages> list;



    public ChatAdapter(Context context, ArrayList<Messages> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.chat_item,parent,false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Messages messages = list.get(position);

        holder.firstName.setText(messages.getFrom()+":");
        holder.message.setText(messages.getMessage());
        holder.date.setText(messages.getDate());
        holder.time.setText(messages.getTime());

        /*RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );*/





        holder.itemView.setOnClickListener(view -> {
            itemClickListner.onItemClick(list.get(position));
        });
    }






    public interface ItemClickListner{
        void onItemClick(Messages messages);
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView firstName, message,date,time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            firstName = itemView.findViewById(R.id.tvfirstName);
            message = itemView.findViewById(R.id.message);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
        }




    }


}
