package com.example.apparend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PiezaAdapter extends RecyclerView.Adapter<PiezaAdapter.PiezaViewHolder> {
    private List<Pieza> listaPiezas;
    private Context context;
    private OnItemClickListener listener; // ✅ Agregar esto

    // ✅ INTERFAZ DENTRO DEL ADAPTER
    public interface OnItemClickListener {
        void onEditClick(Pieza pieza, int position);
        void onDeleteClick(Pieza pieza, int position);
    }

    // ✅ CONSTRUCTOR MODIFICADO para recibir el listener
    public PiezaAdapter(List<Pieza> listaPiezas, Context context, OnItemClickListener listener) {
        this.listaPiezas = listaPiezas;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PiezaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pieza, parent, false);
        return new PiezaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PiezaViewHolder holder, int position) {
        Pieza pieza = listaPiezas.get(position);
        holder.tvMaterial.setText(pieza.getTipoMaterial());
        holder.tvDimensiones.setText(pieza.getDimensiones());
        holder.tvCantidad.setText(String.valueOf(pieza.getCantidad()));
        holder.tvTotalM2.setText(String.format("%.2f m²", pieza.getTotalM2()));

        // ✅ AGREGAR LOS CLICK LISTENERS
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(pieza, position);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(pieza, position);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return listaPiezas.size();
    }

    public static class PiezaViewHolder extends RecyclerView.ViewHolder {
        TextView tvMaterial, tvDimensiones, tvCantidad, tvTotalM2;

        public PiezaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMaterial = itemView.findViewById(R.id.tvMaterial);
            tvDimensiones = itemView.findViewById(R.id.tvDimensiones);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            tvTotalM2 = itemView.findViewById(R.id.tvTotalM2);
        }
    }
}