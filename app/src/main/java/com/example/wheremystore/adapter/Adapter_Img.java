package com.example.wheremystore.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wheremystore.R;
import com.example.wheremystore.activity.Activity_Img;
import com.example.wheremystore.dto.ImgDTO;
import com.example.wheremystore.item.ItemList;
import com.example.wheremystore.item.Retrofit_Item;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Img extends RecyclerView.Adapter<Adapter_Img.MyViewHolder> {
    Context context;
    List<Uri> imgList;
    List<ImgDTO> imgDTOList;

    public Adapter_Img(List<Uri> imgList) {
        this.imgList = imgList;
    }

    public Adapter_Img(List<Uri> imgList, List<ImgDTO> imgDTOList) {
        this.imgDTOList = imgDTOList;
        this.imgList = imgList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public MyViewHolder(@NonNull View view) {
            super(view);
            imageView = view.findViewById(R.id.img_img);
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recycler_img, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if(imgList != null) {
            Uri image_uri = imgList.get(position);
            Glide.with(context).load(image_uri).into(holder.imageView);
        } else {
            String url = Retrofit_Item.getURL() + "image/";
            ImgDTO img = imgDTOList.get(position);
            Glide.with(context).load(url + img.getFile_name()).into(holder.imageView);

            holder.imageView.setOnClickListener(v -> {
                ItemList.setImgDTOList(imgDTOList);

                Intent secIntent = new Intent(context, Activity_Img.class);
                secIntent.putExtra("position", position);
                context.startActivity(secIntent);
            });
        }
    }

    @Override
    public int getItemCount() {
        if(imgList != null) {
            return imgList.size();
        } else {
            return imgDTOList.size();
        }
    }
}
