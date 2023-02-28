package com.example.wheremystore.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wheremystore.R;
import com.example.wheremystore.activity.Activity_Info;
import com.example.wheremystore.api_zip.StoreInfoAPI;
import com.example.wheremystore.dto.StoreInfoDTO;
import com.example.wheremystore.item.Retrofit_Item;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Adapter_myRecycler extends RecyclerView.Adapter<Adapter_myRecycler.MyViewHolder> {
    Context context;
    List<StoreInfoDTO> storeList;
    List<Boolean> bookmarkBooleanList = new ArrayList<>();
    List<Integer> bookmarkSeqList = new ArrayList<>();
    List<Integer> seqList = new ArrayList<>();
    List<String> idList = new ArrayList<>();
    StoreInfoAPI storeInfoAPI;

    public Adapter_myRecycler(List<StoreInfoDTO> storeList) {
        this.storeList = storeList;
        storeInfoAPI = Retrofit_Item.getRetrofit().create(StoreInfoAPI.class);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView nameTv, addrTv, typeTv;
        AppCompatImageButton bookmarkBtn;
        public MyViewHolder (@NonNull View view) {
            super(view);
            nameTv = view.findViewById(R.id.recyclerview_nameTv);
            addrTv = view.findViewById(R.id.recyclerview_addrTv);
            typeTv = view.findViewById(R.id.recyclerview_typeTv);
            linearLayout = view.findViewById(R.id.myRecyclerView_layout);
            bookmarkBtn = view.findViewById(R.id.recyclerview_bookmarkBtn);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recycler_my_recycler, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        StoreInfoDTO dto = storeList.get(position);
        holder.nameTv.setText(dto.getName());
        holder.addrTv.setText(dto.getAddress());
        String str = dto.getType();
        String type = "";
        for(int i=0; i<str.length(); i++) {
            String str2 = str.substring(i, i+1);
            if(str2.equals("1")) {
                if(i == 0) {
                    type +=  "붕어빵, ";
                } else if(i == 1) {
                    type += "떡볶이, ";
                } else if(i == 2) {
                    type += "호떡, ";
                } else if(i == 3) {
                    type += "군고구마, ";
                } else if(i == 4) {
                    type += "풀빵, ";
                } else if(i == 5) {
                    type += "타코야끼, ";
                } else if(i == 6) {
                    type += "기타, ";
                }
            } else if (str2.equals("0")) {
                type += "";
            }
        }
        if(type.length() != 0) {
            type = type.substring(0, type.length() - 2);
        }
        holder.typeTv.setText(type);

        if(dto.getBookmarkSeq() == 0) {
            holder.bookmarkBtn.setBackgroundResource(R.drawable.icon_bookmark_non);
            bookmarkBooleanList.add(position, false);
        } else {
            holder.bookmarkBtn.setBackgroundResource(R.drawable.icon_bookmark);
            bookmarkBooleanList.add(position, true);
        }

        bookmarkSeqList.add(position, dto.getBookmarkSeq());
        seqList.add(position, dto.getSeq());
        idList.add(position, dto.getId());

        holder.bookmarkBtn.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            ProgressDialog progressDialog = ProgressDialog.show(context, "안내", "잠시만 기다려주세요...", true, false);

            Log.i("test", "position : " + adapterPosition + " / bookmarkBoolean : " + bookmarkBooleanList.get(adapterPosition) + " / bookmarkSeq : " + bookmarkSeqList.get(adapterPosition) + " / seq : " + seqList.get(adapterPosition) + " / id : " + idList.get(adapterPosition));
            if(bookmarkBooleanList.get(adapterPosition)) {
                storeInfoAPI.deleteBookmark(bookmarkSeqList.get(adapterPosition)).enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                        if(response.isSuccessful()) {
                            holder.bookmarkBtn.setBackgroundResource(R.drawable.icon_bookmark_non);
                            bookmarkBooleanList.add(adapterPosition, false);
                            storeList.remove(adapterPosition);
                            notifyItemRemoved(adapterPosition);
                            progressDialog.dismiss();
                        } else {
                            Toast.makeText(context.getApplicationContext(), "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            Log.e("Activity_info_error", "bookmark error");
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                        Toast.makeText(context.getApplicationContext(), "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        Log.e("Activity_info_error", "bookmark error : " + t.getMessage());
                        progressDialog.dismiss();
                    }
                });

            } else {
                storeInfoAPI.registerBookmark(seqList.get(adapterPosition), idList.get(adapterPosition)).enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                        if(response.isSuccessful()) {
                            holder.bookmarkBtn.setBackgroundResource(R.drawable.icon_bookmark);
                            bookmarkBooleanList.add(adapterPosition, true);
                            progressDialog.dismiss();
                        } else {
                            Toast.makeText(context.getApplicationContext(), "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            Log.e("Activity_info_error", "bookmark error");
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                        Toast.makeText(context.getApplicationContext(), "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        Log.e("Activity_info_error", "bookmark error : " + t.getMessage());
                        progressDialog.dismiss();
                    }
                });
            }
        });

        holder.linearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, Activity_Info.class);
            intent.putExtra("seq", dto.getSeq());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }
}
