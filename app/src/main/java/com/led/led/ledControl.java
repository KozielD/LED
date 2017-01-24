package com.led.led;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.UUID;


public class ledControl extends ActionBarActivity implements View.OnClickListener, ColorPickerDialog.OnColorChangedListener {





    Button btnOn, btnOff, btnDis,btnColor,btnAkt;
    Button btnMatrix;
    SeekBar brightness;
    String address = null;
    String str = "";
    int jas;
    int farba;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    public static String EXTRA_ADDRESS = "device_address";
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String trasnString;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        for (int i = 0; i < 40; i++) {
            str = str + "000000";
        }
        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device


        setContentView(R.layout.activity_led_control);


        //call the widgtes
        btnOn = (Button)findViewById(R.id.button2);
        btnOff = (Button)findViewById(R.id.button3);
        btnDis = (Button)findViewById(R.id.button4);
        btnColor = (Button)findViewById(R.id.button5);
        btnAkt = (Button)findViewById(R.id.button6);
        brightness = (SeekBar)findViewById(R.id.seekBar);
        btnColor.setId(202);
        btnColor.setOnClickListener(this);
        btnAkt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String hexColor = String.format("%06X", (0xFFFFFF & farba));

                Log.i("color", " " + farba);
                Log.i("color", " " + hexColor);


                Log.i("color", " r  " + charToNumJa(hexColor.charAt(0), hexColor.charAt(1)));
                Log.i("color", " g  " + charToNumJa(hexColor.charAt(2),hexColor.charAt(3)));
                Log.i("color", " b  " + charToNumJa(hexColor.charAt(4), hexColor.charAt(5)));


                double[] values =RGBtoHSV(charToNumJa(hexColor.charAt(0), hexColor.charAt(1)),charToNumJa(hexColor.charAt(2), hexColor.charAt(3)),charToNumJa(hexColor.charAt(4), hexColor.charAt(5)));
                Log.i("color", " h  " + values[0] +" s  " + values[1] +" v  " + values[2]);

                String rgbvalue=hsvToRgb((float)values[0],100,jas);

