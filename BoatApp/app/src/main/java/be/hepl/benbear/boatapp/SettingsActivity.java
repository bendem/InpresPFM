package be.hepl.benbear.boatapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {
    SharedPreferences settings;
    EditText ip;
    EditText port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = getSharedPreferences(getString(R.string.config_file), 0);
        final SharedPreferences.Editor editor = settings.edit();

        ip = (EditText)findViewById(R.id.editTextServerIp);
        port = (EditText)findViewById(R.id.editTextServerPort);

        ip.setText(settings.getString("server_ip", "0.0.0.0"));
        port.setText(Integer.toString(settings.getInt("server_port", 30000)));

        Button b = (Button)this.findViewById(R.id.buttonSave);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipValue = ip.getText().toString();
                int portValue = Integer.parseInt(port.getText().toString());
                editor.putString("server_ip", ipValue);
                editor.putInt("server_port", portValue);
                editor.commit();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }
}
