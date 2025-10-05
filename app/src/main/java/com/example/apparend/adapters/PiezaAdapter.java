
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

//
//    @Override
//    public void onBindViewHolder(@NonNull PiezaViewHolder holder, int position) {
//        Pieza pieza = listaPiezas.get(position);
//
//        holder.tvMaterial.setText("Perfil: " + pieza.getTipoMaterial());
//        holder.tvDescripcion.setText("Descripción: " + pieza.getDescripcion());
//        holder.tvCantidad.setText("Cantidad: " + pieza.getCantidad());
//
//        // 👉 Personalizar según perfil
//        switch (pieza.getTipoMaterial()) {
//            case "Circular":
//                holder.tvAncho.setText("Diámetro: " + String.format("%.2f", pieza.getAncho()));
//                holder.tvAlto.setVisibility(View.GONE);
//                holder.tvLargo.setText("Largo: " + String.format("%.2f", pieza.getLargo()));
//                break;
//
//            case "Plancha":
//                holder.tvAncho.setText("Ancho: " + String.format("%.2f", pieza.getAncho()));
//                holder.tvAlto.setText("Alto: " + String.format("%.2f", pieza.getAlto()));
//                holder.tvAlto.setVisibility(View.VISIBLE);
//                holder.tvLargo.setVisibility(View.GONE); // la plancha no usa largo
//                break;
//
//            default: // Cuadrado, Ángulo, Viga H, Otro...
//                holder.tvAncho.setText("Ancho: " + String.format("%.2f", pieza.getAncho()));
//                holder.tvAlto.setText("Alto: " + String.format("%.2f", pieza.getAlto()));
//                holder.tvAlto.setVisibility(View.VISIBLE);
//                holder.tvLargo.setText("Largo: " + String.format("%.2f", pieza.getLargo()));
//                holder.tvLargo.setVisibility(View.VISIBLE);
//                break;
//        }
//
//        holder.tvTotalM2.setText("Total: " + String.format("%.2f m²", pieza.getTotalM2()));
//
//        // ✅ Click corto → editar
//        holder.itemView.setOnClickListener(v -> {
//            if (listener != null) listener.onEditClick(pieza, position);
//        });
//
//        // ✅ Click largo → eliminar
//        holder.itemView.setOnLongClickListener(v -> {
//            if (listener != null) {
//                listener.onDeleteClick(pieza, position);
//                return true;
//            }
//            return false;
//        });
//    }
//

    @Override
    public void onBindViewHolder(@NonNull PiezaViewHolder holder, int position) {
        Pieza pieza = listaPiezas.get(position);

        holder.tvMaterial.setText("Perfil: " + pieza.getTipoMaterial());
        holder.tvDescripcion.setText("Descripción: " + pieza.getDescripcion());
        holder.tvCantidad.setText("Cantidad: " + pieza.getCantidad());

        // 👉 Personalizar según perfil
        switch (pieza.getTipoMaterial()) {
            case "Circular":
                holder.tvAncho.setText("Diámetro: " + String.format("%.2f", pieza.getAncho()));
                holder.tvAlto.setVisibility(View.GONE); // no aplica
                holder.tvLargo.setText("Largo: " + String.format("%.2f", pieza.getLargo()));
                holder.tvLargo.setVisibility(View.VISIBLE);
                break;

            case "Plancha":
                holder.tvAncho.setText("Ancho: " + String.format("%.2f", pieza.getAncho()));
                holder.tvAlto.setText("Alto: " + String.format("%.2f", pieza.getAlto()));
                holder.tvAlto.setVisibility(View.VISIBLE);
                holder.tvLargo.setVisibility(View.GONE); // la plancha no usa largo
                break;

            default: // Cuadrado, Ángulo, Viga H, Otro...
                holder.tvAncho.setText("Ancho: " + String.format("%.2f", pieza.getAncho()));
                holder.tvAlto.setText("Alto: " + String.format("%.2f", pieza.getAlto()));
                holder.tvAlto.setVisibility(View.VISIBLE);
                holder.tvLargo.setText("Largo: " + String.format("%.2f", pieza.getLargo()));
                holder.tvLargo.setVisibility(View.VISIBLE);
                break;
        }

        holder.tvTotalM2.setText("Total: " + String.format("%.2f m²", pieza.getTotalM2()));

        // ✅ Click corto → editar
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(pieza, position);
        });

        // ✅ Click largo → eliminar
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
}
