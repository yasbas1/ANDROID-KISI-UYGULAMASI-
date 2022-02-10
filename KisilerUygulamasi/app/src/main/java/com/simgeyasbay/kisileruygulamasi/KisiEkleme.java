package com.simgeyasbay.kisileruygulamasi;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.material.snackbar.Snackbar;
import com.simgeyasbay.kisileruygulamasi.databinding.ActivityKisiEklemeBinding;

import java.io.ByteArrayOutputStream;

public class KisiEkleme extends AppCompatActivity {
    private ActivityKisiEklemeBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;  //galeriye gitmek için
    ActivityResultLauncher<String> permissionLauncher;  //izin istemek için
    Bitmap image;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityKisiEklemeBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        registerLauncher();
        database=this.openOrCreateDatabase("Kisiler",MODE_PRIVATE,null);
        Intent intent=getIntent();
        String info=intent.getStringExtra("info");
        if(info.equals("new")){
            //intentten bağlantı aldı info new ise
            //yeni kişi ekleyecek
            binding.name.setText("");
            binding.numara.setText("");
            binding.mail.setText("");
            binding.tarih.setText("");
            binding.notlar.setText("");
            binding.resimekle.setImageResource(R.drawable.resimsecme);
            binding.save.setVisibility(View.VISIBLE);
            binding.mailgonder.setVisibility(View.INVISIBLE);
        }else {
            //old ise detaylar aktivitesine gidecek
            int id=intent.getIntExtra("id",0);
            binding.save.setVisibility(View.INVISIBLE);
            binding.mailgonder.setVisibility(View.VISIBLE);
            try {
                Cursor cursor=database.rawQuery("SELECT * FROM kisiler WHERE id= ?",new String[]{String.valueOf(id)});
                int isimindex=cursor.getColumnIndex("isim");
                int numaraindex=cursor.getColumnIndex("numara");
                int mailindex=cursor.getColumnIndex("mail");
                int tarihindex=cursor.getColumnIndex("tarih");
                int notlarindex=cursor.getColumnIndex("notlar");
                int resimindex=cursor.getColumnIndex("image");
                while (cursor.moveToNext()){
                    //tek tek değişkenleri değiştiriyor
                    binding.name.setText(cursor.getString(isimindex));
                    binding.numara.setText(cursor.getString(numaraindex));
                    binding.mail.setText(cursor.getString(mailindex));
                    binding.tarih.setText(cursor.getString(tarihindex));
                    binding.notlar.setText(cursor.getString(notlarindex));
                    //görseli önce byte tipinde dizide tutuyor
                    byte[] bytes=cursor.getBlob(resimindex);
                    //görseli bitmape çeviriyor
                    Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    binding.resimekle.setImageBitmap(bitmap);
                }
                cursor.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    //kaydet butonu işlemleri
    public void save(View view){
        //bütün verileri kaydediyoruz
        String isim=binding.name.getText().toString();
        String numara=binding.numara.getText().toString();
        String mail=binding.mail.getText().toString();
        String tarih=binding.tarih.getText().toString();
        String notlar=binding.notlar.getText().toString();

        //görseli bir byte dizisi tipindeki dizilerde tutuyoruz database'e kaydetmek için
        Bitmap smallimage=makeSmallerImage(image,300);
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        smallimage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteArray=outputStream.toByteArray();
        try {
            //değerleri uygulama,telefon kapandığında kaybetmemek için
            //database'e kaydediyoruz

            database.execSQL("CREATE TABLE IF NOT EXISTS kisiler (id INTEGER PRIMARY KEY, isim VARCHAR, numara VARCHAR, mail VARCHAR, tarih VARCHAR, notlar VARCHAR, image BLOB)");
            //değerleri kullanıcıdan alacağız
            String sqlString ="INSERT INTO kisiler(isim,numara,mail,tarih,notlar,image) VALUES(?,?,?,?,?,?)";
            //bağlama(binding) işlemi için
            SQLiteStatement sqLiteStatement=database.compileStatement(sqlString);
            sqLiteStatement.bindString(1,isim);
            sqLiteStatement.bindString(2,numara);
            sqLiteStatement.bindString(3,mail);
            sqLiteStatement.bindString(4,tarih);
            sqLiteStatement.bindString(5,notlar);
            sqLiteStatement.bindBlob(6,byteArray);
            sqLiteStatement.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
        //kayıttan sonra maine dönmek için intent
        Intent intent=new Intent(KisiEkleme.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //çalışan bütün aktiviteler kapanır sadece gideceğimiz çalışır
        startActivity(intent);
    }


    //eklenen fotoğraflar büyük boyutlu ise
    //uygulamada hata vermemesi için küçük resimlere
    //dönüştüren fonksiyon
    public Bitmap makeSmallerImage(Bitmap image,int maxSize){
        //fotoğrafın boyutunu ayarlama
        //fotoğraf dikey ya da yataysa ona göre ayarlama yapacak
        int en=image.getWidth();
        int boy=image.getHeight();
        float bitmapOran=(float) en/(float) boy;
        if (bitmapOran>1){
            //görsel yataysa
            en=maxSize;
            boy=(int) (en/bitmapOran);
        }else{
            //görsel dikeyse
            boy=maxSize;
            en=(int) (boy*bitmapOran);
        }
        return image.createScaledBitmap(image,en,boy,true);
    }



    public void resimekle(View view){
        //izinleri kontrol etmek için gerekli
        //ilk kısım istediğimiz izin,permission granted izin verildi anlamına gelir
        //burada kontrol yapıyor eğer izin verilmediyse
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){  //daha önce izin verilmediyse ilk buraya girer
            //neden izin istediğimizi kullanıcıya gösterme
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                //Snackbar ile izin onayı almak
                //daha önce izin verilmediyse snackbar ile uyarı veriliyor
                Snackbar.make(view,"GALERİYE ERİŞMEK İÇİN İZİN GEREKLİ",Snackbar.LENGTH_INDEFINITE).setAction("İZİN VER", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //izin isteme
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);


                    }
                }).show();

            }else{
                //tekrar izin iste
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }else{
            //izin verildiyse(daha önce izin verildiyse tekrar kontrol etmeden buraya girer)
            //Uri nereye gideceğimizi belirtir
            //ilk kısım aksiyonla ne yapacağımızı belirtir
            //ikinci kısım galeri yolu kalıbı
            Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }
    }
    public void send(View view){
        //mail gönderme işlemi
        String mail=binding.mail.getText().toString();
        Intent intent=new Intent(KisiEkleme.this,MailActivity.class);
        //mail hesabı bilgisini diğer aktiviteye aktarıyor
        intent.putExtra("mail",mail);
        startActivity(intent);

    }


