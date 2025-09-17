package com.example.apparend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PiezaAdapter extends RecyclerView.Adapter<PiezaAdapter.PiezaViewHolder> {

    private List<Pieza> listaPiezas;
    private OnItemClickListener mListener;

    public PiezaAdapter(List<Pieza> listaPiezas, OnItemClickListener listener) {
        this.listaPiezas = listaPiezas;
        this.mListener = listener;
    }

    @Override
    public PiezaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_estructura, parent, false);
        return new PiezaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PiezaViewHolder holder, int position) {
        Pieza pieza = listaPiezas.get(position);
        holder.materialTextView.setText("Material: " + pieza.getMaterial());
        holder.largoTextView.setText("Largo: " + pieza.getLargo() + " cm");
        holder.anchoTextView.setText("Ancho: " + pieza.getAncho() + " cm");

        // Configuración de los botones editar y eliminar
        holder.btnEditar.setOnClickListener(v -> mListener.onEditClick(pieza, position));
        holder.btnEliminar.setOnClickListener(v -> mListener.onDeleteClick(pieza, position));
    }

    @Override
    public int getItemCount() {
        return listaPiezas.size();
    }

    public interface OnItemClickListener {
        void onEditClick(Pieza pieza, int position);
        void onDeleteClick(Pieza pieza, int position);
    }

    public static class PiezaViewHolder extends RecyclerView.ViewHolder {

        TextView materialTextView, largoTextView, anchoTextView;
        ImageButton btnEditar, btnEliminar;

        public PiezaViewHolder(View itemView) {
            super(itemView);
            materialTextView = itemView.findViewById(R.id.tvMaterial);
            largoTextView = itemView.findViewById(R.id.tvLargo);
            anchoTextView = itemView.findViewById(R.id.tvAncho);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
