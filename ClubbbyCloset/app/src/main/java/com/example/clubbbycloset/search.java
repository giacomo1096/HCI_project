package com.example.clubbbycloset;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static androidx.core.content.FileProvider.getUriForFile;

public class search extends AppCompatActivity {
    private static final String FILE_TOPICS = "topics.txt";
    ImageView bhome, bsearch, badd, bvote, bprofile;
    EditText stopic;

    GridLayout gridLayout;

    public static String id;

    private static final String FILE_ALLUSERS = "allUsersData.txt";
    private static final String FILE_USER = "userdata.txt";
    private static final String FILE_USERVOTE ="uservote.txt";
    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_LOAD_VOTE = 2;
    private static int REQUEST_IMAGE_CAPTURE = 3;
    private static int REQUEST_VOTE_IMAGE_CAPTURE = 4;
    String currentPhotoPath;
    String currentVotePhotoPath1;
    String currentVotePhotoPath2;
    int n;


    private String picturePath = "";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Bundle Extra = getIntent().getExtras();
        id = Extra.getString("idProfile");

        bhome = (ImageView) this.findViewById(R.id.home);
        bsearch = (ImageView) this.findViewById(R.id.search);
        badd = (ImageView) this.findViewById(R.id.add);
        bvote = (ImageView) this.findViewById(R.id.vote);
        bprofile = (ImageView) this.findViewById(R.id.profile);

        stopic = (EditText)this.findViewById(R.id.et_search);

        gridLayout = (GridLayout) this.findViewById(R.id.grid);
        gridLayout.removeAllViews();
        setGridSearchLayout(FILE_TOPICS, gridLayout);

