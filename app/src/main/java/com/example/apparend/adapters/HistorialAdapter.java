package com.example.apparend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apparend.R;

import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

    private List<String> listaTrabajos;

    public HistorialAdapter(List<String> listaTrabajos) {
        this.listaTrabajos = listaTrabajos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trabajo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String trabajo = listaTrabajos.get(position);
        holder.tvTrabajo.setText(trabajo);
    }

    @Override
    public int getItemCount() {
        return listaTrabajos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTrabajo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTrabajo = itemView.findViewById(R.id.tvTrabajo);
        }
    }
}
