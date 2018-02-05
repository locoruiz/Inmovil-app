package com.roscosoft.inmovil;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Hp on 22/8/2017.
 */

public class VehiculosActivity extends AppCompatActivity implements HttpPost.HttpPostInterface{

    class VehiculoAdapter extends ArrayAdapter<Vehiculo> {
        private List<Vehiculo> lista;
        private Context context;

        public VehiculoAdapter(List<Vehiculo> vehiculos, Context c){
            super(c, 0, vehiculos);
            lista = vehiculos;
            context = c;
        }

        @Override
        public int getCount() {
            return lista.size();
        }


        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Nullable
        @Override
        public Vehiculo getItem(int position) {
            return lista.get(position);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null){
                LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view =  mInflater.inflate(R.layout.autos_item, null);
            }
            Vehiculo auto = lista.get(i);

            String s= String.format(Locale.getDefault(), "%s %,.2f ", auto.moneda, auto.precio);
            SpannableString ss1=  new SpannableString(s);
            ss1.setSpan(new RelativeSizeSpan(0.7f), 0,auto.moneda.length(), 0); // set size

            ((TextView)view.findViewById(R.id.precio)).setText(ss1);
            ((TextView)view.findViewById(R.id.descripcion)).setText(auto.marca+" "+auto.modelo+" "+auto.ano);
            ((TextView)view.findViewById(R.id.lblKilom)).setText(String.format(Locale.getDefault(), "%,d km.", auto.kilometraje));
            if (tipo == 2){
                view.findViewById(R.id.contCuartos).setVisibility(View.GONE);
            }else{
                ((TextView)view.findViewById(R.id.cantPuertas)).setText(auto.puertas+"");
            }
            String caja = "M";
            if (auto.caja == 1){ // 1 manual, 2 automatica, 3 secuencial
                caja = "M";
            }else if(auto.caja == 2){
                caja = "A";
            }else{
                caja = "S";
            }
            ((TextView)view.findViewById(R.id.tipoCaja)).setText(caja);

