package com.example.meucomercio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meucomercio.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private Button btCdasto,btEntrar;
    private EditText edEmail,edSenha;
    private TextView txEmail,txSenha,esqSenha;

    FirebaseAuth auth =FirebaseAuth.getInstance();
    Usuario usuario1 =new Usuario();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btCdasto = (Button) findViewById(R.id.btncadastra);
        edEmail = (EditText)  findViewById(R.id.edemail);
        edSenha =(EditText) findViewById(R.id.edtsenha);
        txSenha = (TextView)findViewById(R.id.txsenha);
        txEmail =(TextView) findViewById(R.id.txemail);
        btEntrar = (Button)  findViewById(R.id.btnentrar);
        esqSenha = (TextView) findViewById(R.id.idEsqSenha);
    }

    @Override
    protected void onStart() {
        super.onStart();
        esqSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Recuperar senha",Toast.LENGTH_LONG).show();
            }
        });
        btCdasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,CadastroActivity.class);
                startActivity(intent);

            }
        });


        btEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario1.setEmail(edEmail.getText().toString());
                usuario1.setSenha(edSenha.getText().toString());
                verificaCampo();
            }
        });
    }

    public void verificaCampo(){
        if (edEmail.getText().length()  != 0 & edSenha.getText().length() != 0){
              LoginUser();
        }else  if (edEmail.getText().length() == 0){
            edEmail.setError("Preencha este campo");
        }else if (edSenha.getText().length() ==0){
            edSenha.setError("Preencha este campo");
        }
    }
    public void LoginUser(){
        auth.signInWithEmailAndPassword(usuario1.getEmail(),usuario1.getSenha())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                FirebaseUser user = auth.getCurrentUser();
                if (task.isSuccessful()){
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                }else {
                     String msgErroLogin ="";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                         msgErroLogin ="Email não Cadastrado";
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        msgErroLogin="Senha incorreta";
                    } catch (Exception e) {
                        msgErroLogin="Erro ao Fazer o login ";
                    }
                    Toast.makeText(getApplicationContext()," " + msgErroLogin,Toast.LENGTH_LONG).show();
                } }
        }); }
}