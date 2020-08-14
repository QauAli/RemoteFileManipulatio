package com.duplicatefile.remover.file.remotefilemanipulatio;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.duplicatefile.remover.file.remotefilemanipulatio.databinding.ActivityMainBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;
import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding mBinding;
    private Uri mImageUri;
    private File mImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.btnDownload.setOnClickListener(this);
        mBinding.imageView.setOnClickListener(this);
        mBinding.btnUpload.setOnClickListener(this);


    }

    private void downloadFile(Uri uri) {
        // Create request for android download manager
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                DownloadManager.Request.NETWORK_MOBILE);
        // set title and description
        request.setTitle("Data Download");
        request.setDescription("Android Data download using DownloadManager.");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.getStorageDirectory().getAbsolutePath(), "downloadfileName.pdf");
        request.setMimeType("*/*");
        downloadManager.enqueue(request);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_download:
//                Uri uri = Uri.parse("http://www.d2s.tech/uploads/generated.json");
                Uri uri = Uri.parse("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf");
                downloadFile(uri);
                break;
            case R.id.imageView:
                ImagePicker.Companion.with(this)
                        .crop()                    //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
                break;
            case R.id.btn_upload:
                if (mImageUri == null || mImageFile == null) {
                    Toast.makeText(this, "First Select image!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        postImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri fileUri = data.getData();
            mBinding.imageView.setImageURI(fileUri);
            mImageUri = fileUri;
            mImageFile = new File(fileUri.getPath());

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    public void postImage() throws IOException {
        final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
        RequestBody reqBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("name", "Telugu Sticker Pack 4")
                .addFormDataPart("photos",
                        mImageFile.getName(),
                        RequestBody.create(MEDIA_TYPE_JPG, mImageFile)).build();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://server.sofit.ltd:16642/uploadIcon")
                .post(reqBody)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("check", response.body().string());
            }
        });


    }
}