            final ImageView imageView = (ImageView) view.findViewById(R.id.foto);
            if (auto.fotos.size() > 0){
                ImageLoader imageLoader = MySingleton.getInstance(VehiculosActivity.this.getApplicationContext()).getImageLoader();

                imageLoader.get(auto.fotos.get(0), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        imageView.setImageBitmap(response.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("rosco", "Error al cargar la imagen: "+error.getMessage());
                    }
                });
            }
            return view;
        }
    }
    HttpPost post;
    ListView lista;
    ProgressBar progressBar;
    int tipo = 1; // 1 autos, 2 motos
    boolean cargando = true;
    boolean buscando = false;
    int alturaOriginal = 100;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_casas);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= 21){
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.negro));
        }

        TextView titluo = (TextView)findViewById(R.id.titulo);

        lista = (ListView)findViewById(R.id.lista);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Vehiculo auto = ((VehiculoAdapter)lista.getAdapter()).getItem(i);
                Intent in = new Intent(VehiculosActivity.this, VehiculoActivity.class);
                in.putExtra("auto", auto);
                startActivity(in);
            }
        });
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        HashMap<String, String> map = new HashMap<>();
        map.put("funcion", "vehiculos");
        tipo = getIntent().getIntExtra("tipo", 1);

        ((ImageView)findViewById(R.id.imgTitle)).setImageResource(R.drawable.auto_ico);
        String tit = "";
        if (tipo == 1){ // autos
            tit = "Autos ";
        }else if(tipo == 2){ // Motos
            tit = "Motos ";
        }
        map.put("tipo", tipo+"");
        titluo.setText(tit);


        ImageButton btnBuscar = (ImageButton)findViewById(R.id.btnBuscar);
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(VehiculosActivity.this, BuscarVehiActivity.class);
                i.putExtra("tipo", tipo);
                startActivityForResult(i, 1); // codigo de request  es 1
            }
        });

        try{
            String postString = HttpPost.getPostDataString(map);
            post = new HttpPost(getString(R.string.url)+"servicios.php", postString, this);
            post.execute();
        }catch (UnsupportedEncodingException e){
            Log.i("rosco", e.getMessage());
        }
        titluo.post(new Runnable() {
            @Override
            public void run() {
                alturaOriginal = (int)progressBar.getY();

                setAllParentsClip(progressBar, false);
            }
        });
    }

    void ocultarCargando(){
        if (cargando == false)
            return;
        TranslateAnimation animation = new TranslateAnimation(0, 0, progressBar.getY(), -progressBar.getY() -progressBar.getHeight());
        animation.setFillAfter(true);
        animation.setDuration(500);
        progressBar.setAnimation(animation);
    }
    void mostrarCargando(){
        if (cargando == true)
            return;
        cargando = true;
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, alturaOriginal);
        animation.setFillAfter(true);
        animation.setDuration(500);
        progressBar.startAnimation(animation);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (resultCode == 1){ // selecciono una busqueda
                HashMap<String, String> map = new HashMap<>();
                map.put("funcion", "buscarVehi");
                if (data.hasExtra("ano"))
                    map.put("ano", data.getIntExtra("ano", 2017)+"");
                if (data.hasExtra("texto")){
                    map.put("texto", data.getStringExtra("texto"));
                }
                if (data.hasExtra("minSup")){
                    map.put("minSup", data.getIntExtra("minSup", 0)+"");
                    map.put("maxSup", data.getIntExtra("maxSup", 1000000)+"");
                }
                if (data.hasExtra("precioMin")){
                    map.put("precioMin", ""+data.getFloatExtra("precioMin", 0.0f));
                }
                if (data.hasExtra("precioMax")){
                    map.put("precioMax", ""+data.getFloatExtra("precioMax", 1000000.0f));
                }
                map.put("tipo", tipo+"");
                try{
                    mostrarCargando();
                    buscando = true;
                    String postString = HttpPost.getPostDataString(map);
                    HttpPost post = new HttpPost(getString(R.string.url)+"servicios.php", postString, this);
                    post.execute();
                }catch (UnsupportedEncodingException e){
                    Log.i("rosco", e.getMessage());
                }
            }
        }
    }
    public static void setAllParentsClip(View v, boolean enabled) {
        int i = 0;
        while (v.getParent() != null && v.getParent() instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) v.getParent();
            viewGroup.setClipChildren(enabled);
            viewGroup.setClipToPadding(enabled);
            v = viewGroup;
            i++;
        }
        Log.i("rosco", "cambio el clip "+i+" veces");
    }


    @Override
    public void termino(JSONObject obj) {
        try{
            ocultarCargando();
            Log.i("rosco", obj.getString("mensaje"));
            if (obj.getInt("success") == 1){
                JSONArray vehiculosJ = obj.getJSONArray("autos");
                ArrayList<Vehiculo> vehiculos = new ArrayList<>();

                for(int i = 0 ; i < vehiculosJ.length(); i++){
                    JSONObject autoJ = vehiculosJ.getJSONObject(i);
                    Vehiculo auto = new Vehiculo();
                    ArrayList<String> fotos = new ArrayList<>();
                    JSONArray fotosJ = autoJ.getJSONArray("fotos");
                    for (int j = 0; j < fotosJ.length(); j++)
                        fotos.add(getString(R.string.url)+fotosJ.getString(j));
                    auto.fotos = fotos;
                    auto.tipo = autoJ.getInt("tipo");
                    auto.precio = autoJ.getDouble("precio");
                    auto.descripcion = autoJ.getString("descripcion");
                    auto.detalle = autoJ.getString("detalle");
                    auto.ano = autoJ.getInt("ano");
                    auto.caja = autoJ.getInt("caja");
                    auto.cilindrada = autoJ.getDouble("cilindrada");
                    auto.unidadCil = autoJ.getString("unidadCil"); // 1 c3, 2 cc
                    auto.puertas = autoJ.getInt("puertas");
                    int com = autoJ.getInt("combustible");
                    if (com == 1){
                        auto.combustible = "Gasolina";
                    }else if(com == 2){
                        auto.combustible = "Diesel";
                    }else if(com == 3){
                        auto.combustible = "Gasolina/Gas";
                    }else if(com == 4){
                        auto.combustible = "Diesel/Gas";
                    }
                    auto.marca = autoJ.getString("marca");
                    auto.modelo = autoJ.getString("modelo");
                    auto.color = autoJ.getString("color");
                    auto.kilometraje = autoJ.getInt("kilometraje");
                    auto.traccion = autoJ.getString("traccion");
                    auto.nombreC = autoJ.getString("nombre_contacto");
                    auto.telefono = autoJ.getString("telefono_contacto");
                    auto.telefono1 = autoJ.getString("telefono_contacto_1");

                    int mon = autoJ.getInt("moneda");
                    if (mon == 1)
                        auto.moneda =  "Bs.";
                    else if (mon == 2)
                        auto.moneda =  "$us.";
                    else
                        auto.moneda = "€";
                    vehiculos.add(auto);
                }
                if (vehiculos.size() == 0){
                    //TODO: cambiar la logica si es necesario
                    String msj = "No hay ";
                    if (buscando){
                        msj = "No se encontraron resultados de su busqueda";
                    }else{
                        if (tipo == 1){
                            msj += "autos en venta";
                        }else if (tipo == 2){
                            msj += "motos en venta";
                        }
                    }

                    Toast.makeText(VehiculosActivity.this, msj, Toast.LENGTH_LONG).show();
                    finish();
                }
                VehiculoAdapter miAdapter = new VehiculoAdapter(vehiculos, VehiculosActivity.this);
                lista.setAdapter(miAdapter);
                buscando = false;
            }else{
                Toast.makeText(VehiculosActivity.this, obj.getString("mensaje"), Toast.LENGTH_SHORT).show();
                finish();
            }
        }catch (JSONException e){
            Toast.makeText(VehiculosActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void cancelo(String error) {
        Log.i("rosco", error);
        Toast.makeText(VehiculosActivity.this, "Hubo un error, intente cargar mas tarde", Toast.LENGTH_LONG).show();
        finish();
    }
}
