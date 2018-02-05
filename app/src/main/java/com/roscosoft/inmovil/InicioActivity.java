package com.roscosoft.inmovil;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class InicioActivity extends AppCompatActivity implements HttpPost.HttpPostInterface{

    int margenBotonesMedios = 0;

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        HashMap<String, String> map = new HashMap<>();
        map.put("funcion", "maximos");
        try{
            String postString = HttpPost.getPostDataString(map);
            HttpPost post = new HttpPost(getString(R.string.url)+"servicios.php", postString, this);
            post.execute();
        }catch (UnsupportedEncodingException e){
            Log.i("rosco", e.getMessage());
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView titluo = (TextView)findViewById(R.id.titulo);
        titluo.setText(toolbar.getTitle());


        final ImageButton btnCasas = (ImageButton)findViewById(R.id.btnCasas);
        final ImageButton btnDepas = (ImageButton)findViewById(R.id.btnDepas);
        final ImageButton btnOfis = (ImageButton)findViewById(R.id.btnOfis);
        final ImageButton btnTerrenos = (ImageButton)findViewById(R.id.btnterrenos);
        final ImageButton btnAutos = (ImageButton)findViewById(R.id.btnAutos);

        final View parent = (View) btnCasas.getParent();

        final View contOfis = findViewById(R.id.contOfis);
        final View contTerrenos = findViewById(R.id.contTerrenos);
        final View contAutos = findViewById(R.id.contAutos);

        View.OnTouchListener onTouchListener = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageButton view = (ImageButton ) v;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        view.setColorFilter(Color.argb(100, 200, 200, 100));
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        if (view == btnAutos){
                            Intent i = new Intent(InicioActivity.this, CasasSubActivity.class);
                            i.putExtra("tipo", 5);
                            startActivity(i);
                        }else if(view == btnCasas){
                            /*
                            float ancho = btnCasas.getWidth();
                            float xDest = parent.getX() + parent.getWidth()/2 - ancho/2;
                            float yDest = convertDpToPixel(30, InicioActivity.this);
                            Log.i("rosco", "30dp = "+yDest);

                            TranslateAnimation translateAnimation = new TranslateAnimation(0, xDest - btnCasas.getX(), 0, yDest - btnCasas.getY());
                            translateAnimation.setDuration(500);

                            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    Intent i = new Intent(InicioActivity.this, CasasSubActivity.class);
                                    startActivity(i);
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            btnCasas.startAnimation(translateAnimation);
                            */
                            Intent i = new Intent(InicioActivity.this, CasasSubActivity.class);
                            i.putExtra("tipo", 1);
                            startActivity(i);
                        }else if(view == btnDepas){
                            Intent i = new Intent(InicioActivity.this, CasasSubActivity.class);
                            i.putExtra("tipo", 2);
                            startActivity(i);
                        }else if(view == btnOfis){
                            Intent i = new Intent(InicioActivity.this, CasasSubActivity.class);
                            i.putExtra("tipo", 3);
                            startActivity(i);
                        }else if(view == btnTerrenos){
                            Intent i = new Intent(InicioActivity.this, CasasSubActivity.class);
                            i.putExtra("tipo", 4);
                            startActivity(i);
                        }
                    case MotionEvent.ACTION_CANCEL: {
                        view.clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return true;
            }
        };
        btnCasas.setOnTouchListener(onTouchListener);
        btnAutos.setOnTouchListener(onTouchListener);
        btnDepas.setOnTouchListener(onTouchListener);
        btnOfis.setOnTouchListener(onTouchListener);
        btnTerrenos.setOnTouchListener(onTouchListener);
        final Button btnInfo = (Button)findViewById(R.id.btnInfo);
        final View linea = findViewById(R.id.linea);

        toolbar.setVisibility(View.INVISIBLE);
        btnInfo.setVisibility(View.INVISIBLE);
        linea.setVisibility(View.INVISIBLE);



        parent.post(new Runnable() {
            @Override
            public void run() {

                final PointF origen = new PointF(parent.getX(), parent.getY());
                origen.y += toolbar.getHeight();
                final float anchoP = (float)parent.getWidth();
                final float altoP = (float)parent.getHeight();
                final long duracion = 1000;

                // Animar casas **************************************************************
                final Animation fadeIn = new AlphaAnimation(0, 1);
                
                float x = btnCasas.getX();
                float y = btnCasas.getY();
                float ancho = btnCasas.getWidth();
                float alto = btnCasas.getHeight();

                final float margen = (anchoP - 2*ancho) / 3;
                // Casas va arriba a la izquierda
                final PointF origenCasas = new PointF(origen.x + margen, origen.y + 100);

                final TranslateAnimation mover = new TranslateAnimation(0, origenCasas.x - x, 0, origenCasas.y - y);
                final float moverCasasX = origenCasas.x - x;
                final float moverCasasY = origenCasas.y - y;

                // Animar departamentos **************************************************************

                x = btnDepas.getX();
                y = btnDepas.getY();
                ancho = btnDepas.getWidth();
                alto = btnDepas.getHeight();
                // Depas va arriba a la derecha
                final PointF origenDepas = new PointF(origen.x + anchoP - margen - ancho, origen.y + 100);

                final TranslateAnimation moverDepas = new TranslateAnimation(0, origenDepas.x - x, 0, origenDepas.y - y);

                // Animar Autos ***********************************************************
                x = contAutos.getX();
                y = contAutos.getY();
                ancho = contAutos.getWidth();
                alto = contAutos.getHeight();
                // autos va al medio abajo
                final PointF origenAutos = new PointF(origen.x + anchoP/2 - ancho/2, btnInfo.getY() - alto);

                final TranslateAnimation moverAutos = new TranslateAnimation(0, origenAutos.x - x, 0, origenAutos.y - y);


                // Animar Oficinas ***********************************************************
                x = contOfis.getX();
                y = contOfis.getY();
                ancho = contOfis.getWidth();
                alto = contOfis.getHeight();
                // Oficinas va al medio a la izquierda

                final PointF origenOfis = new PointF(origen.x + margen, (origenCasas.y + alto + origenAutos.y) / 2 - alto/2);

                margenBotonesMedios = (int)origenAutos.y - (int)origenOfis.y - (int)alto;

                final TranslateAnimation moverOfis = new TranslateAnimation(0, origenOfis.x - x, 0, origenOfis.y - y);

                // Animar Terrenos ***********************************************************
                x = contTerrenos.getX();
                y = contTerrenos.getY();
                ancho = contTerrenos.getWidth();
                alto = contTerrenos.getHeight();
                // terrenos va al medio a la derecha
                final PointF origenTerrenos = new PointF(origen.x + anchoP - margen - ancho, (origenCasas.y + alto + origenAutos.y) / 2 - alto/2);

                final TranslateAnimation moverTerrenos = new TranslateAnimation(0, origenTerrenos.x - x, 0, origenTerrenos.y - y);


                // Animar Toolbar ***********************************************************
                ancho = toolbar.getWidth();
                alto = toolbar.getHeight();

                final TranslateAnimation moverToolbar = new TranslateAnimation(0, 0, -alto - margen, 0);
                // Animar bottombar ***********************************************************
                x = btnInfo.getX();
                y = btnInfo.getY();
                ancho = btnInfo.getWidth();
                alto = btnInfo.getHeight();

                final TranslateAnimation moverBottomBar = new TranslateAnimation(0, 0, origen.y + altoP + margen, 0);

                OvershootInterpolator interpolator = new OvershootInterpolator();
                DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();

                moverToolbar.setDuration(duracion/3);
                moverToolbar.setInterpolator(decelerateInterpolator);
                moverToolbar.setStartOffset(duracion);

                toolbar.setAnimation(moverToolbar);

                moverBottomBar.setDuration(duracion/3);
                moverBottomBar.setInterpolator(decelerateInterpolator);
                moverBottomBar.setStartOffset(duracion);

                linea.setAnimation(moverBottomBar);
                btnInfo.setAnimation(moverBottomBar);

                AnimationSet autos = new AnimationSet(true);
                autos.addAnimation(fadeIn);
                autos.addAnimation(moverAutos);
                autos.setDuration(duracion);
                autos.setInterpolator(interpolator);

                AnimationSet casas = new AnimationSet(true);
                casas.addAnimation(fadeIn);
                casas.addAnimation(mover);
                casas.setDuration(duracion);
                casas.setInterpolator(interpolator);

                AnimationSet depas = new AnimationSet(true);
                depas.addAnimation(fadeIn);
                depas.addAnimation(moverDepas);
                depas.setDuration(duracion);
                depas.setInterpolator(interpolator);

                AnimationSet ofis = new AnimationSet(true);
                ofis.addAnimation(fadeIn);
                ofis.addAnimation(moverOfis);
                ofis.setDuration(duracion);
                ofis.setInterpolator(interpolator);

                AnimationSet terrenos = new AnimationSet(true);
                terrenos.addAnimation(fadeIn);
                terrenos.addAnimation(moverTerrenos);
                terrenos.setDuration(duracion);
                terrenos.setInterpolator(interpolator);

                autos.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        btnCasas.setVisibility(View.VISIBLE);
                        contAutos.setVisibility(View.VISIBLE);
                        btnDepas.setVisibility(View.VISIBLE);
                        contOfis.setVisibility(View.VISIBLE);
                        contTerrenos.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        final TextView lblCasas = (TextView)findViewById(R.id.lblCasas);
                        final TextView lblDepas = (TextView)findViewById(R.id.lblDepas);
                        final TextView lblTerrenos = (TextView)findViewById(R.id.lblTerrenos);
                        final TextView lblOfis = (TextView)findViewById(R.id.lblOfis);
                        final TextView lblAutos = (TextView)findViewById(R.id.lblAutos);


                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(btnCasas.getWidth(), btnCasas.getHeight());
                        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        params.topMargin = 100 + (int)origen.y;
                        params.leftMargin = (int)margen;
                        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        btnCasas.setLayoutParams(params);
                        btnCasas.clearAnimation();

                        params = new RelativeLayout.LayoutParams(btnDepas.getWidth(), btnDepas.getHeight());
                        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        params.topMargin = 100 + (int)origen.y;
                        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        params.rightMargin = (int)margen;
                        btnDepas.setLayoutParams(params);
                        btnDepas.clearAnimation();

                        params = new RelativeLayout.LayoutParams(contAutos.getWidth(), contAutos.getHeight());
                        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        params.bottomMargin = 0;
                        params.addRule(RelativeLayout.ABOVE, R.id.btnInfo);
                        contAutos.setLayoutParams(params);
                        contAutos.clearAnimation();

                        params = new RelativeLayout.LayoutParams(contOfis.getWidth(), contOfis.getHeight());
                        params.addRule(RelativeLayout.ABOVE, R.id.contAutos);
                        params.bottomMargin = margenBotonesMedios;
                        params.leftMargin = (int)margen;
                        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        contOfis.setLayoutParams(params);
                        contOfis.clearAnimation();

                        params = new RelativeLayout.LayoutParams(contTerrenos.getWidth(), contTerrenos.getHeight());
                        params.addRule(RelativeLayout.ABOVE, R.id.contAutos);
                        params.bottomMargin = margenBotonesMedios;
                        params.rightMargin = (int)margen;
                        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        contTerrenos.setLayoutParams(params);
                        contTerrenos.clearAnimation();

                        toolbar.setVisibility(View.VISIBLE);
                        linea.setVisibility(View.VISIBLE);
                        btnInfo.setVisibility(View.VISIBLE);

                        AlphaAnimation aparecer = new AlphaAnimation(0, 1);
                        aparecer.setDuration(duracion);
                        aparecer.setStartOffset(duracion/3);
                        aparecer.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                lblCasas.setVisibility(View.VISIBLE);
                                lblAutos.setVisibility(View.VISIBLE);
                                lblDepas.setVisibility(View.VISIBLE);
                                lblOfis.setVisibility(View.VISIBLE);
                                lblTerrenos.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        lblAutos.setAnimation(aparecer);
                        lblCasas.setAnimation(aparecer);
                        lblDepas.setAnimation(aparecer);
                        lblOfis.setAnimation(aparecer);
                        lblTerrenos.setAnimation(aparecer);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                /*
                autos.setStartOffset(1000);
                casas.setStartOffset(1000);
                depas.setStartOffset(1000);
                ofis.setStartOffset(1000);
                terrenos.setStartOffset(1000);
                */
                contAutos.setAnimation(autos);
                btnCasas.setAnimation(casas);
                btnDepas.setAnimation(depas);
                contOfis.setAnimation(ofis);
                contTerrenos.setAnimation(terrenos);
            }
        });
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(InicioActivity.this, InfoActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void termino(JSONObject obj) {
        try{
            if (obj.getInt("success") == 1) {
                SharedPreferences preferences = getSharedPreferences(getString(R.string.preferencias),MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putFloat("maxCasas", (float)obj.getDouble("maxCasas"));
                editor.putFloat("maxDepars", (float)obj.getDouble("maxDepars"));
                editor.putFloat("maxTerrenos", (float)obj.getDouble("maxTerrenos"));
                editor.putFloat("maxOfis", (float)obj.getDouble("maxOfis"));
                editor.putFloat("maxAutos", (float)obj.getDouble("maxAutos"));
                editor.putFloat("maxMotos", (float)obj.getDouble("maxMotos"));
                editor.apply();
                Log.i("rosco", "se pusieron los datos!");
            }else{
                Log.i("rosco", "Error al cargar los maximos:"+obj.getString("mensaje"));
            }
        }catch (JSONException e){
            Log.i("rosco", e.getMessage());
        }
    }

    @Override
    public void cancelo(String error) {
        Log.i("rosco", "Error al cargar los maximos:"+error);
    }
}
