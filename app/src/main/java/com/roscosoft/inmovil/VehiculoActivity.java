package com.roscosoft.inmovil;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

/**
 * Created by Ricardo Ruiz on 29/9/2017.
 */

public class VehiculoActivity extends AppCompatActivity {
    ViewPager viewPager;
    RelativeLayout galeria;
    ScrollView scrollView;
    private boolean zoomOut =  false;
    private int tamanoOriginal = 200, altoPantalla;
    TextView lblNumFoto;
    Vehiculo auto;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_auto);

        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= 21){
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.negro));
        }

        Toolbar actionBarToolbar = (Toolbar)findViewById(R.id.mi_toolbar);
        setSupportActionBar(actionBarToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        scrollView = (ScrollView)findViewById(R.id.scrollView) ;
        galeria = (RelativeLayout)findViewById(R.id.galeria);
        lblNumFoto = (TextView)findViewById(R.id.lblNumFoto);


        // Imagenes!
        viewPager = (ViewPager)galeria.findViewById(R.id.pager);
        auto = (Vehiculo) getIntent().getSerializableExtra("auto");

        if (auto.fotos.size() == 0)
            lblNumFoto.setVisibility(View.GONE);
        lblNumFoto.setText("1/"+auto.fotos.size());

        if (auto.tipo == 2){ // para motos ocultar algunos campos
            findViewById(R.id.icnPuertas).setVisibility(View.GONE);
            findViewById(R.id.lblPuertas).setVisibility(View.GONE);

            View icnCaja = findViewById(R.id.icnCaja);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) icnCaja.getLayoutParams();
            params.removeRule(RelativeLayout.BELOW);
            params.addRule(RelativeLayout.BELOW, R.id.lblPrecio);
            icnCaja.setLayoutParams(params);

            findViewById(R.id.lblTitComb).setVisibility(View.GONE);
            findViewById(R.id.lblComb).setVisibility(View.GONE);

            findViewById(R.id.lblTitTrac).setVisibility(View.GONE);
            findViewById(R.id.lblTraccion).setVisibility(View.GONE);
        }

        ((TextView)findViewById(R.id.lblDescripcion)).setText(auto.marca+" "+auto.modelo+" "+auto.ano);
        ((TextView)findViewById(R.id.lblPrecio)).setText(String.format(Locale.getDefault(), "%s %,.2f", auto.moneda, auto.precio));
        ((TextView)findViewById(R.id.lblPuertas)).setText(auto.puertas+" puertas");
        String caja = "M";
        if (auto.caja == 1){ // 1 manual, 2 automatica, 3 secuencial
            caja = "Caja Manual";
        }else if(auto.caja == 2){
            caja = "Caja Automática";
        }else{
            caja = "Caja Secuencial";
        }
        ((TextView)findViewById(R.id.lblCaja)).setText(caja);
        ((TextView)findViewById(R.id.lblKilom)).setText(String.format(Locale.getDefault(), "%,d km. recorridos", auto.kilometraje));

        ((TextView)findViewById(R.id.lblDetalle)).setText(auto.detalle);
        ((TextView)findViewById(R.id.lblMarca)).setText(auto.marca);
        ((TextView)findViewById(R.id.lblMod)).setText(auto.modelo);
        ((TextView)findViewById(R.id.lblAno)).setText(auto.ano+"");
        ((TextView)findViewById(R.id.lblCil)).setText(String.format(Locale.getDefault(), "%,.2f %s", auto.cilindrada, auto.unidadCil));
        ((TextView)findViewById(R.id.lblColor)).setText(auto.color);
        ((TextView)findViewById(R.id.lblComb)).setText(auto.combustible);
        ((TextView)findViewById(R.id.lblTraccion)).setText(auto.traccion);

        if (!auto.telefono.trim().equals("") && !auto.telefono1.trim().equals(""))
            ((TextView)findViewById(R.id.lblTitTelf)).setText("Telefonos");
        else if(auto.telefono.trim().equals("") && auto.telefono1.trim().equals("")){
            findViewById(R.id.lblTitTelf).setVisibility(View.GONE);
            findViewById(R.id.lblTelf1).setVisibility(View.GONE);
            findViewById(R.id.lblTelf2).setVisibility(View.GONE);
        }
        else
            ((TextView)findViewById(R.id.lblTitTelf)).setText("Telefono");

        View.OnClickListener llamar = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+((TextView)view).getText()));
                startActivity(callIntent);
            }
        };
        if (!auto.telefono.trim().equals("")){
            TextView telf1 = (TextView)findViewById(R.id.lblTelf1);
            SpannableString content = new SpannableString(auto.telefono);
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            telf1.setText(content);
            telf1.setOnClickListener(llamar);
        }else{
            findViewById(R.id.lblTelf1).setVisibility(View.GONE);
        }

        if (!auto.telefono1.trim().equals("")){
            TextView telf2 = (TextView)findViewById(R.id.lblTelf2);
            SpannableString content = new SpannableString(auto.telefono1);
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            telf2.setText(content);
            telf2.setOnClickListener(llamar);
        }else{
            findViewById(R.id.lblTelf2).setVisibility(View.GONE);
        }

        galeria.post(new Runnable() {
            @Override
            public void run() {
                tamanoOriginal = galeria.getHeight();
                altoPantalla = scrollView.getHeight();
            }
        });
        ImgsPagerAdapter pagerAdapter = new ImgsPagerAdapter(this, auto.fotos);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            private float pointX;
            private float pointY;
            private int tolerance = 50;
            private int distanciaMinima = 15;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        pointX = motionEvent.getX();
                        pointY = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        boolean sameX = pointX + tolerance > motionEvent.getX() && pointX - tolerance < motionEvent.getX();
                        boolean sameY = pointY + tolerance > motionEvent.getY() && pointY - tolerance < motionEvent.getY();
                        if(sameX && sameY){
                            //The user "clicked" certain point in the screen or just returned to the same position an raised the finger
                            if(zoomOut) {
                                zoomIn();
                            }else{
                                zoomOut();
                            }
                        }

                        break;
                }
                return false;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                lblNumFoto.setText((position+1)+"/"+auto.fotos.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY();
                if (scrollY > 100 && zoomOut){
                    zoomIn();
                }
            }
        });
        // Esto anima el layout!
        RelativeLayout p = (RelativeLayout) galeria.getParent();
        LayoutTransition layoutTransition = p.getLayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
    }

    void zoomIn(){
        galeria.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, tamanoOriginal));
        zoomOut = false;
    }
    void zoomOut(){
        galeria.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, altoPantalla));
        scrollView.setScrollY(0);
        zoomOut = true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
}
