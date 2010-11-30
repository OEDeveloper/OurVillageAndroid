package com.oe.ourvillage;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.Date;


public class OurVillage extends Activity implements LocationListener {

    private class MsgDialogCallback implements MsgDialog.ReadyListener {

        String chalkID = "";
        ChalkPoster cp = null;
        FileOutputStream out = null;
        String url = "http://eitc.comze.com/chalk/upload_file.php";

        @Override
        public void ready(String chalk) {

            // image.caption = chalk;

            // Let user know this will take a little time
            Toast.makeText(OurVillage.this, "Posting to server...", Toast.LENGTH_LONG).show();

            cp = new ChalkPoster();

            // TODO mkm Last param is category. If unspecified, BlockChalk puts
            // chalk into 'chatter' category
            // chalkID = cp.post(chalk, lat, lon, chalkUser, "");

            if (lat.equals("") && lon.equals("")) {
                Location location = locationManager.getLastKnownLocation(bestProvider);
                lat = Double.toString(location.getLatitude());
                lon = Double.toString(location.getLongitude());
            }

            chalkID = cp.post(chalk, lat, lon, chalkUser, "Chatter");

            if (!chalkID.equals("")) {
                filename = Environment.getExternalStorageDirectory().toString() + "/"
                           + chalkID + ".jpg";
            } else {
                filename = "junk.jpg";
            }
            // if filename of picture was empty poste

            fileAndPostTask(filename);

            // Display the entered message
            Toast.makeText(OurVillage.this, chalk, Toast.LENGTH_LONG).show();
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
                    Toast.makeText(OurVillage.this, "Post Was Successful", Toast.LENGTH_SHORT)
                         .show();
                } else {
                    Toast
                         .makeText(OurVillage.this, "Error Posting to Site", Toast.LENGTH_LONG)
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

        /*
         * Post to site the chalk and image filename
         */
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

            Log.d(TAG, " MyLocation Listener>> Latitude: " + lat.toString());
            Log.d(TAG, " MyLocation Listener>> Longitude: " + lon.toString());

            /*
             * //Toast.makeText(getApplicationContext(),
             * "onLocationChanged Latitude:" + lat + "\nLongitude: " + lon,
             * Toast.LENGTH_SHORT).show();
             */

            // lat = Double.toString(df.format(location.getLatitude()));
            // lon = Double.toString(location.getLongitude());

            // String Text = "My current location is: " + "Latitude = " +
            // location.getLatitude() + "Longitude =" + location.getLongitude();
            // Toast.makeText(
            // getApplicationContext(),Text,Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
            // Toast.makeText(getApplicationContext(), "GPS Disabled",
            // Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
            // Toast.makeText(getApplicationContext(), "GPS Enabled",
            // Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
            Toast.makeText(getApplicationContext(), "OnStatusChanged Status: " + status,
                           Toast.LENGTH_SHORT).show();
        }

    }

    // Use the same chalk user
    private static final String chalkUser = "0add24dec4864bc68bc2211b6cea0810";

    private static final String TAG = "OurVillage";
    Button buttonClick;

    String curTime;

    FrameLayout frame;
    Image image = new Image();