    private void registerLauncher(){
        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK){  //kullanıcı bir şey seçti mi kontrolü
                    Intent intentFromResult=result.getData();  //data alımı
                    Uri imagedata=intentFromResult.getData();  //datanın kayıtlı olduğu yer uri
                    //aldığımız veriyi databasede tutacağımız için bitmape çevirmeliyiz
                    try{
                        if(Build.VERSION.SDK_INT>=28){  //image decoder sdk 28den büyük olduğunda çalışabiliyor
                            ImageDecoder.Source source=ImageDecoder.createSource(getContentResolver(),imagedata);  //görsellerle ilgili işlem yapmamızı sağlar
                            image=ImageDecoder.decodeBitmap(source);
                            binding.resimekle.setImageBitmap(image);
                        }else{
                            image= MediaStore.Images.Media.getBitmap(getContentResolver(),imagedata);
                            binding.resimekle.setImageBitmap(image);
                        }
                    }catch (Exception e){
                        e.printStackTrace();

                    }
                }
            }
        });

        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                //result true ise izin verildi
                if(result){
                    Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }else {
                    Toast.makeText(KisiEkleme.this, "İZİN GEREKLİ", Toast.LENGTH_LONG).show();
                    //izin verilmediyse Toast mesajı geliyor
                }
            }
        });
    }
}