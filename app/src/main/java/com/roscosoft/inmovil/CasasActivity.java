package com.roscosoft.inmovil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
import android.widget.Button;
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

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Hp on 22/8/2017.
 */

public class CasasActivity extends AppCompatActivity implements HttpPost.HttpPostInterface{

    class CasaAdapter extends ArrayAdapter<Casa> {
        private List<Casa> lista;
        private Context context;

        public CasaAdapter(List<Casa> casas, Context c){
            super(c, 0, casas);
            lista = casas;
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
        public Casa getItem(int position) {
            return lista.get(position);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null){
                LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view =  mInflater.inflate(R.layout.casas_item, null);
            }
            Casa casa = lista.get(i);

            String s= String.format(Locale.getDefault(), "%s %,.2f ", casa.moneda, casa.precio);
            SpannableString ss1=  new SpannableString(s);
            ss1.setSpan(new RelativeSizeSpan(0.7f), 0,casa.moneda.length(), 0); // set size
            //ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, 5, 0);// set color

            ((TextView)view.findViewById(R.id.precio)).setText(ss1);
            ((TextView)view.findViewById(R.id.descripcion)).setText(casa.descripcion);
            //TODO: Ver si mostramos la superficie total o construida
            ((TextView)view.findViewById(R.id.lblSupTot)).setText(String.format(Locale.getDefault(), "%,.2f %s", casa.supT, casa.unidadSup));
            if (tipo == 4){
                final View contCuartos = view.findViewById(R.id.contCuartos);
                contCuartos.setVisibility(View.GONE);
                View contBanos = view.findViewById(R.id.contBanos);
                contBanos.setVisibility(View.GONE);
            }else{
                ((TextView)view.findViewById(R.id.cantBanos)).setText(casa.banos+"");
                ((TextView)view.findViewById(R.id.cantCuartos)).setText(casa.cuartos+"");
            }

            final ImageView imageView = (ImageView) view.findViewById(R.id.foto);
            if (casa.fotos.size() > 0){
                ImageLoader imageLoader = MySingleton.getInstance(CasasActivity.this.getApplicationContext()).getImageLoader();

                imageLoader.get(casa.fotos.get(0), new ImageLoader.ImageListener() {
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
    boolean cargando = true;
    ImageButton btnBuscar;
    int alturaOriginal = 100;
    ProgressBar progressBar;
    int tipo = 1; // 1 casas, 2, depars, 3 ofis, 4 terrenos
    int tipoC = 1; // tipo de compra 1 = venta, 2 = alquiler, 3 anticretico
    //int maxSup = 1000;
    boolean buscando = false;
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

        final TextView titluo = (TextView)findViewById(R.id.titulo);

        lista = (ListView)findViewById(R.id.lista);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Casa casa = ((CasaAdapter)lista.getAdapter()).getItem(i);
                Intent in = new Intent(CasasActivity.this, CasaActivity.class);
                in.putExtra("casa", casa);
                Log.i("rosco", casa.descripcion);
                startActivity(in);
            }
        });
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        HashMap<String, String> map = new HashMap<>();
        map.put("funcion", "casas");
        tipo = getIntent().getIntExtra("tipo", 1);

        String tit = "";
        if (tipo == 1){ // casas
            ((ImageView)findViewById(R.id.imgTitle)).setImageResource(R.drawable.casas_ico);
            tit = "Casas ";
        }else if(tipo == 2){ // Depas
            ((ImageView)findViewById(R.id.imgTitle)).setImageResource(R.drawable.depars_ico);
            tit = "Deptos. ";
        }else if(tipo == 3){ // Ofis
            ((ImageView)findViewById(R.id.imgTitle)).setImageResource(R.drawable.oficina_ico);
            tit = "Oficinas ";
        }else if(tipo == 4){ // Terrenos
            ((ImageView)findViewById(R.id.imgTitle)).setImageResource(R.drawable.terrenos_ico);
            tit = "Terrenos ";
        }

        map.put("tipo", tipo+"");
        tipoC = getIntent().getIntExtra("tipoC", 1);
        map.put("tipoC", tipoC+"");

        if (tipoC == 1){
            tit += "en Venta";
        }else if(tipoC == 2){
            if (tipo == 2)
                tit = "Deptos. ";
            tit += "en Alquiler";
        }else{
            if (tipo == 2)
                tit = "Deptos. ";
            tit += "en Anticretico";
        }
        btnBuscar = (ImageButton)findViewById(R.id.btnBuscar);
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CasasActivity.this, BuscarInmActivity.class);
                i.putExtra("tipo", tipo);
                startActivityForResult(i, 1); // codigo de request  es 1
            }
        });
        titluo.setText(tit);
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
    // TODO: mejorar la animacion
    void ocultarCargando(){
        if (cargando == false)
            return;
        cargando = false;
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -progressBar.getY() -progressBar.getHeight());
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
                map.put("funcion", "buscarInm");
                if (data.hasExtra("cuartos"))
                    map.put("cuartos", data.getIntExtra("cuartos", 0)+"");
                if (data.hasExtra("banos"))
                    map.put("banos", data.getIntExtra("banos", 0)+"");
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
                map.put("tipoC", tipoC+"");
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
    }
    @Override
    public void termino(JSONObject obj) {
        try{
            ocultarCargando();
            Log.i("rosco", obj.getString("mensaje"));
            if (obj.getInt("success") == 1){
                JSONArray casasJ = obj.getJSONArray("casas");
                ArrayList<Casa> casas = new ArrayList<>();

                for(int i = 0 ; i < casasJ.length(); i++){
                    JSONObject casaJ = casasJ.getJSONObject(i);
                    Casa casa = new Casa();
                    casa.tipo = tipo;
                    ArrayList<String> fotos = new ArrayList<>();
                    JSONArray fotosJ = casaJ.getJSONArray("fotos");
                    for (int j = 0; j < fotosJ.length(); j++)
                        fotos.add(getString(R.string.url)+fotosJ.getString(j));
                    casa.fotos = fotos;
                    casa.precio = casaJ.getDouble("precio");
                    casa.descripcion = casaJ.getString("descripcion");
                    casa.banos = casaJ.getInt("banos");
                    casa.cuartos = casaJ.getInt("dormitorios");
                    casa.pisos = casaJ.getInt("pisos");
                    casa.barrio = casaJ.getString("barrio");
                    casa.detalle = casaJ.getString("detalle");
                    casa.zona = casaJ.getString("zona");
                    casa.provincia = casaJ.getString("provincia");
                    casa.direccion = casaJ.getString("direccion");
                    casa.latitud = casaJ.getDouble("latitud");
                    casa.longitud = casaJ.getDouble("longitud");
                    casa.nombreC = casaJ.getString("nombre_contacto");
                    casa.telefono = casaJ.getString("telefono_contacto");
                    casa.telefono1 = casaJ.getString("telefono_contacto_1");
                    casa.supC = casaJ.getDouble("superficie_construida");
                    casa.supT = casaJ.getDouble("superficie");
                    /*
                    int sup = (int)casa.supT;
                    int unidad = casaJ.getInt("unidad_sup");
                    if (unidad == 1){ // m2
                    }else if(unidad == 2){ // ha a m2
                        sup = sup * 10000;
                    }else{ // convertir km a metros
                        sup = sup * 1000000;
                    }
                    if (sup > maxSup)
                        maxSup = sup;
                     */
                    casa.unidadSup = casaJ.getString("umed");
                    casa.unidadSupCon = casaJ.getString("umedC");
                    int mon = casaJ.getInt("moneda");
                    if (mon == 1)
                        casa.moneda =  "Bs.";
                    else if (mon == 2)
                        casa.moneda =  "$us.";
                    else
                        casa.moneda = "€";
                    casas.add(casa);
                }
                if (casas.size() == 0){
                    //TODO: cambiar aqui para mostrar una fila diciendo que no hay resultados
                    String msj = "No hay ";
                    if (buscando){
                        msj = "No se encontraron resultados de su busqueda";
                    }else{
                        if (tipo == 1){
                            msj += "casas ";
                        }else if (tipo == 2){
                            msj += "departamentos ";
                        }else if(tipo == 3){
                            msj += "oficinas ";
                        }else{
                            msj += " terrenos ";
                        }
                        if (tipoC == 1){
                            msj += "en venta";
                        }else if(tipoC == 2){
                            msj += "en alquiler";
                        }else{
                            msj += "en anticretico";
                        }
                    }

                    Toast.makeText(CasasActivity.this, msj, Toast.LENGTH_LONG).show();
                    finish();
                }
                CasaAdapter miAdapter = new CasaAdapter(casas, CasasActivity.this);
                lista.setAdapter(miAdapter);
                buscando = false;
            }else{
                Log.i("rosco", "Error deste el server "+obj.getString("mensaje"));
                Toast.makeText(CasasActivity.this, obj.getString("mensaje"), Toast.LENGTH_SHORT).show();
                finish();
            }
        }catch (JSONException e){
            Log.i("rosco", "Error de json "+e.getMessage());
            Toast.makeText(CasasActivity.this, "Hubo un erro, intenelo mas tarde", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void cancelo(String error) {
        Log.i("rosco", error);
        Toast.makeText(CasasActivity.this, "Hubo un error, intente cargar mas tarde", Toast.LENGTH_LONG).show();
        finish();
    }
}
