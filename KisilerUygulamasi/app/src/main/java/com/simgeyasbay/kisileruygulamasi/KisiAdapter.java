package com.simgeyasbay.kisileruygulamasi;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simgeyasbay.kisileruygulamasi.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class KisiAdapter extends RecyclerView.Adapter<KisiAdapter.KisiHolder> {
    ArrayList<Kisi> kisiArrayList;
    public KisiAdapter(ArrayList<Kisi> kisiArrayList){
        this.kisiArrayList=kisiArrayList;
    }

    @NonNull
    @Override
    public KisiHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //elemanlarımızı arrayden recyclerView'de göstermek için
        RecyclerRowBinding recyclerRowBinding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new KisiHolder(recyclerRowBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull KisiHolder holder, int position) {
        holder.binding.recyclerViewtextView.setText(kisiArrayList.get(position).isim);
        //recyclerView de tıklama yaptığında hangi aktiviteye gideceğini bağlama
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.itemView.getContext(),KisiEkleme.class);
                //önceden olan değer ise old ile kontrol yapıp detayları gösterme kısmına gidecek
                intent.putExtra("info","old");
                intent.putExtra("id",kisiArrayList.get(holder.getAdapterPosition()).id);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return kisiArrayList.size();
    }

    public class KisiHolder extends RecyclerView.ViewHolder{
        private RecyclerRowBinding binding;
        public KisiHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding=binding;

        }
    }
}
