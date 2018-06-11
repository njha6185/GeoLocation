package com.example.nitishkumar.geolocation;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private static final int PICK_FROM_GALLERY = 1;
    private static final double SCALE_ADD_VALUE = 0.1;
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOUGE_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    public static ImageView weatherRelatedImage;
    public static TextView tempPlaceValue;
    List<Address> addresses;
    View clickscreen;
    ImageView mapImageView;
    SupportMapFragment mapFragment;
    private boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Button addButton, rotateImageButton, imgAddScaleX, imgMinusScaleX, imgAddScaleY, imgMinusScaleY;
    private ImageView addImageView;
    private RelativeLayout mapLayoutActivity, parent_layout;
    private CheckBox imageRatioCheck, readyRemoveCheckbox;
    private TextView addressTextview, addressCity, addressState, addressCountry, addressDateTime;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(MainActivity.this, "Map is ready!!!", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mMap.setMyLocationEnabled(true);

            readyRemoveCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    if (compoundButton.isChecked()) {
                        mMap.setMyLocationEnabled(false);
                        readyUiForImage();
                        interchangeMapWithImage();
                    } else {
                        mMap.setMyLocationEnabled(true);
                        readyUiForImage();
                    }
                }
            });
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
//        setMapActivityLayout();
        if (isServicesOK()) {
            playServiceInit();
        } else {
            Toast.makeText(this, "Some Problem Occured", Toast.LENGTH_SHORT).show();
        }
        getLocationPermission();
        init();
        addImage();
        rotateImage();
        scaleImage();
        screenshot();

    }

    public void screenshot() {
        clickscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "clicked image!!!", Toast.LENGTH_SHORT).show();
                View rootview = getWindow().getDecorView().findViewById(R.id.clickView).getRootView();
                rootview.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(rootview.getDrawingCache());
                int fromhere = (int) (bitmap.getHeight() * 0.96);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), fromhere);
                rootview.setDrawingCacheEnabled(false);

                final String dirpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Screenshots/";
                File dir = new File(dirpath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                SimpleDateFormat formateer = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.getDefault());
                Date now = new Date();
                String filename = formateer.format(now) + ".png";
                File file = new File(dirpath, filename);
                try {
                    FileOutputStream fout = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 85, fout);
                    Toast.makeText(MainActivity.this, "saved image!!!", Toast.LENGTH_SHORT).show();
                    fout.flush();
                    fout.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation : Getting Device current Location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete : Found Location");
                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation != null) {
                                addressTextview.setText("hello123");
                                updateLocationAddress(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                            } else {
                                Toast.makeText(MainActivity.this, "Location Not Found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "onComplete : Current Location is null");
                            Toast.makeText(MainActivity.this, "Unable to get current Location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation : SecurityException " + e.getMessage());
        }

    }

    public void interchangeMapWithImage() {
        mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                mapImageView.setImageBitmap(bitmap);
                mapImageView.setVisibility(View.VISIBLE);
                mapImageView.setAlpha(0.76f);
                mapFragment.getView().setVisibility(View.GONE);
            }
        });
    }

    private void updateLocationAddress(LatLng latLng) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            addressTextview.setText(addresses.get(0).getAddressLine(0));
            addressCity.setText(addresses.get(0).getLocality());
            addressState.setText(addresses.get(0).getAdminArea());
            addressCountry.setText(addresses.get(0).getCountryName());

            long date = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd(EEE)    h:mm(a)");
            addressDateTime.setText(sdf.format(date));
            weatherData();
        } catch (Exception e) {
            Log.e(TAG, "exception : " + e.toString());
        }
    }

    private void weatherData() {
        weatherRelatedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FetchData process = new FetchData(addresses.get(0).getLocality(), addresses.get(0).getSubAdminArea());
                Toast.makeText(MainActivity.this,
                        addresses.get(0).getLocality() + addresses.get(0).getSubAdminArea(),
                        Toast.LENGTH_SHORT).show();
                process.execute();
            }
        });
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera : Moving camera to lat : " + latLng.latitude + " lng : " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        mMap.addMarker(new MarkerOptions().position(latLng).
                icon(bitmapDescriptor(this, R.drawable.ic_location_on_black_24dp)));
    }

    private BitmapDescriptor bitmapDescriptor(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void initMap() {
        Log.d(TAG, "initMap : initialising Map");
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.getView().setAlpha(0.8f);
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission : Getting Location Permission!!!");
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult : Called ");
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onRequestPermissionsResult : Permission failed ");
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult : Permission Granted ");
                    mLocationPermissionGranted = true;
                    //init map
                    initMap();
                }
        }
    }

    public void playServiceInit() {
        Toast.makeText(this, "PlayService Is Ok", Toast.LENGTH_SHORT).show();

    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServiceOK: checking google service version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServiceOK: Google play service is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServiceOK: Error Occured But we can fix it.");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOUGE_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(MainActivity.this, "You can't make app request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void setMapActivityLayout() {
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mapLayoutActivity.getLayoutParams();
//        params.height = (parent_layout.getHeight())/5;
//        params.width = parent_layout.getWidth();
//        mapLayoutActivity.setLayoutParams(params);
//        mapLayoutActivity.setBackgroundResource(R.color.colorPrimary);
    }

    public void readyUiForImage() {
        if (readyRemoveCheckbox.isChecked()) {
            addButton.setVisibility(View.GONE);
            imgMinusScaleX.setVisibility(View.GONE);
            imgAddScaleX.setVisibility(View.GONE);
            imgAddScaleY.setVisibility(View.GONE);
            imgMinusScaleY.setVisibility(View.GONE);
            rotateImageButton.setVisibility(View.GONE);
            imageRatioCheck.setVisibility(View.GONE);
            readyRemoveCheckbox.setVisibility(View.GONE);
            clickscreen.setVisibility(View.VISIBLE);
        } else {
            addButton.setVisibility(View.VISIBLE);
            imgMinusScaleX.setVisibility(View.VISIBLE);
            imgAddScaleX.setVisibility(View.VISIBLE);
            imgAddScaleY.setVisibility(View.VISIBLE);
            imgMinusScaleY.setVisibility(View.VISIBLE);
            rotateImageButton.setVisibility(View.VISIBLE);
            imageRatioCheck.setVisibility(View.VISIBLE);
            readyRemoveCheckbox.setVisibility(View.VISIBLE);
            clickscreen.setVisibility(View.GONE);
        }

    }

    public void addImage() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Please Select Image !!!", Toast.LENGTH_SHORT).show();
                setImage();
            }
        });
    }

    public void rotateImage() {
        rotateImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImageView.setRotation(addImageView.getRotation() + 90);
            }
        });
    }

    public void scaleImage() {
        imgAddScaleX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImageView.setScaleX(addImageView.getScaleX() + (float) SCALE_ADD_VALUE);
                if (imageRatioCheck.isChecked()) {
                    addImageView.setScaleY(addImageView.getScaleY() + (float) SCALE_ADD_VALUE);
                }
            }
        });

        imgMinusScaleX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImageView.setScaleX(addImageView.getScaleX() - (float) SCALE_ADD_VALUE);
                if (imageRatioCheck.isChecked()) {
                    addImageView.setScaleY(addImageView.getScaleY() - (float) SCALE_ADD_VALUE);
                }
            }
        });

        imgAddScaleY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImageView.setScaleY(addImageView.getScaleY() + (float) SCALE_ADD_VALUE);
                if (imageRatioCheck.isChecked()) {
                    addImageView.setScaleX(addImageView.getScaleX() + (float) SCALE_ADD_VALUE);
                }
            }
        });

        imgMinusScaleY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImageView.setScaleY(addImageView.getScaleY() - (float) SCALE_ADD_VALUE);
                if (imageRatioCheck.isChecked()) {
                    addImageView.setScaleX(addImageView.getScaleX() - (float) SCALE_ADD_VALUE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK)
        {
            Uri uri = data.getData();
            addImageView.setImageURI(uri);
            Toast.makeText(MainActivity.this, "Image is set !!!", Toast.LENGTH_SHORT).show();
            addButton.setVisibility(View.GONE);
        }
    }

    private void setImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
        imageRatioCheck = (CheckBox) findViewById(R.id.ratioCheckbox);
        readyRemoveCheckbox = (CheckBox) findViewById(R.id.readyCheckbox);
        addressTextview = (TextView) findViewById(R.id.locationAddress);
        addressCity = (TextView) findViewById(R.id.city);
        addressState = (TextView) findViewById(R.id.state);
        addressCountry = (TextView) findViewById(R.id.country);
        addressDateTime = (TextView) findViewById(R.id.dateTime);
        weatherRelatedImage = (ImageView) findViewById(R.id.weatherImage);
        tempPlaceValue = (TextView) findViewById(R.id.tempValue);
        clickscreen = (View) findViewById(R.id.clickView);
        mapImageView = (ImageView) findViewById(R.id.imageMap);
//        mapLayoutActivity = (RelativeLayout)findViewById(R.id.lower_layout_container);
//        parent_layout = (RelativeLayout)findViewById(R.id.parentLayoutContainer);
    }

    @Override
    public void onLocationChanged(Location location) {
        getDeviceLocation();
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }
}
