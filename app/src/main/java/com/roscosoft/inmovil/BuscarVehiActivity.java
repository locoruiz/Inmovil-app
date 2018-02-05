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

public class BuscarVehiActivity extends Activity {
    int tipo = 1; // 1 casas
    int ano = 0;
    int maxSup = 100;
    boolean modificaron = false;
    boolean eraMayor = false;
    TextView lblCuartos, lblSuperficie;
    EditText lblBanos;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_auto);
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
        lblBanos = (EditText)findViewById(R.id.lblNBanos);
        lblSuperficie = (TextView)findViewById(R.id.limSup);
        lblSuperficie.setText("0 km - "+maxSup+" km");
        switch (tipo){
            case 1:
                maxSup = (int)preferences.getFloat("maxAutos", 100000);
                titulo.setText("Buscar Autos");
                break;
            case 2:
                maxSup = (int)preferences.getFloat("maxMotos", 100000);
                titulo.setText("Buscar Motos");
                break;
        }
        Log.i("rosco", "max "+maxSup);

        lblBanos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty())
                    ano = 0;
                else
                    ano = Integer.parseInt(editable.toString());
            }
        });

        final EditText etPrecioMin = (EditText)findViewById(R.id.etPrecioMin);
        final EditText etPrecioMax = (EditText)findViewById(R.id.etPrecioMax);
        (findViewById(R.id.btnMasBanos)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ano++;
                lblBanos.setText(ano+"");
            }
        });
        (findViewById(R.id.btnMenosBanos)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ano--;
                if (ano < 0){
                    ano = 0;
                    lblBanos.setText("");
                }else
                    lblBanos.setText(ano+"");
            }
        });
        final RangeBar rangeBar = (RangeBar)findViewById(R.id.rangeBar);
        lblSuperficie.setText("0 km - "+maxSup+" km");
        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int i, int i1) {
                modificaron = true;
                int factor = maxSup / 100;
                lblSuperficie.setText((i*factor)+" km - "+(i1*factor)+" km");
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
                if (ano > 0)
                    i.putExtra("ano", ano);
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
