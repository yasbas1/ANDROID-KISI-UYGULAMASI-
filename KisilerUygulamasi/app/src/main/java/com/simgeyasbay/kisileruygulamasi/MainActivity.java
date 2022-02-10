package com.simgeyasbay.kisileruygulamasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.simgeyasbay.kisileruygulamasi.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;  //id ile tasarımdaki itemlere erişmek için binding
    ArrayList<Kisi> kisiArrayList;
    KisiAdapter kisiAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        kisiArrayList =new ArrayList<>();
        //recyclerView ile KişiAdapterünü birbirine bağlıyoruz
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        kisiAdapter = new KisiAdapter(kisiArrayList);
        binding.recyclerView.setAdapter(kisiAdapter);
        veriAl();
    }

    //databaseden verileri çekme
    private void veriAl(){
        try {
            SQLiteDatabase sqLiteDatabase=this.openOrCreateDatabase("Kisiler",MODE_PRIVATE,null);
            Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM kisiler",null);
            //görüntülemede sadece isim ve id çekmek yeterli olur
            int isimindex=cursor.getColumnIndex("isim");
            int idindex=cursor.getColumnIndex("id");
            while (cursor.moveToNext()){
                String isim=cursor.getString(isimindex);
                int id=cursor.getInt(idindex);
                Kisi kisi=new Kisi(isim,id);
                kisiArrayList.add(kisi);
            }
            kisiAdapter.notifyDataSetChanged();  //adaptere güncelleme geldiğinde verileri göstermesi için
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    //MENÜ TANIMLAMA İŞLEMİ BURADA OLUR
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.kisi_menu,menu); //eklediğimiz menüyü bağlama
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //MENÜDE TIKLAMA YAPINCA BAĞLAMA İŞLEMİ BURADA OLUR
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.kisi_ekle){//eğer ekleme işlemi seçildiyse
            Intent intent=new Intent(this,KisiEkleme.class);  //intent ile aktivite geçişi
            //yeni bir nesne eklemek istiyor ise new değeri alacak intent ile aktaracak
            //böylece kontrol edip yeni nesne ekleme aktivitesine gidecek
            intent.putExtra("info","new");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}