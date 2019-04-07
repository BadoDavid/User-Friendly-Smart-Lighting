package hu.bme.mit.ufsmartlighting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.SeekBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import hu.bme.mit.ufsmartlighting.device.DeviceAdapter;
import hu.bme.mit.ufsmartlighting.device.DeviceItem;


public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";

    public static final String EXTRA_DEVICE_POSITION = "extra.device_position";
    public static final String EXTRA_DEVICE_NAME = "extra.device_name";
    public static final String EXTRA_DEVICE_STATE = "extra.device_state";
    public static final String EXTRA_DEVICE_IPADDR = "extra.device_ipaddr";
    public static final String EXTRA_DEVICE_PORT = "extra.device_port";

    private int redValue = 0x00;
    private int greenValue = 0x00;
    private int blueValue = 0x00;

    Long ledValue =  new Long(0);

    private String device;

    private int itemPos;

    private String ipAddr;
    private int udpPort;

    OnDeviceStateChangedListener listener;

    public interface OnDeviceStateChangedListener {

        void OnDeviceStateChanged(int pos, Long number);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        itemPos = getIntent().getIntExtra(EXTRA_DEVICE_POSITION, -1);
        device = getIntent().getStringExtra(EXTRA_DEVICE_NAME);
        ledValue = getIntent().getLongExtra(EXTRA_DEVICE_STATE, 0x000000);
        ipAddr = getIntent().getStringExtra(EXTRA_DEVICE_IPADDR);
        udpPort = getIntent().getIntExtra(EXTRA_DEVICE_PORT, -1);

        /* Parse ledValue */
        redValue = (int) ((ledValue & 0xFF0000) >> 16);
        greenValue = (int) ((ledValue & 0x00FF00) >> 8);
        blueValue = (int) (ledValue & 0x0000FF);

        getSupportActionBar().setTitle(device);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SeekBar sbRed = findViewById(R.id.seekBarRed);
        sbRed.setProgress(redValue);

        SeekBar sbGreen = findViewById(R.id.seekBarGreen);
        sbGreen.setProgress(greenValue);

        SeekBar sbBlue = findViewById(R.id.seekBarBlue);
        sbBlue.setProgress(blueValue);


        sbRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) { }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                redValue = seekBar.getProgress();
                onDeviceChanged('R', seekBar.getProgress());
            }
        });

        sbGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                greenValue = seekBar.getProgress();
                onDeviceChanged('G', seekBar.getProgress());
            }
        });

        sbBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                blueValue = seekBar.getProgress();
                onDeviceChanged('B', seekBar.getProgress());
            }
        });

        /*
        ToggleButton btnRed = findViewById(R.id.btnRed);
        btnRed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    onDeviceChanged('R', 0);
                }
                else
                {
                    onDeviceChanged('R', 128);
                }
            }
        });
        ToggleButton btnBlue = findViewById(R.id.btnBlue);

        btnBlue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    onDeviceChanged('B', 0);
                }
                else
                {
                    onDeviceChanged('B', 128);
                }
            }
        });

        ToggleButton btnGreen = findViewById(R.id.btnGreen);

        btnGreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    onDeviceChanged('G', 0);
                }
                else
                {
                    onDeviceChanged('G', 128);
                }
            }
        });
        */
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onDeviceChanged(final char color, final int number) {

        ledValue = ((long)(redValue) << 16) | ((long)(greenValue) << 8) | (long)(blueValue);

        //Intent intent = new Intent();
        //intent.putExtra(EXTRA_DEVICE_POSITION, itemPos);
        //intent.putExtra(EXTRA_DEVICE_STATE, ledValue);
        //setResult(MainActivity.DEVICE_STATE_REQUEST, intent);
        //finish();//finishing activity
        //listener.OnDeviceStateChanged( itemPos,ledValue);
        MainActivity.OnDeviceStateChanged(itemPos, ledValue);

        new Thread() {
            public void run() {

                DatagramSocket udpSocket = null;
                InetAddress local = null;

                try
                {
                    //String messageStr = color + String.valueOf(number);

                    udpSocket = new DatagramSocket();
                    local = InetAddress.getByName(ipAddr);

                    JSONObject object = new JSONObject();

                    object.put("type", "Controll");
                    object.put("red", redValue);
                    object.put("green", greenValue);
                    object.put("blue", blueValue);

                    System.out.print(object.toString());

                    String stringMsg =  object.toString();

                    byte[] msg = stringMsg.getBytes();

                    System.out.println(msg.length);

                    //DatagramPacket p = new DatagramPacket(message, message.length,local,udpPort);

                    DatagramPacket p = new DatagramPacket(msg, msg.length, local, udpPort);

                    udpSocket.send(p);
                }
                catch(IOException e) {
                    System.out.println(e.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
