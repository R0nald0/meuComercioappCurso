package com.example.meucomercio.fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import com.example.meucomercio.R;
import com.example.meucomercio.model.Comercio;
import com.example.meucomercio.model.PostComercio;
import com.example.meucomercio.model.Usuario;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
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

    private Button btAddImege;
    private ImageView imvCard;
    private EditText edTitulo;
    private TextView edData;
    private Button BtnsalvadrDados;
    private  EditText notaPost;


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



       btAddImege.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                adidcionarFoto();
           }
       });

      BtnsalvadrDados.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              addPostagem();
          }
      });

        return view;
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
                   Log.i("img"," " + localImg);
                   Toast.makeText(getContext(),"sUCESSO",Toast.LENGTH_SHORT).show();
               }
           });


    }

    public void addPostagem(){
        Date data = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat hr = new SimpleDateFormat("HH:MM");
        String datahj =dateFormat.format(data);
        String hora  = hr.format(data);
        String horaDta =   datahj + " ás" + hora;

        postComercio.setNomeComercio(edTitulo.getText().toString());
        postComercio.setDataPost( horaDta);
        postComercio.setIdUsuario(auth.getUid());
        postComercio.setNotaPost(notaPost.getText().toString());
        vericarCampos();
        salvarimgFirebase();
        postComercio.salvarPost();
        limparCampos();
    }

    public  void limparCampos(){
         edTitulo.setText("");
         edData.setText("");
         notaPost.setText("");
        Toast.makeText(getContext(), "Comercio Salvo", Toast.LENGTH_SHORT).show();
    }

    public void vericarCampos(){
        if (edTitulo.getText().length() == 0){
            edTitulo.setError("Insira o nome um Titulo");
        }else if (notaPost.getText().length() == 0){
            notaPost.setError("Adicione uma Descrição");
        }
    }



}
