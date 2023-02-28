package com.example.wheremystore.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wheremystore.R;
import com.example.wheremystore.dto.ImgDTO;
import com.example.wheremystore.item.Retrofit_Item;

import java.util.List;

public class Adapter_ImgFull extends RecyclerView.Adapter<Adapter_ImgFull.PagerHolder> {
    Context context;
    List<ImgDTO> imgList;

    public Adapter_ImgFull(List<ImgDTO> imgList) {
        this.imgList = imgList;
    }

    public static class PagerHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public PagerHolder(@NonNull View view) {
            super(view);
            imageView = view.findViewById(R.id.imgFull_img);
        }
    }

    @NonNull
    @Override
    public PagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_img_full, parent, false);
        return new PagerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PagerHolder holder, int position) {
        String url = Retrofit_Item.getURL() + "image/";
        ImgDTO img = imgList.get(position);
        Log.i("test", "full url : " + url + img.getFile_name());
        Glide.with(context.getApplicationContext()).load(url + img.getFile_name()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imgList.size();
    }
}
