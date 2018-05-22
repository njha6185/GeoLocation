package com.example.nitishkumar.geolocation;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_FROM_GALLERY = 1;
    private static final double SCALE_ADD_VALUE = 0.1;

    private Button addButton, rotateImageButton, imgAddScaleX, imgMinusScaleX, imgAddScaleY, imgMinusScaleY;
    private ImageView addImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Please Select Image !!!", Toast.LENGTH_SHORT).show();
                setImage();
            }
        });

        rotateImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImageView.setRotation(addImageView.getRotation() + 90);
            }
        });

        imgAddScaleX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImageView.setScaleX(addImageView.getScaleX() + (float) SCALE_ADD_VALUE);
            }
        });

        imgMinusScaleX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImageView.setScaleX(addImageView.getScaleX() - (float) SCALE_ADD_VALUE);
            }
        });

        imgAddScaleY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImageView.setScaleY(addImageView.getScaleY() + (float) SCALE_ADD_VALUE);
            }
        });

        imgMinusScaleY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImageView.setScaleY(addImageView.getScaleY() - (float) SCALE_ADD_VALUE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK)
        {
            Uri uri = data.getData();
//            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
//            cursor.moveToFirst();
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = cursor.getString(columnIndex);
//            cursor.close();
//            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
//            bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, true);
//            addImageView.setImageBitmap(bitmap);
//
            addImageView.setImageURI(uri);
//            addImageView.setRotation(-90);
//            addImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            Toast.makeText(MainActivity.this, "Image is set !!!", Toast.LENGTH_SHORT).show();
            addButton.setVisibility(View.GONE);
        }
    }

    private void setImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
    }

    private void init() {
        addButton = (Button)findViewById(R.id.addImage);
        addImageView = (ImageView)findViewById(R.id.imageView);
        rotateImageButton = (Button)findViewById(R.id.rotate_button);

        imgAddScaleX = (Button)findViewById(R.id.addScalex);
        imgMinusScaleX = (Button)findViewById(R.id.minusScalex);
        imgAddScaleY = (Button)findViewById(R.id.addScaley);
        imgMinusScaleY = (Button)findViewById(R.id.minusScaley);
    }
}
