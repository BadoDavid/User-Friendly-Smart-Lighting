package hu.bme.mit.ufsmartlighting;

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


public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";

    public static final String EXTRA_DEVICE_NAME = "extra.device_name";
    public static final String EXTRA_DEVICE_IPADDR = "extra.device_ipaddr";
    public static final String EXTRA_DEVICE_PORT = "extra.device_port";

    private int redValue = 0x00;
    private int greenValue = 0x00;
    private int blueValue = 0x00;

    private String device;

    private String ipAddr;
    private int udpPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        device = getIntent().getStringExtra(EXTRA_DEVICE_NAME);
        ipAddr = getIntent().getStringExtra(EXTRA_DEVICE_IPADDR);
        udpPort = getIntent().getIntExtra(EXTRA_DEVICE_PORT, -1);

        getSupportActionBar().setTitle(device);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SeekBar sbRed = findViewById(R.id.seekBarRed);

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

        SeekBar sbGreen = findViewById(R.id.seekBarGreen);

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

        SeekBar sbBlue = findViewById(R.id.seekBarBlue);

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
