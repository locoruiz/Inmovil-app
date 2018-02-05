package com.roscosoft.inmovil;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Ricardo Ruiz on 29/9/2017.
 */

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CasaActivity extends AppCompatActivity implements OnMapReadyCallback {
    ViewPager viewPager;
    RelativeLayout galeria;
    ScrollView scrollView;
    private boolean zoomOut =  false;
    private int tamanoOriginal = 200, altoPantalla;
    TextView lblNumFoto;
    Casa casa;
    private GoogleMap mMap;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_casa);

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
        casa = (Casa)getIntent().getSerializableExtra("casa");

        if (casa.fotos.size() == 0)
            lblNumFoto.setVisibility(View.GONE);
        lblNumFoto.setText("1/"+casa.fotos.size());

        ((TextView)findViewById(R.id.lblDescripcion)).setText(casa.descripcion);
        ((TextView)findViewById(R.id.lblPrecio)).setText(String.format(Locale.getDefault(), "%s %,.2f", casa.moneda, casa.precio));
        ((TextView)findViewById(R.id.lblSupCons)).setText(String.format(Locale.getDefault(), "%,.2f %s Sup. construida", casa.supC, casa.unidadSupCon));
        ((TextView)findViewById(R.id.lblSupTot)).setText(String.format(Locale.getDefault(), "%,.2f %s Sup. total", casa.supT, casa.unidadSup));

        ((TextView)findViewById(R.id.lblCuartos)).setText((casa.cuartos == 1) ? "1 cuarto" : casa.cuartos+" cuartos");
        ((TextView)findViewById(R.id.lblBanos)).setText((casa.banos == 1) ? "1 baño" : casa.banos+" baños");
        ((TextView)findViewById(R.id.lblPisos)).setText((casa.pisos == 1) ? "1 piso" : casa.pisos+" pisos");
        ((TextView)findViewById(R.id.lblDireccion)).setText(casa.direccion);
        ((TextView)findViewById(R.id.lblBarrio)).setText(casa.barrio);
        ((TextView)findViewById(R.id.lblZona)).setText(casa.zona);
        ((TextView)findViewById(R.id.lblDetalle)).setText(casa.detalle);

        if (casa.barrio.trim().equals("")){
            ((RelativeLayout)findViewById(R.id.lblBarrio).getParent()).setVisibility(View.GONE);
        }
        if (casa.zona.trim().equals("")){
            ((RelativeLayout)findViewById(R.id.lblZona).getParent()).setVisibility(View.GONE);
        }
        if (!casa.telefono.trim().equals("") && !casa.telefono1.trim().equals(""))
            ((TextView)findViewById(R.id.lblTitTelf)).setText("Telefonos");
        else if(casa.telefono.trim().equals("") && casa.telefono1.trim().equals("")){
            findViewById(R.id.lblTitTelf).setVisibility(View.GONE);
            findViewById(R.id.lblTelf1).setVisibility(View.GONE);
            findViewById(R.id.lblTelf2).setVisibility(View.GONE);
        }
        else
            ((TextView)findViewById(R.id.lblTitTelf)).setText("Telefono");


        if (casa.tipo == 4){
            findViewById(R.id.icnSupCon).setVisibility(View.GONE);
            findViewById(R.id.lblSupCons).setVisibility(View.GONE);
            findViewById(R.id.icnCuartos).setVisibility(View.GONE);
            findViewById(R.id.lblCuartos).setVisibility(View.GONE);
            findViewById(R.id.icnBanos).setVisibility(View.GONE);
            findViewById(R.id.lblBanos).setVisibility(View.GONE);
            findViewById(R.id.icnPisos).setVisibility(View.GONE);
            findViewById(R.id.lblPisos).setVisibility(View.GONE);

            View icnSupTot = findViewById(R.id.icnSuptot);
            View lblSupTot = findViewById(R.id.lblSupTot);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) icnSupTot.getLayoutParams();
            params.removeRule(RelativeLayout.BELOW);
            params.addRule(RelativeLayout.BELOW, R.id.lblPrecio);
            icnSupTot.setLayoutParams(params);

            View ubi = findViewById(R.id.recuadroContenedor);
            params = (RelativeLayout.LayoutParams) ubi.getLayoutParams();
            params.removeRule(RelativeLayout.BELOW);
            params.addRule(RelativeLayout.BELOW, R.id.lblSupTot);
            ubi.setLayoutParams(params);

            lblSupTot.setBackground(null);
        }

        View.OnClickListener llamar = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+((TextView)view).getText()));
                startActivity(callIntent);
            }
        };
        if (!casa.telefono.trim().equals("")){
            TextView telf1 = (TextView)findViewById(R.id.lblTelf1);
            SpannableString content = new SpannableString(casa.telefono);
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            telf1.setText(content);
            telf1.setOnClickListener(llamar);
        }else{
            findViewById(R.id.lblTelf1).setVisibility(View.GONE);
        }

        if (!casa.telefono1.trim().equals("")){
            TextView telf2 = (TextView)findViewById(R.id.lblTelf2);
            SpannableString content = new SpannableString(casa.telefono1);
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
        ImgsPagerAdapter pagerAdapter = new ImgsPagerAdapter(this, casa.fotos);
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
                lblNumFoto.setText((position+1)+"/"+casa.fotos.size());
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

        if (casa.latitud != 0 && casa.longitud != 0){
            // Cargar Mapa
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }else{
            findViewById(R.id.map).setVisibility(View.GONE);
        }
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setAllGesturesEnabled(false);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(casa.latitud, casa.longitud), 16));
        mMap.addMarker(new MarkerOptions().position(new LatLng(casa.latitud, casa.longitud)));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=%d&q=%f,%f (%s)",
                        casa.latitud, casa.longitud, 16, casa.latitud, casa.longitud, casa.descripcion);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
    }
}
