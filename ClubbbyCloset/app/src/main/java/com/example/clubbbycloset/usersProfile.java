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
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
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
import java.util.Random;

import static androidx.core.content.FileProvider.getUriForFile;

public class usersProfile extends AppCompatActivity {
    ImageView bhome, bsearch, badd, bvote, bprofile, profileImg;
    TextView name;
    LinearLayout linearLayout, hScroll;
    GridLayout gridLayout;
    int finalRowIndex;
    public static String id;

    private static final String FILE_ALLUSERS = "allUsersData.txt";
    private  static final String FILE_ALLVOTE = "allVote.txt";
    private static final String FILE_USERVOTE ="uservote.txt";
    private  static final String FILE_USERBIO = "userBio.txt";

    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_LOAD_VOTE = 2;
    private static int REQUEST_IMAGE_CAPTURE = 3;
    private static int REQUEST_VOTE_IMAGE_CAPTURE = 4;
    String currentPhotoPath;
    String currentVotePhotoPath1;
    String currentVotePhotoPath2;

    int n;

    private static final String FILE_USER = "userdata.txt";
    private String picturePath = "";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profile);

        Bundle Extra = getIntent().getExtras();
        String textView = Extra.getString("user");
        String type = Extra.getString("type");
        id = Extra.getString("idProfile");

        int index = 0;
        if (type.equals("1")){
            index = Extra.getInt("position");
        }

        bhome = (ImageView) this.findViewById(R.id.home);
        bsearch = (ImageView) this.findViewById(R.id.search);
        badd = (ImageView) this.findViewById(R.id.add);
        bvote = (ImageView) this.findViewById(R.id.vote);
        bprofile = (ImageView) this.findViewById(R.id.profile);

        bhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(usersProfile.this, home.class);
                home.putExtra("idProfile", id);
                startActivity(home);
            }
        });
        bsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent search = new Intent(usersProfile.this, search.class);
                search.putExtra("idProfile", id);
                startActivity(search);
            }
        });
        bprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(usersProfile.this, profile.class);
                profile.putExtra("type", "0");
                profile.putExtra("idProfile", id);
                startActivity(profile);
            }
        });
        bvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent vote = new Intent(usersProfile.this, vote.class);
                vote.putExtra("idProfile", id);
                startActivity(vote); // takes the user to the signup activity
            }

        });
        badd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(usersProfile.this, badd);
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
                                    Uri photoURI = getUriForFile(usersProfile.this,"com.example.clubbbycloset",photoFile);
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

        TextView bio = (TextView) findViewById(R.id.bio);
        String[] bios = load(FILE_USERBIO).split(";");
        for(int i =0; i<bios.length; i ++){
            if(bios[i].split(":")[0].equals(textView)){
                bio.setText(bios[i].split(":")[1]);
            }
        }

        name = (TextView)this.findViewById(R.id.username);
        name.setText(textView);

        String[] res = load(FILE_ALLUSERS).split(";;");
        String pimg = null ;
        for(int i = 0 ; i<res.length; i++){
            String[] p= res[i].split(";")[0].split(":");
            if (p[0].equals(textView)){
                pimg = p[1];
                setProfileImg(pimg);
            }
        }

        gridLayout = (GridLayout) this.findViewById(R.id.grid);
        linearLayout = (LinearLayout) this.findViewById(R.id.linear);
        if (type.equals("0")){
            linearLayout.setVisibility(View.INVISIBLE);
            gridLayout.setVisibility(View.VISIBLE);
            gridLayout.removeAllViews();
            try {
                setPhotosGridLayout(res, gridLayout, textView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            linearLayout.setVisibility(View.VISIBLE);
            gridLayout.setVisibility(View.INVISIBLE);
            setPhotosLinearLayuout(res, linearLayout, textView, index);
        }

        try {
            hScroll = (LinearLayout) this.findViewById(R.id.horizScroll);
            setVoteBar(FILE_ALLVOTE, hScroll, textView);
        } catch (IOException e) {
            e.printStackTrace();
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

    public void setProfileImg(String img){
        profileImg=(ImageView)this.findViewById(R.id.profile_img);

        if(img.contains("storage")){
            Bitmap bm = BitmapFactory.decodeFile(img);
            Bitmap resized = Bitmap.createScaledBitmap(bm, 200, 200, false);
            Bitmap conv_bm = null;
            try {
                conv_bm = getRoundedRectBitmap(rotateImage(img,resized), 200);
            } catch (IOException e) {
                e.printStackTrace();
            }
            profileImg.setImageBitmap(conv_bm);
        }else{
            int idS = getResources().getIdentifier(img,"drawable", "com.example.clubbbycloset");
            Bitmap bm = BitmapFactory.decodeResource(getResources(), idS);
            //Bitmap resized = Bitmap.createScaledBitmap(bm, 200, 200, false);
            //Bitmap conv_bm = getRoundedRectBitmap(resized, 200);
            profileImg.setImageBitmap(bm);
        }

    }

    private void setVoteBar(String fileUservote, LinearLayout hScroll, String username) throws IOException {
        String[] t =load(fileUservote).split(";;");
        for (int i = t.length-1; i>-1; i--) {
            if (t[i].split(";")[0].split(":")[0].equals(username)) {
                ImageView vimg = new ImageView(this);
                String[] s = t[i].split(";")[1].split(":");
                if (s.length > 2) {
                    String img = s[(s.length - 1)];
                    if(img.contains("storage")){
                        Bitmap bm = BitmapFactory.decodeFile(img);
                        Bitmap resized = Bitmap.createScaledBitmap(bm, 200, 200, false);
                        Bitmap conv_bm = getRoundedRectBitmap(rotateImage(img, resized), 200);
                        vimg.setImageBitmap(conv_bm);
                    }else{
                        int id = getResources().getIdentifier(img, "drawable", "com.example.clubbbycloset");
                        Bitmap bm = BitmapFactory.decodeResource(getResources(), id);
                        Bitmap resized = Bitmap.createScaledBitmap(bm, 200, 200, false);
                        Bitmap conv_bm = getRoundedRectBitmap(resized, 200);
                        vimg.setImageBitmap(conv_bm);
                    }
                }

                int finalI = i;
                vimg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent voteView = new Intent(usersProfile.this, voteView.class);
                        voteView.putExtra("numb", "1");
                        voteView.putExtra("imgSrc", t[finalI].split(";")[1]);
                        voteView.putExtra("descrSrc", t[finalI].split(";")[2]);
                        voteView.putExtra("votes", t[finalI].split(";")[3]);
                        voteView.putExtra("idProfile", id);
                        voteView.putExtra("voteId", username);

                        startActivity(voteView);
                    }

                });
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(5, 2, 5, 2);
                vimg.setLayoutParams(lp);
                hScroll.addView(vimg);
            }
        }

    }

    public static Bitmap getRoundedRectBitmap(Bitmap bitmap, int pixels) {
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);

            int color = 0xff424242;
            Paint paint = new Paint();
            Rect rect = new Rect(0, 0, 200, 200);
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(100, 100, 100, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

        } catch (NullPointerException e) {
        } catch (OutOfMemoryError o) {
        }
        return result;
    }

    private void setPhotosGridLayout(String[] res, GridLayout grid, String username) throws IOException {
            LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            int buttons= res.length;//the number of bottons i have to put in GridLayout
            int buttonsForEveryRow = 2; // buttons i can put inside every single row
            int buttonsForEveryRowAlreadyAddedInTheRow =0; // count the buttons added in a single rows
            int columnIndex=0; //cols index to which i add the button
            int rowIndex=0; //row index to which i add the button
            int indexDaMandare=0;
            for(int i=0; i < buttons;i++) {

                if (res[i].split(";")[0].split(":")[0].equals(username)) {
                    String imgSrc = res[i].split(";")[1].split(":")[1];
                    View view = inflater.inflate(R.layout.img_frame, null);

                    ImageView newi = (ImageView) view.findViewById(R.id.newImg);

                    if (imgSrc.contains("storage")){
                        Bitmap bm = BitmapFactory.decodeFile(imgSrc);
                        Bitmap rotatedBitmap = rotateImage(imgSrc, bm);
                        Bitmap resized = Bitmap.createScaledBitmap(rotatedBitmap, 550, 600, false);
                        newi.setImageBitmap(resized);

                    } else{
                        int ids = getResources().getIdentifier(imgSrc, "drawable", "com.example.clubbbycloset");
                        Bitmap bm = BitmapFactory.decodeResource(getResources(), ids);
                        Bitmap resized = Bitmap.createScaledBitmap(bm, 550, 600, false);
                        newi.setImageBitmap(resized);
                    }

                    if (buttonsForEveryRowAlreadyAddedInTheRow == buttonsForEveryRow) {
                        rowIndex++; //here i increase the row index
                        buttonsForEveryRowAlreadyAddedInTheRow = 0;
                        columnIndex = 0;
                    }

                    GridLayout.Spec row = GridLayout.spec(rowIndex, 1);
                    GridLayout.Spec colspan = GridLayout.spec(columnIndex, 1);
                    GridLayout.LayoutParams gridLayoutParam = new GridLayout.LayoutParams(row, colspan);
                    LinearLayout f = (LinearLayout) view.findViewById(R.id.frame);
                    f.removeAllViews();
                    grid.addView(newi, gridLayoutParam);

                    buttonsForEveryRowAlreadyAddedInTheRow++;
                    columnIndex++;
                    finalRowIndex = indexDaMandare;
                    indexDaMandare ++;

                    newi.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent profile = new Intent(usersProfile.this, usersProfile.class);
                            profile.putExtra("user", username);
                            profile.putExtra("type", "1");
                            profile.putExtra("idProfile", id);
                            profile.putExtra("position", finalRowIndex);
                            startActivity(profile); // takes the user to the signup activity
                        }

                    });
                }
            }
    }

    private void setPhotosLinearLayuout(String[] res,  LinearLayout linearLayout, String username, int index) {

            LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            for(int i=1; i < res.length;i++) {
                if (res[i].split(";")[0].split(":")[0].equals(username)) {
                    String imgSrc = res[i].split(";")[1].split(":")[1];
                    String descSrc[] = res[i].split(";")[2].split(":");
                    View lview = inflater.inflate(R.layout.img_desc_frame, null);

                    TextView userName = (TextView) lview.findViewById(R.id.username);
                    userName.setText(res[i].split(";")[0].split(":")[0]);
                    userName.setVisibility(View.VISIBLE);
                    String userProfileImageSrc = res[i].split(";")[0].split(":")[1];
                    ImageView userProfileImage = (ImageView)lview.findViewById(R.id.userProfileImg);
                    if (userProfileImageSrc.contains("storage")) {
                        Bitmap bml = BitmapFactory.decodeFile(userProfileImageSrc);
                        Bitmap resized = Bitmap.createScaledBitmap(bml, 200, 200, false);
                        Bitmap conv_bm = null;
                        try {
                            conv_bm = getRoundedRectBitmap(rotateImage(userProfileImageSrc, resized), 200);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        userProfileImage.setImageBitmap(conv_bm);

                    }
                    else {
                        int id = getResources().getIdentifier(userProfileImageSrc, "drawable", "com.example.clubbbycloset");
                        Bitmap bm = BitmapFactory.decodeResource(getResources(), id);
                        if (bm != null ) {
                            Bitmap resized = Bitmap.createScaledBitmap(bm, 200, 200, false);
                            Bitmap conv_bm = getRoundedRectBitmap(resized, 200);
                            userProfileImage.setImageBitmap(conv_bm);
                        }
                    }

                    ImageView newi = (ImageView) lview.findViewById(R.id.foto);
                    if(imgSrc.contains("storage")) {
                        Bitmap bm = BitmapFactory.decodeFile(imgSrc);
                        Bitmap rotatedBitmap = null;
                        try {
                            rotatedBitmap = rotateImage(imgSrc, bm);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        newi.setImageBitmap(rotatedBitmap);
                    }else{
                        int ids = getResources().getIdentifier(imgSrc, "drawable", "com.example.clubbbycloset");
                        Bitmap bm = BitmapFactory.decodeResource(getResources(), ids);
                        newi.setImageBitmap(bm);
                    }

                    TextView edesc, elocation, etime, elink;

                    edesc = (TextView) lview.findViewById(R.id.edesc);
                    edesc.setText(new String(Character.toChars(0x1F4F7)));

                    elocation = (TextView) lview.findViewById(R.id.elocation);
                    elocation.setText(new String(Character.toChars(0x1F4CD)));

                    etime = (TextView) lview.findViewById(R.id.etime);
                    etime.setText(new String(Character.toChars(0x1F552)));

                    elink = (TextView) lview.findViewById(R.id.elink);
                    elink.setText(new String(Character.toChars(0x1F517)));

                    TextView description = (TextView) lview.findViewById(R.id.description);
                    TextView location = (TextView) lview.findViewById(R.id.location);
                    TextView time = (TextView) lview.findViewById(R.id.time);
                    TextView link = (TextView) lview.findViewById(R.id.link);
                    TextView[] arr = {description, location, time, link};
                    for(int k=1; k<descSrc.length; k++){
                        arr[k-1].setText(descSrc[k]);
                        if(k==4 && descSrc[k].contains(".")){
                            int finalK = k;
                            arr[k-1].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri uri = Uri.parse("http://" + descSrc[finalK].split(" ")[1]); // missing 'http://' will cause crashed
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    newi.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent profile = new Intent(usersProfile.this, profile.class);
                            profile.putExtra("type", "0");
                            profile.putExtra("idProfile", id);
                            startActivity(profile); // takes the user to the signup activity
                        }

                    });

                    linearLayout.addView(lview);
                }
            }
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
                Uri photoURI = getUriForFile(usersProfile.this, "com.example.clubbbycloset", photoFile);
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
                Intent voteView = new Intent(usersProfile.this, voteView.class);
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
            Intent imgVote = new Intent(usersProfile.this, imgView.class);
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

    private void newVote(int requestCode, int resultCode, Intent data) throws IOException {
        if (resultCode == RESULT_OK && null != data) {
            if (data.getClipData() != null) {
                String paths = "";
                int cout = data.getClipData().getItemCount();
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
                        cursor.close();
                    }
                    loadVoteImg(paths, FILE_USERVOTE);
                    Intent voteView = new Intent(usersProfile.this, voteView.class);
                    voteView.putExtra("numb", "0");
                    voteView.putExtra("idProfile", id);
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
            Intent imgVote = new Intent(usersProfile.this, imgView.class);
            imgVote.putExtra("numb", "0");
            imgVote.putExtra("idProfile", id);
            imgVote.putExtra("forTopicFile", ";;" + id + ":" + imgId + ";imgSrc:" +  picturePath );
            imgVote.putExtra("forImgSet", id + ":" + imgId + ";imgSrc:" +  picturePath );
            startActivity(imgVote);
            cursor.close();
        }
    }

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

    public static Bitmap rotateImage(String path, Bitmap source) throws IOException {
        Float angle = null;
        ExifInterface ei = new ExifInterface(path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        switch(orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                angle = Float.valueOf(90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                angle = Float.valueOf(180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                angle = Float.valueOf(270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                angle = Float.valueOf(0);
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

}