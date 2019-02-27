package ir.androidlife.simplespeedmeter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Formatter;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    SwitchCompat sw_metric;
    TextView tv_speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sw_metric = findViewById(R.id.sw_metric);
        tv_speed = findViewById(R.id.tv_speed);

        // Check for gps permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else {

            // Start the program if gps permission is OK
            doStuff();
        }

        this.updateSpeed(null);

        sw_metric.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                MainActivity.this.updateSpeed(null);
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null) {
            CLocation myLocation = new CLocation(location, this.useMetricUnits());
            this.updateSpeed(myLocation);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @SuppressLint("MissingPermission")
    private void doStuff() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        Toast.makeText(this, "منتظر فعال شدن جی پی اس", Toast.LENGTH_SHORT).show();
    }

    private void updateSpeed(CLocation location) {
        float nCurrentSpeed = 0;

        if(location != null) {
            location.setUseMetricUnits(this.useMetricUnits());
            nCurrentSpeed = location.getSpeed();
        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(" ", "0");

        if (this.useMetricUnits()) {
            tv_speed.setText(strCurrentSpeed + " km/h");
        } else {
            tv_speed.setText(strCurrentSpeed + " miles/h");
        }
    }

    private boolean useMetricUnits() {
        return sw_metric.isChecked();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doStuff();
            } else {
                finish();
            }
        }
    }
}
