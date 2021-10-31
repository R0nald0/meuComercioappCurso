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
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class Fragment_addPost extends Fragment {
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private Button btAddImege;
    private ImageView imvCard;
    private EditText edTitulo;
    private TextView edData;
    private Button BtnsalvadrDados;
    private  EditText notaPost;
    private  String postUsuNome;
    private  TextView urdtxt;



    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private StorageReference storageR =storage.getReference();
    private PostComercio postComercio =new PostComercio();
    private Comercio comercio = new Comercio();
    private Usuario usuario1 = new Usuario();
    private Uri localImg;

    private String downImg;






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
       urdtxt = (TextView) view.findViewById(R.id.idurldown);



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

                salvarimgFirebase();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addPostagem();
                    }
                },3000);


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

      //////////////
        StorageReference referenceImg =storageR.child("images");
        StorageReference  spaceRef =storageR.child("images/"+numeros+"img.jpg");

        //obter dados do imgview
        imvCard.setDrawingCacheEnabled(true);
        imvCard.buildDrawingCache();

        Bitmap bitmap = ((BitmapDrawable) imvCard.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG,76,stream);

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
                 //  Toast.makeText(getContext(),"sUCESSO",Toast.LENGTH_SHORT).show();
               }
           });

           //Criando url de Dowload

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
                        Uri uri =task.getResult();
                        urdtxt.setText(uri.toString());
                        downImg =uri.toString();
                        Log.i("urll", ""+ uri);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                  Toast.makeText(getContext(),"urL ERRO"+ e.getMessage(),Toast.LENGTH_SHORT).show();
              }
          });

    }

    public void addPostagem(){
        Date data = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat hr = new SimpleDateFormat("HH:MM");

        String datahj =dateFormat.format(data);
        String hora  = hr.format(data);
        String horaDta =   datahj + "ás    " + hora;


        postComercio.setNomeComercio(edTitulo.getText().toString());
        postComercio.setDataPost( horaDta);
        postComercio.setIdUsuario(auth.getUid());
        postComercio.setNotaPost(notaPost.getText().toString());
        postComercio.setNomeUsuario(postUsuNome);
      //  postComercio.setUrlImg(urdtxt.getText().toString());
        postComercio.setUrlImg(downImg);


        vericarCampos();
        postComercio.salvarPost();
        limparCampos();
    }

    public  void limparCampos(){
         edTitulo.setText("");
         edData.setText("");
         notaPost.setText("");
         imvCard.setImageResource(R.drawable.ic_image_24);
         Toast.makeText(getContext(), "Comercio Salvo", Toast.LENGTH_SHORT).show();
    }

    public void vericarCampos(){
        if (edTitulo.getText().length() == 0){
            edTitulo.setError("Insira o nome um Titulo");
        }else if (notaPost.getText().length() == 0){
            notaPost.setError("Adicione uma Descrição");
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
                    Log.i("teste","erro"+ error.toString());
                }
            }
        });

    }

}
