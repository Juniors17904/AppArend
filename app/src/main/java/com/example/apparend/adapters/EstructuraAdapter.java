package com.example.apparend.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apparend.R;
import com.example.apparend.models.Estructura;

import java.util.List;

public class EstructuraAdapter extends RecyclerView.Adapter<EstructuraAdapter.EstructuraViewHolder> {

    private List<Estructura> listaEstructuras;
    private Context context;

    public EstructuraAdapter(List<Estructura> listaEstructuras, Context context) {
        this.listaEstructuras = listaEstructuras;
        this.context = context;
    }

    @NonNull
    @Override
    public EstructuraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_estructura_row, parent, false);
        return new EstructuraViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EstructuraViewHolder holder, int position) {
        Estructura estructura = listaEstructuras.get(position);
        holder.tvDescripcion.setText(estructura.getDescripcion());
        holder.tvMetros.setText(String.format("M²: %.2f", estructura.getTotalM2()));

        if (estructura.getImagenUri() != null) {
            holder.imgEstructura.setImageURI(Uri.parse(estructura.getImagenUri()));
        } else {
            holder.imgEstructura.setImageResource(R.mipmap.ic_launcher); // imagen por defecto
        }
    }

    @Override
    public int getItemCount() {
        return listaEstructuras.size();
    }

    static class EstructuraViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescripcion, tvMetros;
        ImageView imgEstructura;

        public EstructuraViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvMetros = itemView.findViewById(R.id.tvMetros);
            imgEstructura = itemView.findViewById(R.id.imgEstructura);
        }
    }
}
