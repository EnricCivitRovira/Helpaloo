package health.tueisDeveloper.helpaloo.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import health.tueisDeveloper.helpaloo.R;
import java.util.Objects;

public class ContactUs extends AppCompatActivity {

    private String versionNumber;
    private int versionCode;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contact_us);
        Objects.requireNonNull(getSupportActionBar()).hide();
        Button termsOfUse = findViewById(R.id.termsOfUseButton);
        TextView version = findViewById(R.id.version);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            versionNumber = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        version.setText("Tu versión de Helpaloo es: "+versionNumber+ " "+versionCode);

        termsOfUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://helpaloo.flycricket.io/privacy.html"));
                startActivity(browserIntent);
            }
        });



    }
}