        stopic.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String insert = String.valueOf(stopic.getText());
                    if(insert.contains(";") || insert.contains(":")){
                        Toast.makeText(getApplicationContext(), "Invalid String: ';' and ':' are not allow", Toast.LENGTH_SHORT).show();
                    }else{
                        String[] forSearch = load(FILE_TOPICS).split(";;;");
                        String ts = "";
                        for( int i =0; i<forSearch.length; i++){
                            String items = forSearch[i].split(";;")[0];
                            items.replace(" ", "");
                            String[] topics= items.split(":");
                            for(int j = 0 ; j<topics.length; j++){
                                if (topics[j].contains(insert)){
                                    if(ts.equals("")){
                                        ts = insert+ ";" + i + ":";
                                    }else {
                                        ts = ts + i + ":";
                                    }
                                }
                            }
                        }
                        switchActivity(ts);
                    }
                }
                return false;
            }
        });

        bhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(search.this, home.class);
                home.putExtra("idProfile", id);
                startActivity(home); // takes the user to the signup activity
            }

        });

        bsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent search = new Intent(search.this, search.class);
                search.putExtra("idProfile", id);
                startActivity(search); // takes the user to the signup activity
            }

        });

        bprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(search.this, profile.class);
                profile.putExtra("type", "0");
                profile.putExtra("idProfile", id);
                startActivity(profile); // takes the user to the signup activity
            }

        });

        bvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent vote = new Intent(search.this, vote.class);
                vote.putExtra("idProfile", id);
                startActivity(vote); // takes the user to the signup activity
            }

        });


        badd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(search.this, badd);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Add photo from gallery")){
                            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(i, RESULT_LOAD_IMAGE);
                        }
                        else if (item.getTitle().equals("Add photos from gallery")){
                            Toast.makeText(getApplicationContext(), "Select two pictures", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            startActivityForResult(i, RESULT_LOAD_VOTE);
                        }
                        else if (item.getTitle().equals("Take a picture")){
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            // Ensure that there's a camera activity to handle the intent
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                // Create the File where the photo should go
                                File photoFile = null;
                                try {
                                    photoFile = createImageFile();
                                } catch (IOException ex) {
                                }
                                // Continue only if the File was successfully created
                                if (photoFile != null) {
                                    Uri photoURI = getUriForFile(search.this,"com.example.clubbbycloset",photoFile);
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                }
                            }

                        }
                        else if (item.getTitle().equals("Take pictures")){
                            Toast.makeText(getApplicationContext(), "Take two pictures", Toast.LENGTH_LONG).show();
                            n=1;
                            StartActivity();
                        }
                        return true;
                    }
                });
                popup.show();//showing popup menu
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        verifyStoragePermissions(this);
        if (requestCode == RESULT_LOAD_IMAGE) {
            newImg(requestCode,resultCode,data);
        }else if(requestCode == RESULT_LOAD_VOTE) {
            try {
                newVote(requestCode,resultCode,data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            takeAPicture(resultCode, data);
        }else if (requestCode == REQUEST_VOTE_IMAGE_CAPTURE) {
            takeAVotePictures(resultCode, data);
        }
    }

    private void StartActivity() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createVoteFile();
            } catch (IOException ex) {
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = getUriForFile(search.this, "com.example.clubbbycloset", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_VOTE_IMAGE_CAPTURE);
            }
        }
    }

    private void takeAVotePictures(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if(n == 2){
                try {
                    String paths = currentVotePhotoPath1 + ":" + currentVotePhotoPath2;
                    loadVoteImg(paths, FILE_USERVOTE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent voteView = new Intent(search.this, voteView.class);
                voteView.putExtra("numb", "0");
                voteView.putExtra("idProfile", id);
                startActivity(voteView);
            }else {
                n = 2;
                StartActivity();
            }
        }

    }

    private File createVoteFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        if(n == 1){
            // Save a file: path for use with ACTION_VIEW intents
            currentVotePhotoPath1= image.getAbsolutePath();
        }else{
            currentVotePhotoPath2= image.getAbsolutePath();
        }
        return image;
    }

    private void takeAPicture(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String imgId = null;
            String[] d = load(FILE_USER).split(";;");
            for(int i=0; i<d.length; i++){
                String[] src = d[i].split(";");
                if(src[0].split(":")[1].equals(id)){
                    imgId = src[2].split(":")[1];
                }
            }
            Intent imgVote = new Intent(search.this, imgView.class);
            imgVote.putExtra("numb", "0");
            imgVote.putExtra("idProfile", id);
            imgVote.putExtra("forTopicFile", ";;" + id + ":" + imgId + ";imgSrc:" +  currentPhotoPath );
            imgVote.putExtra("forImgSet", id + ":" + imgId + ";imgSrc:" +  currentPhotoPath );
            startActivity(imgVote);

        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setGridSearchLayout(String FILE_NAME, GridLayout grid) {
        String[] res = load(FILE_NAME).split(";;;");

        LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int buttons= res.length;//the number of bottons i have to put in GridLayout
        int buttonsForEveryRow = 2; // buttons i can put inside every single row
        int buttonsForEveryRowAlreadyAddedInTheRow =0; // count the buttons added in a single rows
        int columnIndex=0; //cols index to which i add the button
        int rowIndex=0; //row index to which i add the button
        for(int i=0; i < buttons;i++) {
                String[] resS = res[i].split(";;");
                String txtSrc = resS[0].split(":")[1];
                String imgSrc = resS[1].split(";")[1].split(":")[1];

                View view = inflater.inflate(R.layout.search_img_frame, null);

                ImageView newi = (ImageView) view.findViewById(R.id.topicimg);
                int idS = getResources().getIdentifier(imgSrc,"drawable", "com.example.clubbbycloset");
                Bitmap bm = BitmapFactory.decodeResource(getResources(), idS);
                Bitmap resized = Bitmap.createScaledBitmap(bm, 550, 600, false);
                newi.setImageBitmap(resized);

                TextView txt = (TextView) view.findViewById(R.id.topic);
                txt.setText(txtSrc);

                /*if numeroBottoniPerRigaInseriti equals numeroBottoniPerRiga i have to put the other buttons in a new row*/
                if (buttonsForEveryRowAlreadyAddedInTheRow == buttonsForEveryRow) {
                    rowIndex++; //here i increase the row index
                    buttonsForEveryRowAlreadyAddedInTheRow = 0;
                    columnIndex = 0;
                }

                GridLayout.Spec row = GridLayout.spec(rowIndex, 1);
                GridLayout.Spec colspan = GridLayout.spec(columnIndex, 1);
                GridLayout.LayoutParams gridLayoutParam = new GridLayout.LayoutParams(row, colspan);
                grid.addView(view, gridLayoutParam);
                grid.setVisibility(View.VISIBLE);

                buttonsForEveryRowAlreadyAddedInTheRow++;
                columnIndex++;

            int finalI = i;
            newi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s = txt.getText().toString() +";"+ finalI;
                        switchActivity(s);
                    }

                });
        }

    }

    public String load(String FILE_NAME) {
        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null) {
                sb.append(text);
            }
            //Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_SHORT).show();
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public void switchActivity(String topics){
        Intent profilo = new Intent(search.this, searchResults.class);
        profilo.putExtra("idProfile", id);
        profilo.putExtra("categorie", topics);
        startActivity(profilo);
    }

    private void newVote(int requestCode, int resultCode, Intent data) throws IOException {
        if (resultCode == RESULT_OK && null != data) {
            if (data.getClipData() != null) {
                String paths = "";
                int cout = data.getClipData().getItemCount();
                //Toast.makeText(getApplicationContext(), "SIZE  " + cout,Toast.LENGTH_SHORT).show();
                if(cout <= 4) {
                    for (int i = 0; i < cout; i++) {
                        // adding imageuri in array
                        Uri selectedImage = data.getClipData().getItemAt(i).getUri();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        picturePath = cursor.getString(columnIndex);
                        paths = paths + picturePath + ":";
                        //Toast.makeText(getApplicationContext(), "LETTO  " + paths,Toast.LENGTH_SHORT).show();
                        cursor.close();
                    }
                    loadVoteImg(paths, FILE_USERVOTE);
                    Intent voteView = new Intent(search.this, voteView.class);
                    voteView.putExtra("idProfile", id);
                    voteView.putExtra("numb", "0");
                    startActivity(voteView);
                }
            }

        }
    }

    private void loadVoteImg(String picPath, String FILE_NAME) throws IOException {
        //Toast.makeText(getApplicationContext(), "LETTO  " + load(FILE_NAME),Toast.LENGTH_SHORT).show();
        String t = load(FILE_NAME) + ";voteSrc:" + picPath;
        FileOutputStream fos = null;
        fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
        fos.write(t.getBytes());
        //Toast.makeText(getApplicationContext(), "Scritto   " + t,Toast.LENGTH_SHORT).show();
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void newImg(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            String imgId = null;
            String[] d = load(FILE_USER).split(";;");
            for(int i=0; i<d.length; i++){
                String[] src = d[i].split(";");
                if(src[0].split(":")[1].equals(id)){
                    imgId = src[2].split(":")[1];
                }
            }
            Intent imgVote = new Intent(search.this, imgView.class);
            imgVote.putExtra("numb", "0");
            imgVote.putExtra("idProfile", id);
            imgVote.putExtra("forTopicFile", ";;" + id + ":" + imgId + ";imgSrc:" +  picturePath );
            imgVote.putExtra("forImgSet", id + ":" + imgId + ";imgSrc:" +  picturePath );
            startActivity(imgVote);
            cursor.close();
        }
    }
    //to give the permission for load img
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void save(String FILE_NAME, String text) throws IOException {
        FileOutputStream fos = null;
        fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
        fos.write(text.getBytes());
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}