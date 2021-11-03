package com.example.meucomercio.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.meucomercio.R;
import com.example.meucomercio.model.Comercio;
import com.example.meucomercio.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Fragment_addcomercio extends Fragment {

    private EditText nomeComecio,localizacao;
    private TextView nomeUsuario;
    private Button  btnSalvarComer;

   private FirebaseAuth auth = FirebaseAuth.getInstance();
   private FirebaseFirestore db=FirebaseFirestore.getInstance();
   private Usuario usuario1= new Usuario();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addcomercio,container,false);
        nomeComecio =(EditText) view.findViewById(R.id.edtNomeComer);
        nomeUsuario =(TextView) view.findViewById(R.id.txComerNomeUSER);
        localizacao = (EditText)  view.findViewById(R.id.localComercio);
        btnSalvarComer = (Button)  view.findViewById(R.id.edSalvaComercio);
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        btnSalvarComer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarComercio();
            }
        });
        buscarCampoUsuario();
    }
    public void vericarCampos(){
        if (nomeComecio.getText().length() ==0 ){
              nomeComecio.setError("Adcione o nome do comércio");
        }else if(localizacao.getText().length() ==0 ){
             localizacao.setError("Insira a localização");
        }
    }
    public void  salvarComercio(){

        Comercio comercio = new Comercio();

        comercio.setNome(nomeComecio.getText().toString());
        comercio.setLocalizacao(localizacao.getText().toString());
        comercio.setIdUsuario(auth.getCurrentUser().getUid());
        vericarCampos();
        comercio.addComercio();
        limparCampos();
    }
    public  void limparCampos(){
        nomeComecio.setText("");
        localizacao.setText("");
        Toast.makeText(getContext(), "Comercio Salvo", Toast.LENGTH_SHORT).show();
    }
    public void buscarCampoUsuario(){

        DocumentReference documentReference =db.collection("Usuarios").document(auth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null){
                    nomeUsuario.setText(value.getString("nome"));
                }else {
                    Log.i("teste","erro"+ error.toString());
                }
            }
        });
    }


}
