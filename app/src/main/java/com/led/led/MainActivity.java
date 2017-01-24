package com.led.led;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothDevice;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.util.UUID;

public class MainActivity
        extends Activity
        implements View.OnClickListener, ColorPickerDialog.OnColorChangedListener, AdapterView.OnItemSelectedListener {
    String str = "";
    int rady = 4;
    int stlpce = 7;

    int count = 40;

    Button [] b = new Button[count];
    int bi=0;
    int rad=0;
    int stlpec=0;
    private Spinner spinner;
    private static final String[]paths = {"rad 1", "rad 2", "rad 3","rad 4", "rad 5"};
    private Spinner spinner1;
    private static final String[]paths1 = {"stlpec 1", "stlpec 2", "stlpec 3","stlpec 4", "stlpec 5", "stlpec 6","stlpec 7", "stlpec 8"};
    String address = null;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private ProgressDialog progress;
    public static String EXTRA_ADDRESS = "device_address";
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        for (int i = 0; i < 40; i++) {
            str = str + "000000";
        }

        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        //btSocket=getIntent().getSerializableExtra("btsorce");
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device


        //new ConnectBT().execute(); //Call the class to connect


        TableLayout Tlayout = new TableLayout(this);
        //Tlayout.setLayoutParams(new TableLayout.LayoutParams(4, 5));

        Tlayout.setPadding(5, 5, 5, 5);
        new ConnectBT().execute(); //Call the class to connect

        for (int f = 0; f <= rady; f++) {
            TableRow tr = new TableRow(this);
            for (int c = 0; c <= stlpce; c++) {
                b[bi] = new Button(this);
                b[bi].setId((8 * f + c));
                b[bi].setText("" + (8 * f + c));
                b[bi].setTextSize(10.0f);
                b[bi].setTextColor(Color.rgb(255, 255, 255));
                b[bi].setBackgroundColor(Color.rgb(0, 0, 0));
                b[bi].setOnClickListener(this);
                b[bi].setPadding(1,1,1,1);
                tr.addView(b[bi], 65, 60);
                bi++;
            } // for

            Tlayout.addView(tr);


        } // for


       // LL.addView(Tlayout);
       // super.setContentView(LL);
        LinearLayout linLayout = new LinearLayout(this);
        // specifying vertical orientation
        linLayout.setOrientation(LinearLayout.VERTICAL);
        // creating LayoutParams
        LinearLayout.LayoutParams linLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        // set LinearLayout as a root element of the screen
        //setContentView(linLayout, linLayoutParam);
        super.setContentView(linLayout, linLayoutParam);
        LinearLayout.LayoutParams lpView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        Button btn = new Button(this);
        btn.setText("Reset");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetAll();   //method to turn off
            }
        });

        LinearLayout.LayoutParams centerGravityParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        centerGravityParams.gravity = Gravity.CENTER;

        linLayout.addView(btn, centerGravityParams);
        linLayout.addView(Tlayout);



        TableLayout Tlayout1 = new TableLayout(this);
        //Tlayout.setLayoutParams(new TableLayout.LayoutParams(4, 5));
        Tlayout1.setPadding(5, 5, 5, 5);
        TableRow tr1 = new TableRow(this);



        spinner=new Spinner(this);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //spinner.setOnItemSelectedListener();
        tr1.addView(spinner);
        spinner.setOnItemSelectedListener(new RadSpinnerClass());

        Button btn2 = new Button(this);
        btn2.setText("Vyber farbu");
        btn2.setId((int) (200));
        btn2.setOnClickListener(this);
        tr1.addView(btn2);

        Tlayout1.addView(tr1);
        linLayout.addView(Tlayout1);




        TableLayout Tlayout2 = new TableLayout(this);
        //Tlayout.setLayoutParams(new TableLayout.LayoutParams(4, 5));
        Tlayout2.setPadding(5, 5, 5, 5);
        TableRow tr2 = new TableRow(this);



        spinner1=new Spinner(this);
        ArrayAdapter<String>adapter1 = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item,paths1);

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        //spinner.setOnItemSelectedListener();
        tr2.addView(spinner1);
        spinner1.setOnItemSelectedListener(new StlpecSpinnerClass());

        Button btn4 = new Button(this);
        btn4.setText("Vyber farbu");
        btn4.setId((201));
        btn4.setOnClickListener(this);



        tr2.addView(btn4);

        Tlayout2.addView(tr2);
        linLayout.addView(Tlayout2);




        Button btnClose = new Button(this);
        btnClose.setText("Odpojit");
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btSocket!=null) //If the btSocket is busy
                {
                    try
                    {
                        btSocket.close(); //close connection
                    }
                    catch (IOException e)
                    { msg("Error");}
                }
                finish(); //
            }
        });


        linLayout.addView(btnClose, centerGravityParams);




        //super.setContentView(layout);


        //lay.addView(layout);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    } // ()
    class StlpecSpinnerClass implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {

            stlpec=(int)position;
            Log.i("spin","stlpec " + stlpec);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    class RadSpinnerClass implements AdapterView.OnItemSelectedListener
    {
       public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
           rad=(int)position;
            Log.i("spin","rad " + rad);

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void onClick(View view) {
        ColorPickerDialog color = new ColorPickerDialog(this, this, "" + ((Button) view).getId(), Color.BLACK);
        color.show();
        Log.i("col", "" + str + "---" + str.length());
        Log.i("key", "" + ((Button) view).getId());

    }
    // class

    @Override
    public void colorChanged(String key, int color) {
        /*if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write(color.toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
*/


        String hexColor = String.format("%06X", (0xFFFFFF & color));
        Log.i("color", key);
        Button btn = (Button) findViewById(Integer.valueOf(key));
        if(Integer.valueOf(key)==200){
            //rad
            for(int i=0;i<8;i++){
                str = str.substring(0, (rad*8+i)*6) + hexColor + str.substring((rad*8+i)*6+6, str.length());
                Button btn11 = (Button) findViewById(rad*8+i);
                btn11.setBackgroundColor(color);

            }
        }
        else if(Integer.valueOf(key)==201){
            //stlpec
            for(int k=0;k<5;k++) {
                for (int i = 0; i < 40; i++) {

                    if(i==stlpec+k*8) {
                        str = str.substring(0, ( i) * 6) + hexColor + str.substring(( i) * 6 + 6, str.length());
                        Button btn22 = (Button) findViewById(i);
                        btn22.setBackgroundColor(color);

                    }
                }

            }
        }
        else {
            btn.setBackgroundColor(color);
            int start = Integer.valueOf(key) * 6;
            int end = Integer.valueOf(key) * 6 + 6;
            str = str.substring(0, start) + hexColor + str.substring(end, str.length());
        }
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(str.getBytes());
            } catch (IOException e) {
                Log.i("col", "Error");
            }
        }

    }
    private void ResetAll()
    {

        for(int i=0;i<40;i++){
            b[i].setBackgroundColor(Color.rgb(0, 0, 0));

        }


        str = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(str.getBytes());
            } catch (IOException e) {
                Log.i("col", "Error");
            }

        }
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.led.led/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.led.led/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(MainActivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            } else {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}
