package mobile.komputer.unsyiah.ac.id.editorsederhana;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

public class MainActivity extends AppCompatActivity {


    // Nama berkas untuk menyimpan isi editor
    private static String NAMA_BERKAS = "";
    private static final int PICKFILE_RESULT_CODE=1;

    String preferencesFirst = "isFirstTime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstTime();

    }

    public void firstTime(){
        SharedPreferences first = getSharedPreferences(preferencesFirst,0);
        String namaBerkas = first.getString("berkas","");
        if(first.getBoolean("firstTime",true)){

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent,PICKFILE_RESULT_CODE);

            first.edit().putBoolean("firstTime", false).apply();
        }else{
            NAMA_BERKAS = namaBerkas;
            berkas();
        }
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case PICKFILE_RESULT_CODE:
                if (resultCode==RESULT_OK){
                    NAMA_BERKAS = data.getData().getPath();
                    SharedPreferences first = getSharedPreferences(preferencesFirst,0);
                    SharedPreferences.Editor editor = first.edit();
                    editor.putString("berkas", NAMA_BERKAS);
                    editor.apply();
                    berkas();
                }
                break;
        }
    }

    public void berkas(){
        // Baca isi berkas
        String isiBerkas = bacaBerkas();

        // Ambil txtIsi dan isikan dengan isi berkas
        EditText txtIsi = (EditText) findViewById(R.id.txtIsi);
        txtIsi.setText(isiBerkas);

        // Pindahkan cursor ke akhir text
        txtIsi.setSelection(isiBerkas.length());
    }

    /**
     * Tangani penekanan tombol simpan.
     */
    public void clickBtnSimpan(View view) {
        // Ambil isi dari txtIsi
        EditText txtIsi = (EditText) findViewById(R.id.txtIsi);
        String isi = txtIsi.getText().toString();

        // Tulis isi ke berkas
        simpanBerkas(isi);
    }


    /**
     * Membaca semua isi suatu berkas text.
     *
     * @return Semua isi berkas.
     */
    private String bacaBerkas() {
        // Karena berkas hanya dibaca per baris maka perlu bantuan StringWriter untuk menggabungkan
        // semua baris yang dibaca
        StringWriter stringWriter = new StringWriter();

        FileInputStream berkasStream = null;
        try {
            // Buka berkas untuk dibaca, pakai buffer biar lebih efisien
            berkasStream = new FileInputStream(new File(NAMA_BERKAS));
            InputStreamReader berkasStreamReader = new InputStreamReader(berkasStream);
            BufferedReader berkasBuffered = new BufferedReader(berkasStreamReader);

            // Tandai ini masih membaca baris pertam
            boolean barisPertama = true;

            String satuBaris = null;
            try {
                // Baca satu baris
                satuBaris = berkasBuffered.readLine();
                while (satuBaris != null) { // Selagi masih ada baris yang masih bisa dibaca
                    // Periksa apakah ini baris pertama atau tidak.
                    // Jika baris pertama maka tidak ada \n sebelumnya
                    // jika bukan baris pertama maka ada \n untuk memisahkannya dengan baris
                    // sebelumnya
                    if (barisPertama == false)
                        stringWriter.write("\n");
                    else
                        barisPertama = false;

                    // Serahkan baris yang baru dibaca ke StringWriter agar dapat disambung dengan
                    // baris-baris yang telah dibaca sebelumnya.
                    stringWriter.write(satuBaris);

                    // Baca lagi satu baris
                    satuBaris = berkasBuffered.readLine();
                }
            }
            catch (IOException salah) {
                salah.printStackTrace();
            }
            finally {
                // Jangan lupa ditutup
                berkasBuffered.close();
            }
        }
        catch (FileNotFoundException salah) {
            salah.printStackTrace();
        }
        catch (IOException salah) {
            salah.printStackTrace();
        }

        // Kembalikan isi berkas
        return stringWriter.toString();
    }

    /**
     * Menulis ke suatu berkas text.
     *
     * @param isi Apa yang harus ditulis ke berkas.
     */
    private void simpanBerkas(String isi) {
        FileOutputStream berkasStream = null;
        try {
            // Buka berkas untuk ditulis, pakai buffer biar lebih efisien
            berkasStream = new FileOutputStream (new File(NAMA_BERKAS));
            OutputStreamWriter berkasStreamWriter = new OutputStreamWriter(berkasStream);
            BufferedWriter berkasBuffered = new BufferedWriter(berkasStreamWriter);

            try {
                // Tulis ke berkas
                berkasBuffered.write(isi);
            }
            catch (IOException salah) {
                salah.printStackTrace();
            }
            finally {
                // Jangan lupa ditutup
                berkasBuffered.close();
            }
        }
        catch (FileNotFoundException salah) {
            salah.printStackTrace();
        }
        catch (IOException salah) {
            salah.printStackTrace();
        }
    }
}