package com.example.wheremystore.item;

import static android.os.Environment.DIRECTORY_PICTURES;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.example.wheremystore.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class BottomSheet extends BottomSheetDialogFragment {
    View view;
    Intent intent = null;
    int ACTION_CAMERA = 1001;
    int ACTION_GALLERY = 1002;

    List<Uri> uriList = new ArrayList<>();

    File imgFile = null;
    String imageFilePath = null;
    Uri imgUri = null;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet, container, false);

        // 카메라로 가서 사진찍기
        view.findViewById(R.id.bottomSheet_camera).setOnClickListener(v -> {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                try {
                    // 빈 파일을 만들어서 가져온다.
                    imgFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(imgFile != null) {
                    imgUri = FileProvider.getUriForFile(requireContext(), "com.example.wheremystore.fileprovider", imgFile);
                    // 위에서 만든 빈 파일에 찍은 사진을 담아서 넘긴다.
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                }
            }
            startActivityForResult(intent, ACTION_CAMERA);
        });

        // 갤러리 가서 사진 선택하기
        view.findViewById(R.id.bottomSheet_gallery).setOnClickListener(v -> {
            intent = new Intent(Intent.ACTION_PICK);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // 다중 이미지를 가져올 수 있도록 셋팅
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, ACTION_GALLERY);
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ACTION_CAMERA) {
            // 카메라에서 사진을 선택한 경우
            if(resultCode == Activity.RESULT_OK) {
                // 사진 찍어서 확인 버튼을 누름
                Log.i("resultActivity", "Activity.Result_OK");

                uriList.add(imgUri);

                // Fragment_Add 에 값(Uri)들을 넘기기
                Bundle result = new Bundle();
                result.putString("imageFilePath", imageFilePath);
                result.putParcelableArrayList("uriList", (ArrayList<? extends Parcelable>) uriList);
                getParentFragmentManager().setFragmentResult("choiceImgRequestKey", result);
                dismiss();

            } else if(resultCode == Activity.RESULT_CANCELED) {
                // 사진 찍기를 취소해도 빈 파일이 이미 만들어졌기 때문에 0B 이미지 파일이 만들어진다.
                // 그래서 해당 파일을 바로 삭제해버린다.
                File deleteFile = new File(imageFilePath);
                boolean b = deleteFile.delete();
                Log.i("delete YN", "delete TF = " + b);
            }

        } else if(requestCode == ACTION_GALLERY) {
            // 갤러리에서 사진을 선택한 경우
            if(resultCode == Activity.RESULT_OK) {
                if(data != null) {
                    // 이미지를 하나라도 선택한 경우
                    if(data.getClipData() == null) {
                        // 이미지를 하나만 선택한 경우
                        Log.e("single choice : ", String.valueOf(data.getData()));
                        // data에서 절대경로로 이미지를 가져옴
                        Uri imageUri = data.getData();

                        uriList.add(imageUri);
                    } else {
                        // 이미지를 여러장 선택한 경우
                        ClipData clipData = data.getClipData();
                        Log.e("clipData", String.valueOf(clipData.getItemCount()));
                        if(clipData.getItemCount() > 10) {
                            // 사진을 11장 이상 선택한 경우
                            Toast.makeText(requireActivity(), "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            // 선택한 사진이 10장 이하인 경우
                            Log.e("TAG", "multiple choice");
                            for(int i=0; i<clipData.getItemCount(); i++) {
                                // 선택한 이미지들의 uri를 가져온다
                                Uri imageUri = clipData.getItemAt(i).getUri();
                                try {
                                    // uri를 list에 담는다.
                                    uriList.add(imageUri);
                                } catch (Exception e) {
                                    Log.e("TAG", "File select error", e);
                                }
                            }
                        }
                    }
                }

                // Fragment_Add 에 값(Uri)들을 넘기기
                Bundle result = new Bundle();
                // uriList, 직접 만든 List 를 넘기기 위해서는 Parcel 을 사용해야 함
                result.putString("imageFilePath", null);
                result.putParcelableArrayList("uriList", (ArrayList<? extends Parcelable>) uriList);
                getParentFragmentManager().setFragmentResult("choiceImgRequestKey", result);
                dismiss();
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private File createImageFile() throws IOException {
        // 저장소에 빈 파일을 아래의 내용으로 만들어서 사진을 찍고 확인을 누르면
        // 해당 파일을 채우게 되고, 내가 지정한 file_paths.xml 에 저장된다
        // 확인 버튼을 누르지 않아도 0B 빈 파일을 만들어 저장한다.
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String imageFileName = "WMS_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);

        imageFilePath = imageFile.getAbsolutePath();

        return imageFile;
    }
}