    // Handles data for jpeg picture
    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {

            preview.camera.startPreview();

            // TODO mkm Some of this could be put in asynch threads
            // Dialog to get chalk message
            // TODO mkm would be nice to freeze image taken
            MsgDialog dialog = new MsgDialog(OurVillage.this, "", lat, lon, getPicTime(),
                                             source, new MsgDialogCallback());
            dialog.show();

            pictureBuffer = data.clone();
            lenPictureBuffer = data.length;
            Log.d(TAG, "onPictureTaken - jpeg");
        }

    };

    int lenPictureBuffer;

    TextView locate;

    LocationManager locationManager;

    byte[] pictureBuffer;

    SharedPreferences prefs;

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

    private String bestProvider;
    private final String CHALK_ID = "";
    private SQLiteDatabase db;
    private Dialog dialog;
    private String filename;

    private String lat = "";

    private String lon = "";

    private final int REQUEST_CODE = -1;

    private final String source = "Mobile";

    private final int STARTDELAY = 3500;

    public void getMyGPSLocation() {

        Criteria criteria = new Criteria();
        // criteria.setSpeedRequired(true);
        bestProvider = locationManager.getBestProvider(criteria, false);
        Log.d(TAG, "Best provider : " + bestProvider);

        LocationListener mlocListener = new MyLocationListener();
        String betterprovider = (bestProvider.equals("network") ? LocationManager.NETWORK_PROVIDER
                                                               : LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(betterprovider, 0, 0, mlocListener);

        // Location location =
        // locationManager.getLastKnownLocation(bestProvider);
        // filename = "IMAG" + System.currentTimeMillis() + ".jpg" ;
        // Log.d(TAG,"Filename: " + filename );

        /*
         * TODO mkm image.name = filename; image.latitude =
         * Double.toString(location.getLatitude()); image.longitude =
         * Double.toString(location.getLongitude()); image.caption =
         * "TEST CAPTION";
         */

        /*
         * lat = Double.toString(location.getLatitude()); lon =
         * Double.toString(location.getLongitude());
         */

        // Toast.makeText(this, "Latitude: "+ Double.toString(
        // location.getLatitude())+ " Longitude: " +
        // Double.toString(location.getLongitude()), Toast.LENGTH_SHORT).show();
        // return "Latitude: "+ Double.toString( location.getLatitude())+
        // "\nLongitude: " + Double.toString(location.getLongitude());

    }

    public void myLocation() {
        isGPSEnabled();
    }

    // Called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //
        // startSplashScreen();
        startActivity(new Intent(OurVillage.this, OurVillageSplashScreen.class));

        preview = new Preview(this);
        frame = (FrameLayout) findViewById(R.id.preview);
        frame.addView(preview);

        // Initialize shared preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(OurVillage.this);
        prefs.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
                //
            }
        });

        // Get Userid
        SharedPreferences id = getSharedPreferences(CHALK_ID, 0);
        if (id.equals("")) {
            // request for ID from block chalk
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("First Time: Request User ID?").setCancelable(false)
                   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           // Make GET Request for ID
                           // chalkUser = getChalkUserID();
                           // OurVillage.this.finish();
                       }

                       private String getChalkUserID() {
                           // GET
                           HttpClient httpclient = new DefaultHttpClient();

                           try {

                               // Prepare a request object
                               HttpGet httpget = new HttpGet(
                                                             getResources()
                                                                           .getString(
                                                                                      R.string.url_server)
                                                                                             + "user/new");

                               HttpResponse response = httpclient.execute(httpget);

                               HttpEntity entity = response.getEntity();

                               // HttpEntity entity = response.getEntity();

                           } catch (Exception e) {

                           }
                           return null;
                       }
                   }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           dialog.cancel();
                       }
                   });
            AlertDialog alert = builder.create();
            alert.show();

        }

        // locate = (TextView) findViewById(R.id.locate);
        // buttonExit = (Button) findViewById(R.id.buttonExit);
        buttonClick = (Button) findViewById(R.id.buttonClick);

        buttonClick.setOnClickListener(new OnClickListener() {
            // Handle camera shutter click
            public void onClick(View v) {

                // mkm Might call this at start up too to get GPS woken up
                myLocation();

                // Dialog and chalk post moved to jpegCallback
                preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);

                // locate.setText("Long: "+ image.longitude + "\nLat: " +
                // image.latitude );

                /*
                 * TODO figure this out //Insert into db boolean ispushed =
                 * pushintoDB(image); Log.d(TAG,
                 * " After PushintoDB: Inserted into database: "+ ispushed);
                 * 
                 * //Post the image on to website //TODO crashing
                 */
                // new PostImageTask().execute("http://flicker.com/...", null,
                // null);

                // mkm Need to restart preview mode after taking picture
                // preview.camera.startPreview();

            }

        });

        /*
         * buttonExit.setOnClickListener(new OnClickListener(){
         * 
         * @Override public void onClick(View v) { Log.d(TAG,
         * "Exiting the Mobile App"); onDestroy(); } });
         */
        // TODO why is this commented?
        // preview.camera.release();
    }

    // options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OurVillage.this.finish();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            // mkm Todo: verify these will get hit
            lat = Double.toString(location.getLatitude());
            lon = Double.toString(location.getLongitude());
        }
    }

    // ** Called when menu item is selected **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

        case R.id.status:
            startActivity(new Intent(this, Prefs.class));
            // Toast.makeText(this, "Settings Selected ",
            // Toast.LENGTH_SHORT).show();
            // Log.d(TAG, "Refresh Attempted Category: " + category +
            // " Displayno:  " + displayno );
            break;
        }
        return true;
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == 0) {
            String provider = Settings.Secure
                                             .getString(
                                                        getContentResolver(),
                                                        Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (provider != null) {
                Log.v(TAG, " Location providers: " + provider);
                // Start searching for location and update the
                // location text when update available.
                getMyGPSLocation();

            }
        }
    }

    private void buildAlertMessageNoGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
               .setMessage("Your GPS seems to be disabled, do you want to enable it?")
               .setCancelable(false)
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(
                                       @SuppressWarnings("unused") final DialogInterface dialog,
                                       @SuppressWarnings("unused") final int id) {
                       launchGPSOptions();
                   }

                   // GPS options
                   private void launchGPSOptions() {
                       // TODO Auto-generated method stub
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

    // get the time the picture was taken
    private String getPicTime() {
        Date dt = new Date();
        int hours = dt.getHours();
        int minutes = dt.getMinutes();
        int seconds = dt.getSeconds();
        return curTime = hours + ":" + minutes + ":" + seconds;
    }

    // check is GPS is enabled
    private void isGPSEnabled() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)
            || !locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)) {
            buildAlertMessageNoGPS();

        }
        getMyGPSLocation();
        if (lat == null && lon == null) {
            locationManager.getLastKnownLocation(bestProvider);
        }
    }

    // Storing the image data into database
    // NOT WORKING Since not implemented
    private boolean pushintoDB(Image imag) {
        try {
            ContentValues values = ImageLocationSQLHelper.ImageToContentValues(imag);
            // db.insertOrThrow(ImageLocations.IMAGE_TABLE_NAME, null, values);
            Toast.makeText(
                           this,
                           "Inserted into database:\n File: " + image.name + "\n Long: "
                                                           + image.longitude + "\n Lat: "
                                                           + image.latitude,
                           Toast.LENGTH_SHORT).show();
            // Log.d(TAG, " Inserted into DB Sucessfully");
        } catch (Exception e) {
            Log.e(TAG, "Problem Inserting an image data into database" + e);
            return false;
        }
        return true;
    }

    /*
     * private void startSplashScreen() {
     * 
     * Context mContext = getApplicationContext(); dialog = new
     * Dialog(mContext);
     * 
     * dialog.setContentView(R.layout.splash_screen);
     * dialog.setTitle("Custom Dialog");
     * 
     * // TextView text = (TextView) dialog.findViewById(R.id.text); //
     * text.setText("Welcome to Our Village!"); // ImageView image = (ImageView)
     * dialog.findViewById(R.id.image); //
     * image.setImageResource(R.drawable.icon);
     * 
     * new Handler().postDelayed(new Runnable() {
     * 
     * @Override public void run() {
     * 
     * // OurVillage.this.startActivity(new Intent(OurVillage.this, //
     * NewsReader.class)); // OurVillage.this.finish(); dialog.dismiss(); } },
     * STARTDELAY);
     * 
     * }
     */

}