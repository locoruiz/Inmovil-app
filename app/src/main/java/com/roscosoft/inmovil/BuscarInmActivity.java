package com.roscosoft.inmovil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.edmodo.rangebar.RangeBar;

/**
 * Created by Hp on 14/11/2017.
 */

public class BuscarInmActivity extends Activity {
    int tipo = 1; // 1 casas
    int cuartos = 0;
    int banos = 0;
    int maxSup = 100;
    boolean modificaron = false;
    boolean eraMayor = false;
    TextView lblCuartos, lblBanos, lblSuperficie;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_casa);
        tipo = getIntent().getIntExtra("tipo", 1);
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preferencias), MODE_PRIVATE);
        /*
        maxSup = getIntent().getIntExtra("maxSup", 100);
        int aux = 100;
        while (aux < maxSup){
            aux *= 10;
        }
        maxSup = aux;
        */
        final TextInputEditText txtBuscar = (TextInputEditText) findViewById(R.id.txtBuscar);
        TextView titulo = (TextView)findViewById(R.id.titulo);
        lblBanos = (TextView)findViewById(R.id.lblNBanos);
        lblCuartos = (TextView)findViewById(R.id.lblNCuartos);
        lblSuperficie = (TextView)findViewById(R.id.limSup);
        lblSuperficie.setText("0 m2 - "+maxSup+" m2");
        switch (tipo){
            case 1:
                maxSup = (int)preferences.getFloat("maxCasas", 100);
                titulo.setText("Buscar Casas");
                break;
            case 2:
                maxSup = (int)preferences.getFloat("maxDepars", 100);
                titulo.setText("Buscar Deptos.");
                break;
            case 3:
                maxSup = (int)preferences.getFloat("maxOfis", 100);
                titulo.setText("Buscar Oficinas");
                break;
            case 4:
                maxSup = (int)preferences.getFloat("maxTerrenos", 100);
                titulo.setText("Buscar Terrenos");
                ((View)findViewById(R.id.btnMasBanos).getParent()).setVisibility(View.GONE);
                ((View)findViewById(R.id.btnMasCuartos).getParent()).setVisibility(View.GONE);
                break;
        }
        final EditText etPrecioMin = (EditText)findViewById(R.id.etPrecioMin);
        final EditText etPrecioMax = (EditText)findViewById(R.id.etPrecioMax);
        ((Button)findViewById(R.id.btnMasCuartos)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cuartos < 6)
                    cuartos++;
                if (cuartos == 6)
                    lblCuartos.setText("6 o más");
                else
                    lblCuartos.setText(cuartos+"");
            }
        });
        ((Button)findViewById(R.id.btnMasBanos)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (banos < 6)
                    banos++;
                if (banos == 6)
                    lblBanos.setText("6 o más");
                else
                    lblBanos.setText(banos+"");
            }
        });
        ((Button)findViewById(R.id.btnMenosCuartos)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cuartos > 0)
                    cuartos--;
                if (cuartos == 0)
                    lblCuartos.setText("");
                else
                    lblCuartos.setText(cuartos+"");
            }
        });
        ((Button)findViewById(R.id.btnMenosBanos)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (banos > 0)
                    banos--;
                if (banos == 0)
                    lblBanos.setText("");
                else
                    lblBanos.setText(banos+"");
            }
        });
        final RangeBar rangeBar = (RangeBar)findViewById(R.id.rangeBar);
        lblSuperficie.setText("0 m2 - "+maxSup+" m2");
        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int i, int i1) {
                modificaron = true;
                int factor = maxSup / 100;
                lblSuperficie.setText((i*factor)+" m2 - "+(i1*factor)+" m2");
            }
        });

        etPrecioMin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!etPrecioMax.getText().toString().isEmpty() && !editable.toString().isEmpty()){
                    float maximo = Float.parseFloat(etPrecioMax.getText().toString());
                    float minimo = Float.parseFloat(etPrecioMin.getText().toString());
                    if (minimo > maximo){
                        etPrecioMax.setText(etPrecioMin.getText());
                    }
                }
            }
        });
        etPrecioMax.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etPrecioMax.getText().toString().length() >= etPrecioMin.getText().toString().length())
                    eraMayor = true;
                if (etPrecioMax.getText().toString().isEmpty())
                    eraMayor = false;
                if (!etPrecioMin.getText().toString().isEmpty() && !editable.toString().isEmpty()){
                    float maximo = Float.parseFloat(etPrecioMax.getText().toString());
                    float minimo = Float.parseFloat(etPrecioMin.getText().toString());
                    if (minimo > maximo && eraMayor){
                        etPrecioMin.setText(etPrecioMax.getText());
                    }
                }
            }
        });

        findViewById(R.id.btnBuscar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                if (cuartos > 0)
                    i.putExtra("cuartos", cuartos);
                if (banos > 0)
                    i.putExtra("banos", banos);
                if (!txtBuscar.getText().toString().trim().isEmpty())
                    i.putExtra("texto", txtBuscar.getText().toString());
                if (modificaron){
                    i.putExtra("minSup", rangeBar.getLeftIndex() * (maxSup/100));
                    i.putExtra("maxSup", rangeBar.getRightIndex() * (maxSup/100));
                }
                if (!etPrecioMin.getText().toString().isEmpty())
                    i.putExtra("precioMin", Float.parseFloat(etPrecioMin.getText().toString()));
                if (!etPrecioMax.getText().toString().isEmpty())
                    i.putExtra("precioMax", Float.parseFloat(etPrecioMax.getText().toString()));

                setResult(1, i);
                finish();
            }
        });
    }
}
