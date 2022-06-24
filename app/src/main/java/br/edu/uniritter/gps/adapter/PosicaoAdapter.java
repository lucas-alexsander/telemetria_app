package br.edu.uniritter.gps.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import br.edu.uniritter.atsd.gps.R;
import br.edu.uniritter.gps.repositorios.PosicaoRepository;
import br.edu.uniritter.gps.servicosApp.PosicaoServices;

public class PosicaoAdapter extends RecyclerView.Adapter {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public ViewHolder(View view) {
            super(view);
            this.view = view;
            // Define click listener for the ViewHolder's View

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_posicao, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((TextView)holder.itemView.findViewById(R.id.tvPosicao)).setText(
                PosicaoServices.getInstance(null).getAllLocalizacao().get(position).toString());
//                PosicaoRepository.getInstance().getPosicoes().getValue().get(position).toString());
    }


    @Override
    public int getItemCount() {
        return PosicaoRepository.getInstance().getPosicoes().getValue().size();
    }
    public void refresh() {
        this.notifyDataSetChanged();
    }
}
