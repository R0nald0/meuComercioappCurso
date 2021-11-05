package com.example.meucomercio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

public class Detalhes extends AppCompatActivity {
    private TextView txRecebido,txUserNome,txComerNome,txDataNome,txComenta;
    private Button btSalvacomentario,btVoltar;
    private EditText edComent;
    private ImageView imPerfil;
     private FirebaseAuth auth = FirebaseAuth.getInstance();
    private androidx.appcompat.widget.Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);
          imPerfil=(ImageView) findViewById(R.id.imgPerfil);
        txRecebido=(TextView) findViewById(R.id.txRecebido);
        txUserNome=(TextView) findViewById(R.id.txNomeUsuário);
        txComerNome =(TextView) findViewById(R.id.txNotanome);
        txDataNome =(TextView) findViewById(R.id.txNomeData);
        btSalvacomentario = (Button) findViewById(R.id.btnComentar);
        btVoltar = (Button) findViewById(R.id.btnVoltar);
        txComenta = (TextView) findViewById(R.id.txComentario);
        edComent =(EditText) findViewById(R.id.edtComentar);

    }

    @Override
    protected void onStart() {
        super.onStart();
        toolbar = (Toolbar) findViewById(R.id.toobar);
        toolbar.setTitle(R.string.meu_com_rcio);
        setSupportActionBar(toolbar);

        if (getIntent().hasExtra("nome")){
            Bundle bundle = getIntent().getExtras();
            Glide.with(getApplicationContext()).load(bundle.getString("img")).into(imPerfil);
            txRecebido.setText("Nome do Comércio: " + bundle.getString("nome"));
            txUserNome.setText("Postado por: "+ bundle.getString("nomeUser"));
            txDataNome.setText("Postado em: " +bundle.getString("data"));
            txComerNome.setText("Info:" +bundle.getString("nota"));

            Log.i("put",bundle.getString("nome"));
        }else {
            Toast.makeText(getApplicationContext(),"nada para recduperar",Toast.LENGTH_LONG).show();
        }
        btSalvacomentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String comentario = edComent.getText().toString();
                txComenta.setText(comentario);
                edComent.setText("");
            }
        });
        btVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 startActivity(new Intent(getApplicationContext(),MainActivity.class));
                 finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuconfi,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.idItsair:
                deslogar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public  void  deslogar(){
        auth.signOut();
        startActivity(new Intent(Detalhes.this,LoginActivity.class));
        finish();
    }
}