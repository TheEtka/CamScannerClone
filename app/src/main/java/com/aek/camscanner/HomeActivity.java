package com.aek.camscanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.aek.camscanner.databinding.ActivityHomeBinding;

import java.io.File;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    ListView lv_pdf;
    public static ArrayList<File> fileList = new ArrayList<>();
    PDFAdapter obj_adapter;
    File dir;

    private ActivityHomeBinding binding;
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    Uri imageUri;

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();
    }

    private void init()
    {
        lv_pdf = binding.lvPdf;

        dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "CamScannerStorage");
        getFile(dir);

        obj_adapter = new PDFAdapter(getApplicationContext(), fileList);
        lv_pdf.setAdapter(obj_adapter);

        lv_pdf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), PdfActivity.class);
                intent.putExtra("position",i);
                startActivity(intent);
            }
        });
    }

    private ArrayList<File> getFile(File dir) {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0)
        {
            for (int i=0; i<listFile.length; i++)
            {
                if (listFile[i].isDirectory())
                {
                    getFile(listFile[i]);
                }
                else
                {
                    boolean booleanPdf = false;
                    if (listFile[i].getName().endsWith(".pdf"))
                    {
                        for (int j=0; j<fileList.size(); j++)
                        {
                            if (fileList.get(j).getName().equals(listFile[i].getName()))
                            {
                                booleanPdf = true;
                            }
                        }
                        if (booleanPdf)
                        {
                            booleanPdf = false;
                        }
                        else
                        {
                            fileList.add(listFile[i]);
                        }
                    }
                }
            }
        }
        return fileList;
    }

    public void check(View v)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_DENIED)
            {
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CODE);
            }
            else
            {
                openCamera();
            }
        }
        else
        {
            openCamera();
        }
    }

    private void openCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Image");
        values.put(MediaStore.Images.Media.DESCRIPTION, "from the camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            Intent i = new Intent(HomeActivity.this, ScanActivity.class);
            i.setData(imageUri);
            startActivity(i);
        }
    }
}