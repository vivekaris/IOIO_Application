package org.easytrainer.ioioapplication;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.Toolbar;
import ioio.lib.api.IOIO;
import ioio.lib.api.IOIO.VersionType;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import ioio.lib.api.IOIOConnection;


public class MainActivity extends IOIOActivity {

    private ToggleButton togDigit0;
    private BluetoothAdapter BA;
    private TextView tw;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        togDigit0= (ToggleButton) findViewById(R.id.toggleButton);
        BA = BluetoothAdapter.getDefaultAdapter();
        tw=(TextView) findViewById(R.id.textView2);


        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
           startActivityForResult(turnOn, 0);
            Toast.makeText(MainActivity.this,"Turned on"
                    ,Toast.LENGTH_LONG).show();

        }
        else{
            Toast.makeText(MainActivity.this,"Already on",Toast.LENGTH_SHORT).show();
        }
    } //  end of on create


    class Looper extends BaseIOIOLooper {

        private DigitalOutput digi0;
        private AnalogInput volt;
        private float f;


        @Override
        protected void setup() throws ConnectionLostException, InterruptedException {
           // super.setup();

            digi0= ioio_.openDigitalOutput(0,false);
            volt=ioio_.openAnalogInput(42);
            enableUi(true);
            showVersions(ioio_, "Connected firmware version!");

        }

        @Override
        public void loop() throws ConnectionLostException, InterruptedException {

            float voltage= (float) (5.0/1023.0);
            f = volt.read();//*voltage;

           setNumber(f*10);
           digi0.write(!togDigit0.isChecked());
           Thread.sleep(500);

            toast("Reading..... ");
            Log.v("Value of current",String.valueOf(f));
        }

        @Override
        public void disconnected() {
            enableUi(false);
            toast("IOIO disconnected");
        }

        @Override
        public void incompatible() {
            //super.incompatible();
           // Toast.makeText(MainActivity.this,"IOIO Incomptible dicconnecting", Toast.LENGTH_LONG).show();
            showVersions(ioio_, "Incompatible firmware version!");
        }
    }  //end of looper

    protected IOIOLooper createIOIOLooper()
    {
        return new Looper();
    }

    private void enableUi(final boolean enable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                         tw.setEnabled(enable);
                         togDigit0.setEnabled(enable);

            }
        });
    }

    private void showVersions(IOIO ioio, String title) {
        toast(String.format("%s\n" +
                        "IOIOLib: %s\n" +
                        "Application firmware: %s\n" +
                        "Bootloader firmware: %s\n" +
                        "Hardware: %s",
                title,
                ioio.getImplVersion(VersionType.IOIOLIB_VER),
                ioio.getImplVersion(VersionType.APP_FIRMWARE_VER),
                ioio.getImplVersion(VersionType.BOOTLOADER_VER),
                ioio.getImplVersion(VersionType.HARDWARE_VER)));
    }
    private void toast(final String message) {
        final Context context = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }
    private void setNumber(float f) {
        final String str = String.format("%.2f", f);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tw.setText("Reading Volts :"+str+"V");
            }
        });
    }
}
