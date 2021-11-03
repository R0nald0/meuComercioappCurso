package com.example.meucomercio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.meucomercio.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CadastroActivity extends AppCompatActivity {

    private EditText nome,email,senha;
    private Button cadastrarBt;
    private FirebaseAuth auth =FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String estadoEscolhido,cidadeEscolhida,bairroEscolhido;
    private Spinner estadospi,cidadespi,bairrospi;

    private String[] estad={
            "Bahia","Rio de Janeiro","São Paulo"};
    private String[] cidade={
            "Salvador","Camaçari","Feira de Santana"};
    private String[] baiiros={
            "Cajazeiras","Rio vermelho","Boca do Rio"};
    Usuario usuario1 = new Usuario();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        nome = (EditText) findViewById(R.id.cadEdtnome);
        email =(EditText)  findViewById(R.id.cadEdtemail);
        senha = (EditText) findViewById(R.id.cadEdtsenha);
        cadastrarBt= (Button) findViewById(R.id.telaCadBtn);
        estadospi = (Spinner) findViewById(R.id.spEstaodos);
        cidadespi = (Spinner) findViewById(R.id.spCidade);
        bairrospi = (Spinner) findViewById(R.id.spBairro);
    }

    @Override
    protected void onStart() {
        super.onStart();
         iniSpinmer();
        cadastrarBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifCampo();
            }
        });
    }

    private void verifCampo(){
        String erro ="Preencha este campo" ;
        if (nome.getText().length() !=0 & email.getText().length() !=0  & senha.getText().length() != 0){
             CadastrarUsuario();
        }
        else if (nome.getText().length() == 0){
            nome.setError(erro);
        }else  if (email.getText().length() == 0){
            email.setError(erro);
        } else if (senha.getText().length() == 0) {
            senha.setError(erro);
        }
    }

    private void CadastrarUsuario(){
        FirebaseUser user = auth.getCurrentUser();
        if (user != null){
            startActivity(new Intent(CadastroActivity.this,MainActivity.class));
        }else {
            auth.createUserWithEmailAndPassword(email.getText().toString(), senha.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                            usuario1.setNome(nome.getText().toString());
                            usuario1.setEmail(email.getText().toString());
                            usuario1.setSenha(senha.getText().toString());
                            usuario1.setEstado(estadoEscolhido);
                            usuario1.setCidade(cidadeEscolhida);
                            usuario1.setBairro(bairroEscolhido);
                            usuario1.setId(task.getResult().getUser().getUid());
                            usuario1.salvarUsuario();
                        startActivity(new Intent(CadastroActivity.this,MainActivity.class));
                        finish();
                    }else {
                        String msgErro="";
                        try {
                             throw task.getException();
                        }catch (FirebaseAuthWeakPasswordException e){
                                msgErro ="Digite uma senha mais forte";
                        }catch (FirebaseAuthUserCollisionException e){
                               msgErro ="Email ja esta cadastrado";
                        }catch (FirebaseAuthInvalidCredentialsException e){
                            msgErro="Email digitado é invalido";
                        } catch (Exception e) {
                            msgErro="Erro ao cadastrar o Usuario";
                        }

                        Toast.makeText(getApplicationContext()," "+ msgErro ,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void iniSpinmer(){
        List<String>  estados = new ArrayList<>();
        estados.addAll(Arrays.asList(estad));

        List<String> ci = new ArrayList<>();
        Collections.addAll(ci, cidade);

        List<String> bairroList = new ArrayList<>();
        for (int i = 0 ;  i<baiiros.length; i++){
            bairroList.add(baiiros[i]);
        }
  //////////// criando adapter e setando Layout

        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                estados);
        estadospi.setAdapter(adapter);

        ArrayAdapter adapterCidade = new ArrayAdapter(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                ci);
        cidadespi.setAdapter(adapterCidade);
        ArrayAdapter<String> adapterBairros = new ArrayAdapter(getApplicationContext()
                ,android.R.layout.simple_spinner_dropdown_item,bairroList);
        bairrospi.setAdapter(adapterBairros);
        /////////////// obtendo itens selecionado do sppiner////////////////////////////////////////////
        estadospi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                estadoEscolhido = estados.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        cidadespi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 cidadeEscolhida = ci.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        bairrospi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bairroEscolhido =bairroList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        }); }
}