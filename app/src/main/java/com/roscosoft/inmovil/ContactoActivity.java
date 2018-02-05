package com.roscosoft.inmovil;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Hp on 17/10/2017.
 */

public class ContactoActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);
        View.OnClickListener llamar = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+((TextView)view).getText()));
                startActivity(callIntent);
            }
        };
        findViewById(R.id.lblTelf1).setOnClickListener(llamar);
        findViewById(R.id.lblTelf2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto",((TextView)view).getText().toString(), null));
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Quiero registrar mi inmueble");
                    intent.putExtra(Intent.EXTRA_TEXT, "Hola quiero publicar en inmovil!");
                    startActivity(Intent.createChooser(intent, "Choose an Email client :"));
                }catch (Exception e){
                    Log.i("rosco", e.getMessage());
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("text", ((TextView)view).getText().toString());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(ContactoActivity.this, "Correo copiado al portapapeles", Toast.LENGTH_LONG).show();
                }
            }

        });
    }
}
