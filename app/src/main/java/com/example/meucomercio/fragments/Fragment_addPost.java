package com.example.meucomercio.fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.meucomercio.R;
import com.example.meucomercio.model.Comercio;
import com.example.meucomercio.model.PostComercio;
import com.example.meucomercio.model.Usuario;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class Fragment_addPost extends Fragment {
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private Button btAddImege,BtnsalvadrDados;
    private ImageView imvCard;
    private EditText edTitulo,notaPost;
    private TextView edData;
    private  String postUsuNome,downImg;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private StorageReference storageR =storage.getReference();
    private PostComercio postComercio =new PostComercio();
    private Comercio comercio = new Comercio();
    private Usuario usuario1 = new Usuario();
    private Uri localImg;


    @SuppressLint("WrongThread")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_addpost,container,false);

       btAddImege =(Button) view.findViewById(R.id.btnPostImg);
       imvCard =(ImageView) view.findViewById(R.id.imgvComPost);
       edTitulo=(EditText) view.findViewById(R.id.edtComTitulo);
       edData =(TextView) view.findViewById(R.id.edtComDataPost);
       notaPost = (EditText) view.findViewById(R.id.idaddPostNota);
       BtnsalvadrDados= (Button) view.findViewById(R.id.edtComSalvarPost);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

       buscarCampoUsuario();
        btAddImege.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adidcionarFoto();
            }
        });

        BtnsalvadrDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vericarCampos();
            }
        });
    }

    public void adidcionarFoto(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,RESULT_OK);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==RESULT_OK && resultCode == RESULT_OK && data !=null){
           localImg =data.getData();
            Bitmap img = null;
            try {
                img = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(),localImg);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.PNG,74,stream);
                imvCard.setImageBitmap(img);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void salvarimgFirebase(){
       // numeros randomicos que nao se repetem
        Set<Integer> numeros = new TreeSet<>();
        Random random= new Random();
        numeros.add(random.nextInt());

        StorageReference referenceImg =storageR.child("images");
        StorageReference  spaceRef =storageR.child("images/"+numeros+"img.jpg");

        //obter dados do imgview
        imvCard.setDrawingCacheEnabled(true);
        imvCard.buildDrawingCache();

        Bitmap bitmap = ((BitmapDrawable) imvCard.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG,76,stream);
        ///////////salva img no firebase//////////////
       byte[] dados =stream.toByteArray();//uploado com putBytes
          UploadTask task = spaceRef.putBytes(dados);

           task.addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   Toast.makeText(getContext(),"errro"+ e.getMessage(),Toast.LENGTH_SHORT).show();
               }
           }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
               @Override
               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                 //  Toast.makeText(getContext(),"sucesso",Toast.LENGTH_SHORT).show();
               }
           });

           ///////////////////////Criando url de Dowload////////////////////////////////////
            Task<Uri> uriTask =task.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if ( !task.isSuccessful()){
                        throw  task.getException();
                    }
                    return spaceRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (!task.isSuccessful()){
                        Toast.makeText(getContext(),"urL ERRO ao gerar url",Toast.LENGTH_SHORT).show();
                    }else{
                        if (task.isComplete()){
                            downImg =task.getResult().toString();
                            addPostagem();
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                  Toast.makeText(getContext(),"urL ERRO: "+ e.getMessage(),Toast.LENGTH_SHORT).show();
              }
          });
    }

    public void addPostagem(){

        String timer = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        postComercio.setNomeComercio(edTitulo.getText().toString());
        postComercio.setDataPost( timer);
        postComercio.setIdUsuario(auth.getUid());
        postComercio.setNotaPost(notaPost.getText().toString());
        postComercio.setNomeUsuario(postUsuNome);
        postComercio.setUrlImg(downImg);

        postComercio.salvarPost();
        limparCampos();
    }
    public  void limparCampos(){
         edTitulo.setText("");
         edData.setText("");
         notaPost.setText("");
         imvCard.setImageResource(R.drawable.ic_image_24);
         Toast.makeText(getContext(), "Seu Post foi adicionado", Toast.LENGTH_SHORT).show();
    }
    public void vericarCampos(){
        if (edTitulo.getText().length() != 0 & notaPost.getText().length() != 0 & imvCard.getDrawable() != null){
            Toast.makeText(getContext(),"Aguarde um Momento",Toast.LENGTH_LONG).show();
            salvarimgFirebase();
        }else if (edTitulo.getText().length() == 0){
            edTitulo.setError("Adicione uma O nome do Comércio");
        }else  if (imvCard.getDrawable() == null){
             notaPost.setError("Adcione uma imagem no campo Acima,toque no botao 'ADCIONAR IMAGEM'");
             
        }else if (notaPost.getText().length() == 0){
            notaPost.setError("Insira uma Descrição");

        }
    }
    public void buscarCampoUsuario(){
        DocumentReference documentReference =db.collection("Usuarios").document(auth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {

            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null){
                    postUsuNome=value.getString("nome");
                }else {
                    Log.i("teste","erro: "+ error.toString());
                }
            }
        });

    }

}
