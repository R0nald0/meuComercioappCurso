package com.example.meucomercio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.meucomercio.fragments.Fragment_addPost;
import com.example.meucomercio.fragments.Fragment_addcomercio;
import com.example.meucomercio.fragments.Fragment_home;
import com.example.meucomercio.model.Usuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

 private FirebaseAuth auth = FirebaseAuth.getInstance();
 private androidx.appcompat.widget.Toolbar toolbar;

 private BottomNavigationView bttNavView;

 private Usuario usuario1 =new Usuario();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

     bttNavView = (BottomNavigationView) findViewById(R.id.bottomNavigationView2);
     getSupportFragmentManager().beginTransaction().replace(R.id.framalayout,new Fragment_home()).commit();

        toolbar = (Toolbar) findViewById(R.id.toobar);
        toolbar.setTitle(R.string.meu_com_rcio);
        setSupportActionBar(toolbar);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuconfi,menu);

        return  true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.idItsair:
                  deslogar();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }


    }

    @Override
    protected void onStart() {
        super.onStart();




        bttNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragmentEscolhida = null;

                switch (item.getItemId()){
                    case R.id.home:
                        fragmentEscolhida = new Fragment_home();
                        break;

                    case R.id.mComercio:
                        fragmentEscolhida = new Fragment_addcomercio();
                        break;

                    case R.id.post:
                        fragmentEscolhida = new Fragment_addPost();
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.framalayout,fragmentEscolhida).commit();

                return true;
            }
        });


    }
  public  void  deslogar(){
      auth.signOut();
      startActivity(new Intent(MainActivity.this,LoginActivity.class));
      finish();
  }

}