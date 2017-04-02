package com.imperialsoupgmail.TalkMan;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import java.util.Locale;
import com.googlecode.tesseract.android.TessBaseAPI;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    Bitmap img;
    private TessBaseAPI mTes;
    String datapath = "";
    TextToSpeech t2S;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t2S = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    t2S.setLanguage(Locale.US);
                    button.setEnabled(true);
                    TextToSpeechFunction();
                }
            }
        });

        button = (Button) findViewById(R.id.button);
        img = BitmapFactory.decodeResource(getResources(), R.drawable.ct);
        String language = "eng";
        datapath = getFilesDir()+ "/tesseract/";
        mTes = new TessBaseAPI();

        checkFile(new File(datapath + "tessdata/"));

        mTes.init(datapath, language);


        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                TextToSpeechFunction() ;
            }

        });
    }
    String ocrResult = null;
    public void processImage(View view){
        mTes.setImage(img);
        ocrResult = mTes.getUTF8Text();
        TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView);
        OCRTextView.setText(ocrResult);
    }


    public void TextToSpeechFunction()
    {

        String textHolder = ocrResult ;
        t2S.speak(textHolder, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onDestroy() {

        t2S.shutdown();

        super.onDestroy();
    }


    private void checkFile(File dir) {
        if (!dir.exists()&& dir.mkdirs()){
                copyFiles();
        }
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);

            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    private void copyFiles() {
        try {
            String filepath = datapath + "/tessdata/eng.traineddata";
            AssetManager assetManager = getAssets();

            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }


            outstream.flush();
            outstream.close();
            instream.close();

            File file = new File(filepath);
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
