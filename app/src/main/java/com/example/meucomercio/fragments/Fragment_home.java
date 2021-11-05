package com.example.meucomercio.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meucomercio.Detalhes;
import com.example.meucomercio.R;
import com.example.meucomercio.model.PostComercio;
import com.example.meucomercio.model.Usuario;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Fragment_home extends Fragment {

    private Spinner bairrospi;

    private RecyclerView recyclerViewCard;
    private FirebaseAuth auth =FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private PostComercio postComercio = new PostComercio();
    private Usuario usuario1 = new Usuario();
    private Uri local;

    private FirestoreRecyclerAdapter <PostComercio, posVholder> adapterCardview;
    private String bairroPost  ;
    private String[] baiiros={
            "Cajazeiras","Rio vermelho","Boca do Rio"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragments_inicio, container, false);
         bairrospi = (Spinner) view.findViewById(R.id.spBairros);

         recyclerViewCard = (RecyclerView) view.findViewById(R.id.cardIni);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==RESULT_OK && resultCode == RESULT_OK && data !=null){
            local =data.getData();
        }

    }
    @Override
    public void onStart() {
        super.onStart();

        //Adicionando dados do FireStore no Cardview//////////////////////////////////////////////////////////////////////////////
        Query query= db.collection("Post").orderBy("dataPost", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<PostComercio> options = new FirestoreRecyclerOptions.Builder<PostComercio>()
                .setQuery(query,PostComercio.class )
                .build();

        adapterCardview = new FirestoreRecyclerAdapter<PostComercio, posVholder>(options) {
            @NonNull
            @Override
            public posVholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View viewC = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardlayout,parent,false);


                return new posVholder(viewC);
            }

            @Override
            protected void onBindViewHolder(@NonNull posVholder holder, int i, @NonNull PostComercio postComercio) {

                holder.nome.setText(postComercio.getNomeComercio());
                holder.data.setText(postComercio.getDataPost());
                Glide.with(getContext()).load(postComercio.getUrlImg()).into(holder.imageView);

                holder.notaPost.setText(postComercio.getNotaPost());
                holder.localPost.setText("Bahia/Salvador/" + bairroPost);
                holder.nomeUsuarioPost.setText(postComercio.getNomeUsuario());




                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String img = postComercio.getUrlImg();

                        Intent intent = new Intent(getContext(), Detalhes.class);
                        intent.putExtra( "nome",holder.nome.getText());
                        intent.putExtra( "nomeUser",holder.nomeUsuarioPost.getText());
                        intent.putExtra( "img",img);
                        intent.putExtra( "data",holder.data.getText());
                        intent.putExtra("nota",holder.notaPost.getText());

                        startActivity(intent);
                    }
                });

                holder.btnGotei.setOnClickListener(new View.OnClickListener() {
                    int cont = 0;
                    @Override
                    public void onClick(View v) {
                        cont = cont +1;
                        holder.btnGotei.setText("("+cont +") Gostei");

                    }
                });
            }
        };

        //////////////Cria o layou e set adptador////////////////////
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(getContext());
        recyclerViewCard.setLayoutManager(layoutManager);
        recyclerViewCard.setAdapter(adapterCardview);

         iniSpinmer();
        adapterCardview.startListening();
        FirebaseUser user =auth.getCurrentUser();


    }
    @Override
    public void onStop() {
        super.onStop();
      adapterCardview.stopListening();
    }

   private   class posVholder extends RecyclerView.ViewHolder {
        private TextView nome,nomeUsuarioPost,data,notaPost,localPost;
        private ImageView imageView;
        private Button btnGotei;

        public posVholder(@NonNull View itemView) {
            super(itemView);
            nome = (TextView) itemView.findViewById(R.id.cardVnome);
            data = (TextView) itemView.findViewById(R.id.cardVdata);
            imageView = (ImageView) itemView.findViewById(R.id.cardVimage);
            notaPost = (TextView) itemView.findViewById(R.id.cardVNota);
            localPost = (TextView) itemView.findViewById(R.id.cardLocal);
            nomeUsuarioPost = (TextView) itemView.findViewById(R.id.cardVnomeUsuario);
            btnGotei = (Button) itemView.findViewById(R.id.cardVbtnGostei);


        }
    }
    public void iniSpinmer(){
        List<String> bai = new ArrayList<>();
        bai.addAll(Arrays.asList(baiiros));

        ArrayAdapter adapterBairros = new ArrayAdapter(getContext(),
                android.R.layout.simple_spinner_dropdown_item,bai);
        bairrospi.setAdapter(adapterBairros);

        bairrospi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bairroPost = bai.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
