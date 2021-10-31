package com.example.meucomercio.model;

import android.net.Uri;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class PostComercio {
    private String idPost;
    private String idUsuario;
    private String nomeUsuario;
    private String tituloComercio;
    private String dataPost;
    private String urlImg;
    private String notaPost;


    public PostComercio() {
    }

    public void salvarPost() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        DocumentReference documentReference = db.collection("Post").document();

        documentReference.set(this).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("teste", e.getMessage());
            }
        });


    }

    public void teste() {

    }

    public void RecuperarDados() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Post")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                //Log.i("query",documentSnapshot.getId() + "=>" + documentSnapshot.getData());

                            }
                        } else {
                            Log.i("query", "Erro" + task.getException());
                        }
                    }
                });


    }


    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getNomeComercio() {
        return tituloComercio;
    }

    public void setNomeComercio(String nomeComercio) {
        this.tituloComercio = nomeComercio;
    }

    public String getDataPost() {
        return dataPost;
    }

    public void setDataPost(String dataPost) {
        this.dataPost = dataPost;
    }

    public String getNotaPost() {
        return notaPost;
    }

    public void setNotaPost(String notaPost) {
        this.notaPost = notaPost;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }
}
