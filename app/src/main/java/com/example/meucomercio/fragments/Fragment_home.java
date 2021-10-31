package com.example.meucomercio.fragments;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meucomercio.R;
import com.example.meucomercio.adapter.AdapterCardview;
import com.example.meucomercio.model.PostComercio;
import com.example.meucomercio.model.Usuario;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Inflater;

public class Fragment_home extends Fragment {

    private Spinner bairrospi;

    private RecyclerView recyclerViewCard;
    private FirebaseAuth auth =FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    PostComercio postComercio = new PostComercio();
    Usuario usuario1 = new Usuario();



    private FirestoreRecyclerAdapter <PostComercio, posVholder> adapterCardview;
    private  String bairroPost  ;


    private String[] baiiros={
            "Cajazeiras","Rio vermelho","Boca do Rio"
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragments_inicio, container, false);


          bairrospi = (Spinner) view.findViewById(R.id.spBairros);

         recyclerViewCard = (RecyclerView) view.findViewById(R.id.cardIni);

        //AdapterCardview adapterCardview = new AdapterCardview( );

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
                        holder.imageView.setImageResource(R.drawable.img1);
                        Glide.with(getContext()).load(postComercio.getUrlImg()).into(holder.imageView);
                        holder.notaPost.setText(postComercio.getNotaPost());
                        holder.localPost.setText("Bahia/Salvador/" + bairroPost);
                        holder.nomeUsuarioPost.setText(postComercio.getNomeUsuario());

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


               RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(getContext());
               recyclerViewCard.setLayoutManager(layoutManager);
               recyclerViewCard.setAdapter(adapterCardview);


               //Adicionando dados do FireStore no Cardview//////////////////////////////////////////////////////////////////////////////
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
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


        private TextView nome;
        private TextView data;
        private ImageView imageView;
        private TextView notaPost;
        private  TextView localPost;
        private  TextView nomeUsuarioPost;
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


            btnGotei.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });
        }

    }

    public void iniSpinmer(){
        List<String> bai = new ArrayList<>();

        bai.addAll(Arrays.asList(baiiros));

       // ArrayList opsBai = recuperarCidades();

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
