
package com.example.apparend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apparend.R;
import com.example.apparend.models.Pieza;

import java.util.List;

public class PiezaAdapter extends RecyclerView.Adapter<PiezaAdapter.PiezaViewHolder> {
    private List<Pieza> listaPiezas;
    private Context context;
    private OnItemClickListener listener;

    // ✅ INTERFAZ para clicks (editar / eliminar)
    public interface OnItemClickListener {
        void onEditClick(Pieza pieza, int position);
        void onDeleteClick(Pieza pieza, int position);
    }

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

        holder.tvMaterial.setText("Perfil: " + pieza.getTipoMaterial());
        holder.tvDescripcion.setText("Descripción: " + pieza.getDescripcion());
        holder.tvCantidad.setText("Cantidad: " + pieza.getCantidad());

        // Obtener unidad y símbolo
        String unidad = pieza.getUnidadMedida() != null ? pieza.getUnidadMedida() : "Metros";
        String simbolo = obtenerSimbolo(unidad);

        // Mostrar valores con su unidad
        switch (pieza.getTipoMaterial()) {
            case "Circular":
                holder.tvAncho.setText("Diámetro: " + formatear(pieza.getAncho()) + " " + simbolo);
                holder.tvAlto.setVisibility(View.GONE);
                holder.tvLargo.setText("Largo: " + formatear(pieza.getLargo()) + " m");
                holder.tvLargo.setVisibility(View.VISIBLE);
                break;

            case "Plancha":
                holder.tvAncho.setText("Ancho: " + formatear(pieza.getAncho()) + " " + simbolo);
                holder.tvAlto.setText("Alto: " + formatear(pieza.getAlto()) + " " + simbolo);
                holder.tvAlto.setVisibility(View.VISIBLE);
                holder.tvLargo.setVisibility(View.GONE);
                break;

            default: // Cuadrado, Ángulo, Viga H, Otro...
                holder.tvAncho.setText("Ancho: " + formatear(pieza.getAncho()) + " " + simbolo);
                holder.tvAlto.setText("Alto: " + formatear(pieza.getAlto()) + " " + simbolo);
                holder.tvAlto.setVisibility(View.VISIBLE);
                holder.tvLargo.setText("Largo: " + formatear(pieza.getLargo()) + " m");
                holder.tvLargo.setVisibility(View.VISIBLE);
                break;
        }

        holder.tvTotalM2.setText("Total: " + String.format("%.2f m²", pieza.getTotalM2()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(pieza, position);
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
        TextView tvMaterial, tvCantidad, tvAncho, tvAlto, tvLargo, tvTotalM2,tvDescripcion;

        public PiezaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMaterial = itemView.findViewById(R.id.tvMaterial);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            tvAncho = itemView.findViewById(R.id.tvAncho);
            tvAlto = itemView.findViewById(R.id.tvAlto);
            tvLargo = itemView.findViewById(R.id.tvLargo);
            tvTotalM2 = itemView.findViewById(R.id.tvTotalM2);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
        }
    }


    private String obtenerSimbolo(String unidad) {
        switch (unidad) {
            case "Pulgadas": return "\"";
            case "Centimetros": return "cm";
            case "Milimetros": return "mm";
            case "Metros":
            default: return "m";
        }
    }

    private String formatear(float valor) {
        valor = Math.round(valor * 100f) / 100f;
        if (valor == (int) valor) return String.valueOf((int) valor);
        return String.format("%.2f", valor).replaceAll("0*$", "").replaceAll("\\.$", "");
    }


}