                Log.i("color", " RGB " + rgbvalue.toUpperCase() );
                str="";
                for (int i = 0; i < 40; i++) {
                    str =str  + rgbvalue.toUpperCase();
                }
                Log.i("color", " RGB " + str);
                try
                {
                    btSocket.getOutputStream().write(str.getBytes());
                }
                catch (IOException e)
                {

                }

            }
        });


        new ConnectBT().execute(); //Call the class to connect

        //commands to be sent to bluetooth
        btnOn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                turnOnLed();      //method to turn on
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                turnOffLed();   //method to turn off
            }
        });

        btnDis.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Disconnect(); //close connection
            }
        });






        brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser==true)
                {
                    Log.i("color", "jass   " + progress);
                    jas=progress;



                /*
                    try
                    {
                        btSocket.getOutputStream().write(str.getBytes());
                    }
                    catch (IOException e)
                    {

                    }
                    */
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish(); //return to the first layout

    }

    private void turnOffLed()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void turnOnLed()
    {


        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write(str.getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_led_control, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void colorChanged(String key, int color) {
        farba=color;
        Button btn = (Button) findViewById(Integer.valueOf(key));
        btn.setBackgroundColor(color);
        /*
        String hexColor = String.format("%06X", (0xFFFFFF & color));
        Log.i("color", key);
        Log.i("color", " " + color);
        Log.i("color", " " + hexColor);
        Button btn = (Button) findViewById(Integer.valueOf(key));
        btn.setBackgroundColor(color);

        Log.i("color", " r  " + charToNumJa(hexColor.charAt(0), hexColor.charAt(1)));
        Log.i("color", " g  " + charToNumJa(hexColor.charAt(2),hexColor.charAt(3)));
        Log.i("color", " b  " + charToNumJa(hexColor.charAt(4), hexColor.charAt(5)));


        double[] values =RGBtoHSV(charToNumJa(hexColor.charAt(0), hexColor.charAt(1)),charToNumJa(hexColor.charAt(2), hexColor.charAt(3)),charToNumJa(hexColor.charAt(4), hexColor.charAt(5)));
        Log.i("color", " h  " + values[0] +" s  " + values[1] +" v  " + values[2]);

        String rgbvalue=hsvToRgb((float)values[0],100,jas);

        Log.i("color", " RGB " + rgbvalue.toUpperCase() );
        str="";
        for (int i = 0; i < 40; i++) {
            str =str  + rgbvalue.toUpperCase();
        }
        Log.i("color", " RGB " + str);
        try
        {
            btSocket.getOutputStream().write(str.getBytes());
        }
        catch (IOException e)
        {

        }

        */
    }

    @Override
    public void onClick(View view) {
        ColorPickerDialog color = new ColorPickerDialog(this, this, "" + ((Button) view).getId(), Color.BLACK);
        color.show();
    }


    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(ledControl.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                 myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                 BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                 btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                 BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                 btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }






    public static double[] RGBtoHSV(double r, double g, double b){

        double h, s, v;

        double min, max, delta;

        min = Math.min(Math.min(r, g), b);
        max = Math.max(Math.max(r, g), b);

        // V
        v = max;

        delta = max - min;

        // S
        if( max != 0 )
            s = delta / max;
        else {
            s = 0;
            h = -1;
            return new double[]{h,s,v};
        }

        // H
        if( r == max )
            h = ( g - b ) / delta; // between yellow & magenta
        else if( g == max )
            h = 2 + ( b - r ) / delta; // between cyan & yellow
        else
            h = 4 + ( r - g ) / delta; // between magenta & cyan

        h *= 60;    // degrees

        if( h < 0 )
            h += 360;

        return new double[]{h,s,v};
    }


    public static String hsvToRgb(float H, float S, float V) {

        float R, G, B;

        H /= 360f;
        S /= 100f;
        V /= 100f;

        if (S == 0)
        {
            R = V * 255;
            G = V * 255;
            B = V * 255;
        } else {
            float var_h = H * 6;
            if (var_h == 6)
                var_h = 0; // H must be < 1
            int var_i = (int) Math.floor((double) var_h); // Or ... var_i =
            // floor( var_h )
            float var_1 = V * (1 - S);
            float var_2 = V * (1 - S * (var_h - var_i));
            float var_3 = V * (1 - S * (1 - (var_h - var_i)));

            float var_r;
            float var_g;
            float var_b;
            if (var_i == 0) {
                var_r = V;
                var_g = var_3;
                var_b = var_1;
            } else if (var_i == 1) {
                var_r = var_2;
                var_g = V;
                var_b = var_1;
            } else if (var_i == 2) {
                var_r = var_1;
                var_g = V;
                var_b = var_3;
            } else if (var_i == 3) {
                var_r = var_1;
                var_g = var_2;
                var_b = V;
            } else if (var_i == 4) {
                var_r = var_3;
                var_g = var_1;
                var_b = V;
            } else {
                var_r = V;
                var_g = var_1;
                var_b = var_2;
            }

            R = var_r * 255; // RGB results from 0 to 255
            G = var_g * 255;
            B = var_b * 255;
        }

        String rs = Integer.toHexString((int) (R));
        String gs = Integer.toHexString((int) (G));
        String bs = Integer.toHexString((int) (B));

        if (rs.length() == 1)
            rs = "0" + rs;
        if (gs.length() == 1)
            gs = "0" + gs;
        if (bs.length() == 1)
            bs = "0" + bs;
        return  rs + gs + bs;
    }

    public static int charToNumJa(char ityChar1,char ityChar2)
    {
        int cislo1;

        if (ityChar1 < 'A')
            cislo1 = (ityChar1 - '0') * 16;
        else
            cislo1 = (ityChar1 - 'A' + 10) * 16;

        if (ityChar2 < 'A')
            cislo1 += (ityChar2 - '0');
        else
            cislo1 += (ityChar2 - 'A' + 10);

        return cislo1;
    }

}

