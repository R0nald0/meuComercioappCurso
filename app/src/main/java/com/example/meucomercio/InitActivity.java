package com.example.meucomercio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meucomercio.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class InitActivity extends AppCompatActivity {
   FirebaseAuth auth = FirebaseAuth.getInstance();
   private TextView txMcomercio;


   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        txMcomercio = (TextView) findViewById(R.id.txMeuComercio);
    }


    @Override
    protected void onStart() {
        super.onStart();

        verificaLogin();
    }

    public void verificaLogin(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initActivity();
                finish();
            }
        },3000);

    }


    public void initActivity(){
        FirebaseUser  user = auth.getCurrentUser();
        if (user != null){

            Intent intent= new Intent(InitActivity.this,MainActivity.class);
            startActivity(intent);

            Toast.makeText(this,"Entrando",Toast.LENGTH_LONG).show();

        }else{
            startActivity(new Intent(InitActivity.this,LoginActivity.class));
        }
    };
}