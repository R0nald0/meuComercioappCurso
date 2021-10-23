package com.example.meucomercio.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meucomercio.R;
import com.example.meucomercio.model.PostComercio;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class AdapterCardview extends RecyclerView.Adapter<AdapterCardview.Vholder> {

    PostComercio postagem = new PostComercio();


    @NonNull
    @Override
    public Vholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listaItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardlayout,parent,false); //passar layout Para view


        return new Vholder(listaItem);
    }

    @Override
    public void onBindViewHolder(@NonNull Vholder holder, int position) { //holder recupera os atributos do layout



        holder.nome.setText(postagem.getNomeComercio());
        holder.data.setText("12/02/2001");
        holder.imageView.setImageResource(R.drawable.img1);
       // holder.notaPost.setText(postagem.getNotaPost());
    }

    @Override
    public int getItemCount() {
        return 4;
    }
       private View view;

    public class Vholder extends RecyclerView.ViewHolder{

          private TextView nome;
          private TextView data;
          private ImageView imageView;
          private  TextView notaPost;

        public Vholder(@NonNull View itemView) {
            super(itemView);

            view = imageView;

            nome =(TextView) itemView.findViewById(R.id.cardVnome);
            data = (TextView)  itemView.findViewById(R.id.cardVdata);
            imageView = (ImageView)  itemView.findViewById(R.id.cardVimage);
            notaPost = (TextView)   itemView.findViewById(R.id.cardVNota);


        }
    }




}
