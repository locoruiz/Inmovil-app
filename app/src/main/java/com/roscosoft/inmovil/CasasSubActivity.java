package com.roscosoft.inmovil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Hp on 22/8/2017.
 */

public class CasasSubActivity extends AppCompatActivity {

    int tipo = 1; // 1 casas, 2 depars, 3 ofis, 4 terrenos

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_sub_casas);

        Intent intent = getIntent();
        tipo = intent.getIntExtra("tipo", 1);

        if (tipo == 1){ // Casas
            ((ImageView)findViewById(R.id.btnCasas)).setImageResource(R.drawable.ic_casa_ico);
            ((TextView)findViewById(R.id.lblAutos)).setText("CASAS");
            ((Button)findViewById(R.id.btnInfo)).setText("¿Quieres ofrecer tu casa?");
        }else if (tipo == 2){ // Depars
            ((ImageView)findViewById(R.id.btnCasas)).setImageResource(R.drawable.ic_depars_ico);
            ((TextView)findViewById(R.id.lblAutos)).setText("DEPARTAMENTOS");
            ((Button)findViewById(R.id.btnInfo)).setText("¿Quieres ofrecer tu departamento?");
        }else if (tipo == 3){ // Oficinas
            ((ImageView)findViewById(R.id.btnCasas)).setImageResource(R.drawable.ic_ofis_ico);
            ((TextView)findViewById(R.id.lblAutos)).setText("OFICINAS");
            ((Button)findViewById(R.id.btnInfo)).setText("¿Quieres ofrecer tu oficina?");
        }else if (tipo == 4){ // Terrenos
            ((ImageView)findViewById(R.id.btnCasas)).setImageResource(R.drawable.ic_terrenos_ico);
            ((TextView)findViewById(R.id.lblAutos)).setText("TERRENOS");
            ((Button)findViewById(R.id.btnInfo)).setText("¿Quieres ofrecer tu terreno?");
            findViewById(R.id.btnEnAlquiler).setVisibility(View.GONE);
            findViewById(R.id.btnEnAnti).setVisibility(View.GONE);
        }else if (tipo == 5){ // Autos
            ((ImageView)findViewById(R.id.btnCasas)).setImageResource(R.drawable.ic_auto_ico);
            ((TextView)findViewById(R.id.lblAutos)).setText("VEHÍCULOS");
            ((Button)findViewById(R.id.btnInfo)).setText("¿Quieres vender tu vehículo?");
            ((Button)findViewById(R.id.btnEnVenta)).setText("Automóviles");
            ((Button)findViewById(R.id.btnEnAnti)).setText("Motocicletas");
            findViewById(R.id.btnEnAlquiler).setVisibility(View.GONE);
        }

        findViewById(R.id.btnEnVenta).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tipo == 5){
                    Intent i = new Intent(CasasSubActivity.this, VehiculosActivity.class);
                    i.putExtra("tipo", 1);
                    startActivity(i);
                    return;
                }
                Intent i = new Intent(CasasSubActivity.this, CasasActivity.class);
                i.putExtra("tipo", tipo);
                i.putExtra("tipoC", 1);
                startActivity(i);
            }
        });
        findViewById(R.id.btnEnAlquiler).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CasasSubActivity.this, CasasActivity.class);
                i.putExtra("tipo", tipo);
                i.putExtra("tipoC", 2);
                startActivity(i);
            }
        });
        findViewById(R.id.btnEnAnti).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tipo == 5){
                    Intent i = new Intent(CasasSubActivity.this, VehiculosActivity.class);
                    i.putExtra("tipo", 2);
                    startActivity(i);
                    return;
                }
                Intent i = new Intent(CasasSubActivity.this, CasasActivity.class);
                i.putExtra("tipo", tipo);
                i.putExtra("tipoC", 3);
                startActivity(i);
            }
        });
        findViewById(R.id.btnInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CasasSubActivity.this, ContactoActivity.class);
                startActivity(i);
            }
        });
    }
}
