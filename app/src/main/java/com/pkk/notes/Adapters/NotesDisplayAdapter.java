package com.pkk.notes.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pkk.notes.Activities.MainActivity;
import com.pkk.notes.Activities.NotesActivity;
import com.pkk.notes.Models.NotesModel;
import com.pkk.notes.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotesDisplayAdapter extends RecyclerView.Adapter<NotesDisplayAdapter.ViewHolder> {

    List<NotesModel> modelList;
    Context context;

    public NotesDisplayAdapter(Context context, List<NotesModel> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_notes_recyclerview_display_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotesModel model = modelList.get(position);
        holder.title.setText(model.getTitle());
        holder.body.setText(model.getBody());
        SimpleDateFormat formater = new SimpleDateFormat("dd MMM yyyy hh:mm a");
        Date date = model.getDate();
        holder.time.setText(formater.format(date));
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NotesActivity.class);
                intent.putExtra("id", model.getId());
                context.startActivity(intent);
                ((MainActivity)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView body;
        TextView time;
        CardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notes_display_recyclerview_item_title);
            body = itemView.findViewById(R.id.notes_display_recyclerview_item_body);
            time = itemView.findViewById(R.id.notes_display_recyclerview_item_time);
            card = itemView.findViewById(R.id.notes_display_recyclerview_item_card);
        }
    }
}
