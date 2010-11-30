package com.oe.ourvillage;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;


public class Ours extends Activity {

    private class MsgDialogCallback implements MsgDialog.ReadyListener {

        String chalkID = "";
        ChalkPoster cp = null;
        FileOutputStream out = null;
        String url = getResources().getString(R.string.url_server);// "http://eitc.comze.com/chalk/upload_file.php";

        @Override
        public void ready(String chalk) {

            image.caption = chalk;

            cp = new ChalkPoster();

            // TODO mkm Last param is category. If unspecified, BlockChalk puts
            // chalk into 'chatter' category
            chalkID = cp.post(chalk, lat, lon, chalkUser, "");

            filename = Environment.getExternalStorageDirectory().toString() + "/" + chalkID
                       + ".jpg";

            fileAndPostTask(filename);

            // Let user know this will take a little time
            Toast.makeText(Ours.this, "Posting to server...", Toast.LENGTH_LONG).show();

            // Display the entered message
            Toast.makeText(Ours.this, chalk, Toast.LENGTH_LONG).show();

        }

        private void fileAndPostTask(String filename) {
            out = null;
            try {
                File file = new File(filename);
                out = new FileOutputStream(file);

                Bitmap bm = BitmapFactory.decodeByteArray(pictureBuffer, 0,
                                                          pictureBuffer.length);
                Bitmap sbm = Bitmap.createScaledBitmap(bm, 640, 480, false);
                sbm.compress(Bitmap.CompressFormat.JPEG, 40, out);

                out.close();
                bm.recycle();
                sbm.recycle();

                if (postFileToSite()) {
                    Toast.makeText(Ours.this, "Post Was Successful", Toast.LENGTH_SHORT)
                         .show();
                } else {
                    Toast.makeText(Ours.this, "Error Posting to Site", Toast.LENGTH_LONG)
                         .show();
                }

                // HttpFileUploader uploader = new HttpFileUploader(url,
                // filename, chalkID);
                // uploader.upload();
                file.delete();

            } catch (Exception ex) {
                Log.d(TAG, "dialog problem");
            }

        }

        private boolean postFileToSite() {
            try {
                HttpFileUploader uploader = new HttpFileUploader(url, filename, chalkID);
                uploader.upload();

            } catch (Exception e) {
                Log.d(TAG, "Error Posting to image to site");
                return false;
            }
            return true;
        }

    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            // format double to 5 decimal places
            DecimalFormat df = new DecimalFormat("#.#####");

            lat = df.format(location.getLatitude());
            lon = df.format(location.getLongitude());

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
            Toast.makeText(getApplicationContext(), "GPS Disabled", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
            Toast.makeText(getApplicationContext(), "GPS Enabled", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

    }

    // Chalk User id
    private static final String chalkUser = "0add24dec4864bc68bc2211b6cea0810";

    Button buttonClick;

    // image
    Image image = new Image();

    // Handles data for jpeg picture
    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {

            preview.camera.startPreview();

            // TODO mkm Some of this could be put in asynch threads
            // Dialog to get chalk message
            // TODO mkm would be nice to freeze image taken
            MsgDialog dialog = new MsgDialog(Ours.this, "", lat, lon, "myTime", "Mobile",
                                             new MsgDialogCallback());
            dialog.show();

            pictureBuffer = data.clone();
            lenPictureBuffer = data.length;
            Log.d(TAG, "onPictureTaken - jpeg");
        }
    };
    // location variables coordinates lat and lon
    String lat;

    int lenPictureBuffer;

    // views
    TextView locate;

    // location stuff
    LocationManager locationManager;
    String lon;

    byte[] pictureBuffer;

    // camera related
    Preview preview;
    // Handles data for raw picture - ignored, though could be used for image
    // reduction
    PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken - raw");
        }
    };

    ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
            Log.d(TAG, "Shutter with onShutter method");
        }
    };

    // filename
    private String filename;
    // final variables
    private final int REQUEST_CODE = -1;

    private final String TAG = "OurVillage";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // views
        // locate = (TextView) findViewById(R.id.locate);
        buttonClick = (Button) findViewById(R.id.buttonClick);

        buttonClick.setOnClickListener(new OnClickListener() {

            // handle camera shutter click
            public void onClick(View v) {

                // get location coordinates
                getThisLocation();

                // Dialog and chalk post moved to jpegCallback
                preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
            }

        });
    }

    /*
     * @Override public void onLocationChanged(Location location) { // format
     * double to 5 decimal places DecimalFormat df = new
     * DecimalFormat("#.#####");
     * 
     * lat = df.format(location.getLatitude()); lon =
     * df.format(location.getLongitude());
     * 
     * }
     * 
     * @Override public void onProviderDisabled(String provider) { // TODO
     * Auto-generated method stub
     * 
     * }
     * 
     * @Override public void onProviderEnabled(String provider) { // TODO
     * Auto-generated method stub
     * 
     * }
     * 
     * @Override public void onStatusChanged(String provider, int status, Bundle
     * extras) { // TODO Auto-generated method stub
     * 
     * }
     */
    private void buildAlertMessageNoGPS() {
        // Alert that the GPS is disabled and ask to enable it

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
               .setMessage("Your GPS seems to be disabled, do you want to enable it?")
               .setCancelable(false)
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(final DialogInterface dialog, final int id) {
                       launchGPSOptions();
                   }

                   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                       if (requestCode == REQUEST_CODE && resultCode == 0) {
                           String provider = Settings.Secure
                                                            .getString(
                                                                       getContentResolver(),
                                                                       Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                           if (provider != null) {
                               Log.v(TAG, " Location providers: " + provider);
                               getLatLongCordinates();

                           }
                       }
                   }

                   // GPS options
                   private void launchGPSOptions() {
                       Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                       startActivityForResult(intent, REQUEST_CODE);
                   }
               })
               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   public void onClick(final DialogInterface dialog,
                                       @SuppressWarnings("unused") final int id) {
                       dialog.cancel();
                   }
               });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    private void getLatLongCordinates() {
        LocationListener mlocListener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                                               mlocListener);

    }

    // Get the Location GPS
    private void getThisLocation() {
        // Is GPS Enabled if not alert or just get the coordinates
        if (!isGPSEnabled()) {
            // show alert no GPS message
            buildAlertMessageNoGPS();

        }
        // get location coordinates
        getLatLongCordinates();
    }

    // Check if GPS is enabled
    private boolean isGPSEnabled() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
    }

}
