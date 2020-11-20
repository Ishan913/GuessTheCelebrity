package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebURLs = new ArrayList<String >();
    ArrayList<String> celebNames = new ArrayList<String>();
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    int chosenCeleb = 0;
    String[] answerOptions = new String[4];
    int locationOfCorrectAnswer =0;
    ImageView imageView;

    public void checkAnswer(View view){
        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){
            Toast.makeText(this, "CORRECT!", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Wrong! It was " + celebNames.get(chosenCeleb), Toast.LENGTH_SHORT).show();
        }

        newQuestion();
    }

    public void newQuestion(){
        try {
            Random random = new Random();
            chosenCeleb = random.nextInt(celebURLs.size());

            ImageDownloader imageTask = new ImageDownloader();
            Bitmap celebImage = imageTask.execute(celebURLs.get(chosenCeleb)).get();
            imageView.setImageBitmap(celebImage);

            locationOfCorrectAnswer = random.nextInt(4);

            for (int i=0;i<4;i++){
                if (i==locationOfCorrectAnswer){
                    answerOptions[i]= celebNames.get(chosenCeleb);
                }
                else {
                    int temp = random.nextInt(celebNames.size());
                    while (temp==chosenCeleb){
                        temp = random.nextInt(celebNames.size());
                    }
                    answerOptions[i]=celebNames.get(temp);
                }
            }
            button0.setText(answerOptions[0]);
            button1.setText(answerOptions[1]);
            button2.setText(answerOptions[2]);
            button3.setText(answerOptions[3]);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadTask task = new DownloadTask();
        String result = null;
        imageView = findViewById(R.id.imageView2);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        try {
            result = task.execute("https://www.imdb.com/list/ls052283250").get();

            String[] splitResult = result.split("class=\"lister-list\"");
            String[] splitResult2 = splitResult[1].split("class=\"footer filmosearch\"");

            Pattern pattern = Pattern.compile("src=\"(.*?)\"");
            Matcher matcher = pattern.matcher(splitResult2[0]);

            while (matcher.find()){
                celebURLs.add(matcher.group(1));
            }

            Pattern pattern1 = Pattern.compile("<img alt=\"(.*?)\"");
            Matcher matcher1 = pattern1.matcher(splitResult2[0]);
            while (matcher1.find()){
                celebNames.add(matcher1.group(1));
            }

            newQuestion();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result="";

            try {
                URL url = new URL(urls[0]);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

                String input = null;
                StringBuffer stringBuffer = new StringBuffer();
                while (true){
                    if (!((input = in.readLine()) != null))
                        break;
                    stringBuffer.append(input);
                }
                in.close();
                return  stringBuffer.toString();


            } catch (Exception e) {
                e.printStackTrace();
                return  null;
            }


            /*
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data!=-1){
                    char current = (char) data;
                    result+= current;
                    data=reader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }*/
        }
    }

}