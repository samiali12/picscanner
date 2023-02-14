package android.example.picscanner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.example.picscanner.adapters.RenameImagesAdapter;
import android.example.picscanner.models.RenameImagesModel;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageCaptureActivity extends AppCompatActivity {

    private Context context=this;

    private final int CAMERA_REQUEST_CODE = 100;
    private String BARCODE_DATA = "";
    private int imageCount = 0;
    private Toolbar toolbar;
    private ImageView imageView;
    private Uri imagePath;

    // Buttons
    Button addNewImageBtn;
    Button done;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;

    // Recycler view and adapter
    RenameImagesAdapter renameImagesAdapter;
    List<RenameImagesModel> arrayList;
    RecyclerView imageRecyclerView;

    int totalImagesVal;

    File myDir = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture);

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // shared preference use to store value of total barcode and total images that are scanned
        BARCODE_DATA = getIntent().getStringExtra("BARCODE_DATA");

        toolbar = findViewById(R.id.toolbarImageCaptureActivity);

        imageRecyclerView = findViewById(R.id.imcRecyclerView);
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        arrayList = new ArrayList<RenameImagesModel>();
        renameImagesAdapter = new RenameImagesAdapter(ImageCaptureActivity.this, arrayList);
        imageRecyclerView.setAdapter(renameImagesAdapter);

        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }


        totalImagesVal = getIntent().getIntExtra("TOTAL_IMAGES",0);

        addNewImageBtn = findViewById(R.id.add);
        done = findViewById(R.id.done);

        addNewImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, CAMERA_REQUEST_CODE);
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImageCaptureActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        createImageDirectory();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu ) {
        getMenuInflater().inflate(R.menu.imagecapturemenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {

        switch (item.getItemId()){
            case R.id.capture_image:
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, CAMERA_REQUEST_CODE);
                break;

            case R.id.rename_image:
                Intent intent = new Intent(ImageCaptureActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap img = (Bitmap) (data.getExtras().get("data"));
            imageCount += 1;
            // Shared Preferennce is used to store the value
            SharedPreferences sharedPreferences = getSharedPreferences("picscanner", MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            totalImagesVal += imageCount;
            myEdit.putInt("totalImages", totalImagesVal);
            myEdit.apply();
            imagePath = getImageUri(ImageCaptureActivity.this,img);
            arrayList.add(new RenameImagesModel(String.valueOf(imageCount),imagePath));
            saveImageToExternalStorage(img);
            uploadImage();
            renameImagesAdapter.notifyDataSetChanged();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        String title = BARCODE_DATA + "_" + String.valueOf(imageCount);
        File tempFile = new File(getCacheDir(), title+".jpg");
        try {
            FileOutputStream fos = new FileOutputStream(tempFile);
            inImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Uri uri = Uri.fromFile(tempFile);
        return uri;
    }


    private void createImageDirectory() {
        String root = Environment.getExternalStorageDirectory().toString();
        myDir = new File(root + "/Shipshop/"+BARCODE_DATA);
        myDir.mkdirs();
    }

    private void saveImageToExternalStorage(Bitmap imageBitmap) {

        String imageFileName = BARCODE_DATA+"_"+String.valueOf(imageCount);

        File file = new File(myDir, imageFileName + ".jpg");

        try {
            FileOutputStream out = new FileOutputStream(file);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Toast.makeText(getApplicationContext(), "Image saved successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error saving image", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void uploadImage(){

        // Code for showing progressDialog while uploading
        ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference ref = storageReference.child(BARCODE_DATA+"/"+ imageCount);

        ref.putFile(imagePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(ImageCaptureActivity.this, "Image Rename Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ImageCaptureActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00* snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                        progressDialog.setMessage("Percentage: "+(int)progressPercent);
                    }
                });

    }
}