package com.simgeyasbay.kisileruygulamasi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.simgeyasbay.kisileruygulamasi.databinding.ActivityMailBinding;

public class MailActivity extends AppCompatActivity {
    private ActivityMailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMailBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        Intent intent=getIntent();
        String mail=intent.getStringExtra("mail");
        binding.textView2.setText(mail);


    }
    public void gonder(View view){
        //MAİL GONDERİLDİ
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();//fragment başlatma

        GonderildiFragment gonderildiFragment=new GonderildiFragment();
        fragmentTransaction.add(R.id.frame_layout,gonderildiFragment).commit();

    }
}