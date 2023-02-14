package android.example.picscanner;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 7;
    Toolbar toolbar;
    Context context = this;

    Button imageScanBtn;

    TextView totalImagesTextView;
    TextView totalBarcodesTextView;
    private int totalBarcodesVal;
    private int totalImagesVal;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageScanBtn = (Button) findViewById(R.id.imageScanBtn);
        toolbar = findViewById(R.id.toolbar);
        totalImagesTextView = findViewById(R.id.totalImagesTextView);
        totalBarcodesTextView = findViewById(R.id.totalBarcodeTextView);

        setSupportActionBar(toolbar);
        toolbar.setTitle("Pic Scanner");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        imageScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScanImageActivity.class);
                intent.putExtra("TOTAL_IMAGES", totalImagesVal);
                intent.putExtra("TOTAL_BARCODE", totalBarcodesVal);
                startActivity(intent);
            }
        });

        verifyStoragePermissions(MainActivity.this);
        getPreferenceValue();
        createMainDirectory();
    }

    private void createMainDirectory() {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Shipshop");
        if(!myDir.exists()){
            myDir.mkdirs();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh:
                getPreferenceValue();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void getPreferenceValue(){
        SharedPreferences sharedPreferences = getSharedPreferences("picscanner", Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        totalBarcodesVal = sharedPreferences.getInt("totalBarcode",10);
        totalImagesVal = sharedPreferences.getInt("totalImages",0);
        totalImagesTextView.setText("Total Barcode: " + String.valueOf(totalBarcodesVal));
        totalBarcodesTextView.setText("Total Images: " + String.valueOf(totalImagesVal));
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}