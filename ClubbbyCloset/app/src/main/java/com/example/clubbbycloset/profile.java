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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
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

public class profile extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_LOAD_VOTE = 2;
    private static int REQUEST_IMAGE_CAPTURE = 3;
    private static int REQUEST_VOTE_IMAGE_CAPTURE = 4;
    private static int RESULT_LOAD_IMAGE_PROFILE = 5;
    String currentPhotoPath;
    String currentVotePhotoPath1;
    String currentVotePhotoPath2;

    int n;
    private static final String FILE_USER = "userdata.txt";
    private static final String FILE_USERVOTE ="uservote.txt";
    private  static final String FILE_ALLVOTE = "allVote.txt";
    private static final String FILE_ALLUSERS = "allUsersData.txt";
    private  static final String FILE_USERBIO = "userBio.txt";

    public static String id;

    private String picturePath = "";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    ImageView bhome, bsearch, badd, bvote, bprofile, blogout, bprofileImg;

    TextView tvusername;
    GridLayout gridLayout;
    LinearLayout linearLayout, hScroll;

    int finalRowIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle Extra = getIntent().getExtras();
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
        blogout = (ImageView)this.findViewById(R.id.logout);
        bprofileImg = (ImageView)this.findViewById(R.id.profile_img);

        tvusername= (TextView)this.findViewById(R.id.usernameProfile);
        String name =null;
        try {
            String[] t =load(FILE_USER).split(";;");
            for (int i = 0; i< t.length; i++){
                String[] s = t[i].split(";");
                if (s[0].split(":")[1].equals(id)){
                    tvusername.setText(id);
                    name = id;
                    if(!s[2].split(":")[1].equals("imgSrc")) {
                        //Toast.makeText(getApplicationContext(), "in profile img   " + s[1], Toast.LENGTH_SHORT).show();
                        Bitmap bm = BitmapFactory.decodeFile(s[2].split(":")[1]);
                        Bitmap resized = Bitmap.createScaledBitmap(bm, 200, 200, false);
                        Bitmap conv_bm = getRoundedRectBitmap(rotateImage(s[2].split(":")[1],resized), 200);
                        bprofileImg.setImageBitmap(conv_bm);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        EditText bio = (EditText) findViewById(R.id.bio);
        String[] bios = new String[0];
        String r = "";
        try {
            r = load(FILE_USERBIO);
            bios =r.split(";");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i =0; i<bios.length; i ++){
            if(bios[i].split(":")[0].equals(id)){
                bio.setEnabled(false);
                bio.setText(bios[i].split(":")[1]);
            }
        }

        String finalR = r;
        bio.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String insert = String.valueOf(bio.getText());
                    if(insert.contains(";") || insert.contains(":")){
                        Toast.makeText(getApplicationContext(), "Invalid String: ';' and ':' are not allow", Toast.LENGTH_SHORT).show();
                    }else{
                        try {
                            save(FILE_USERBIO, finalR + id + ":" + insert + ";");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return false;
            }
        });

        linearLayout = (LinearLayout) this.findViewById(R.id.linear);
        gridLayout = (GridLayout) this.findViewById((R.id.grid));
        if (type == null){
            linearLayout.setVisibility(View.INVISIBLE);
            gridLayout.setVisibility(View.VISIBLE);
            gridLayout.removeAllViews();
            setPhotosGridLayout(FILE_ALLUSERS, gridLayout);
        }
        else if (type.equals("0")){
            linearLayout.setVisibility(View.INVISIBLE);
            gridLayout.setVisibility(View.VISIBLE);
            gridLayout.removeAllViews();
            setPhotosGridLayout(FILE_ALLUSERS, gridLayout);
        }
        else{
            linearLayout.setVisibility(View.VISIBLE);
            gridLayout.setVisibility(View.INVISIBLE);
            int finalIndex = index;
            setPhotosLinearLayuout(FILE_ALLUSERS, linearLayout);
        }

        hScroll = (LinearLayout) this.findViewById(R.id.horizScroll);
        try {
            setVoteBar(FILE_ALLVOTE, hScroll, id);
        } catch (IOException e) {
            e.printStackTrace();
        }

        bprofileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE_PROFILE);
            }
        });

        bhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(profile.this, home.class);
                home.putExtra("idProfile", id);
                startActivity(home); // takes the user to the signup activity
            }

        });

        bsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent search = new Intent(profile.this, search.class);
                search.putExtra("idProfile", id);
                startActivity(search); // takes the user to the signup activity
            }

        });

        bprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(profile.this, profile.class);
                profile.putExtra("idProfile", id);
                profile.putExtra("type", "0");
                startActivity(profile); // takes the user to the signup activity
            }

        });

        bvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent vote = new Intent(profile.this, vote.class);
                vote.putExtra("idProfile", id);
                startActivity(vote); // takes the user to the signup activity
            }

        });

        blogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_logout, null);
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                // dismiss the popup window when touched
                TextView byes = (TextView)popupView.findViewById(R.id.byes);
                TextView bno = (TextView)popupView.findViewById(R.id.bno);

                byes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent login = new Intent(profile.this, login.class);
                        startActivity(login); // takes the user to the signup activity
                    }
                });

                bno.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

            }

        });

        badd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(profile.this, badd);
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
                                    Uri photoURI = getUriForFile(profile.this,"com.example.clubbbycloset",photoFile);
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

    private void setVoteBar(String fileUservote, LinearLayout hScroll, String username) throws IOException {
        String[] t =load(fileUservote).split(";;");

        for (int i = t.length-1; i>-1; i--) {
            if (t[i].split(";")[0].split(":")[0].equals(username)) {
                ImageView vimg = new ImageView(this);
                String[] s = t[i].split(";")[1].split(":");
                if (s.length > 2) {
                    //Toast.makeText(getApplicationContext(), "in profile img   " + s[1], Toast.LENGTH_SHORT).show();
                    Bitmap bm = BitmapFactory.decodeFile(s[(s.length - 1)]);
                    Bitmap resized = Bitmap.createScaledBitmap(bm, 200, 200, false);
                    Bitmap conv_bm = getRoundedRectBitmap(rotateImage(s[(s.length - 1)], resized), 200);
                    vimg.setImageBitmap(conv_bm);
                }

                int finalI = i;
                vimg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent voteView = new Intent(profile.this, voteView.class);
                        voteView.putExtra("numb", "1");
                        voteView.putExtra("imgSrc", t[finalI].split(";")[1]);
                        voteView.putExtra("descrSrc", t[finalI].split(";")[2]);
                        voteView.putExtra("votes", t[finalI].split(";")[3]);
                        voteView.putExtra("idProfile", id);
                        voteView.putExtra("voteId", id);

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

    private void setPhotosGridLayout(String FILE_NAME, GridLayout grid) {
        try {
            String[] res = load(FILE_NAME).split(";;");
            LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            int buttons= res.length;//the number of bottons i have to put in GridLayout
            int buttonsForEveryRow = 2; // buttons i can put inside every single row
            int buttonsForEveryRowAlreadyAddedInTheRow =0; // count the buttons added in a single rows
            int columnIndex=0; //cols index to which i add the button
            int rowIndex=0; //row index to which i add the button
            for(int i=0; i < buttons;i++) {
                int indexDaMandare = 0;
                if (res[i].split(";")[0].split(":")[0].equals(id)) {

                    indexDaMandare++;
                    String imgSrc = res[i].split(";")[1].split(":")[1];
                    View view = inflater.inflate(R.layout.img_frame, null);
                    ImageView newi = (ImageView) view.findViewById(R.id.newImg);

                    Bitmap bm = BitmapFactory.decodeFile(imgSrc);
                    Bitmap rotatedBitmap = rotateImage(imgSrc, bm);
                    Bitmap resized = Bitmap.createScaledBitmap(rotatedBitmap, 550, 600, false);
                    newi.setImageBitmap(resized);

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
                    newi.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent profile = new Intent(profile.this, profile.class);
                            profile.putExtra("type", "1");
                            profile.putExtra("idProfile", id);
                            profile.putExtra("position", finalRowIndex);
                            startActivity(profile); // takes the user to the signup activity
                        }

                    });
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setPhotosLinearLayuout(String FILE_NAME,  LinearLayout linearLayout) {
        LinearLayout topBar = (LinearLayout)findViewById(R.id.TopAppBar);
        LinearLayout voteBar = (LinearLayout)findViewById(R.id.voteBar);

        try {
            String[] res = load(FILE_NAME).split(";;");
            LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            for(int i=0; i < res.length;i++) {
                if (res[i].split(";")[0].split(":")[0].equals(id)) {
                    String imgSrc = res[i].split(";")[1].split(":")[1];
                    String descSrc[] = res[i].split(";")[2].split(":");
                    View lview = inflater.inflate(R.layout.img_desc_frame, null);

                    TextView username = (TextView) lview.findViewById(R.id.username);
                    username.setText(res[i].split(";")[0].split(":")[0]);
                    username.setVisibility(View.VISIBLE);
                    String userProfileImageSrc = res[i].split(";")[0].split(":")[1];
                    ImageView userProfileImage = (ImageView)lview.findViewById(R.id.userProfileImg);
                    if (userProfileImageSrc.contains("storage")) {
                        Bitmap bml = BitmapFactory.decodeFile(userProfileImageSrc);
                        Bitmap resized = Bitmap.createScaledBitmap(bml, 200, 200, false);
                        Bitmap conv_bm = getRoundedRectBitmap(rotateImage(userProfileImageSrc, resized), 200);
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
                    Bitmap bm = BitmapFactory.decodeFile(imgSrc);
                    Bitmap rotatedBitmap = rotateImage(imgSrc, bm);
                    //Bitmap resized = Bitmap.createScaledBitmap(rotatedBitmap, 550, 600, false);
                    newi.setImageBitmap(rotatedBitmap);

                    TextView edesc, elocation, etime, elink, delateImage;

                    edesc = (TextView)lview.findViewById(R.id.edesc);
                    edesc.setText(new String(Character.toChars(0x1F4F7)));

                    elocation = (TextView)lview.findViewById(R.id.elocation);
                    elocation.setText(new String(Character.toChars(0x1F4CD)));

                    etime = (TextView)lview.findViewById(R.id.etime);
                    etime.setText(new String(Character.toChars(0x1F552)));

                    elink = (TextView)lview.findViewById(R.id.elink);
                    elink.setText(new String(Character.toChars(0x1F517)));

                    ImageView buttonmenu = (ImageView) lview.findViewById(R.id.menu);
                    buttonmenu.setVisibility(View.VISIBLE);
                    buttonmenu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupMenu popup_edit = new PopupMenu(profile.this, buttonmenu);
                            popup_edit.getMenuInflater().inflate(R.menu.popup_modify_delete, popup_edit.getMenu());
                            popup_edit.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                public boolean onMenuItemClick(MenuItem item) {
                                    if (item.getTitle().equals("Edit post")) {
                                        Toast.makeText(getApplicationContext(), "Function not available now", Toast.LENGTH_LONG).show();
                                    }
                                    if (item.getTitle().equals("Delete post")) {
                                        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                                        View popupView = inflater.inflate(R.layout.popup_delate, null);
                                        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                                        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                        boolean focusable = true; // lets taps outside the popup also dismiss it
                                        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                                        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                                        TextView byes = (TextView)popupView.findViewById(R.id.byes);
                                        TextView bno = (TextView)popupView.findViewById(R.id.bno);

                                        byes.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {
                                                    delateImage(imgSrc);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                        bno.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                popupWindow.dismiss();
                                            }
                                        });
                                    }
                                    return true;
                                }
                            });
                            popup_edit.show();//showing popup menu
                        }
                    });

                    TextView description = (TextView) lview.findViewById(R.id.description);
                    TextView location = (TextView) lview.findViewById(R.id.location);
                    TextView time = (TextView) lview.findViewById(R.id.time);
                    TextView link = (TextView) lview.findViewById(R.id.link);
                    TextView[] arr ={description,location,time,link};
                    for(int k=1; k<descSrc.length; k++){
                        arr[k-1].setText(descSrc[k]);
                        if(k==4 && descSrc[k].contains("bit.ly")){
                            int finalK = k;
                            arr[k-1].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri uri = Uri.parse("http://" + descSrc[finalK]); // missing 'http://' will cause crashed
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                    newi.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent profile = new Intent(profile.this, profile.class);
                            profile.putExtra("type", "0");
                            profile.putExtra("idProfile", id);
                            startActivity(profile); // takes the user to the signup activity
                        }

                    });

                    linearLayout.addView(lview);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void delateImage(String imgSrc) throws IOException {
        String[] res = load(FILE_ALLUSERS).split(";;");
        String toAdd = "";
        for(int i =0; i<res.length; i++){
            if(res[i].split(";")[0].split(":")[0].equals(id) && res[i].split(";")[1].split(":")[1].equals(imgSrc)){
                toAdd = toAdd;
            }else{
                toAdd = toAdd + res[i] + ";;";
            }
        }
        save(FILE_ALLUSERS, toAdd);
        Intent profile = new Intent(profile.this, profile.class);
        profile.putExtra("idProfile", id);
        profile.putExtra("type", "0");
        startActivity(profile); // takes the user to the signup activity

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

    public String load(String FILE_NAME) throws IOException {
        FileInputStream fis = null;
        fis = openFileInput(FILE_NAME);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String text;
        while ((text = br.readLine()) != null) {
            if(text!= null){
                return text;
            }
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

    //to load img from gallery for img profile
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        verifyStoragePermissions(this);
        if (requestCode == RESULT_LOAD_IMAGE_PROFILE ) {
            newProfileImg(resultCode,data);
        }else if (requestCode == RESULT_LOAD_IMAGE) {
            newImg(requestCode,resultCode,data);
        }
        else if(requestCode == RESULT_LOAD_VOTE) {
            try {
                newVote(requestCode,resultCode,data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            takeAPicture(resultCode, data);
        }
        else if (requestCode == REQUEST_VOTE_IMAGE_CAPTURE) {
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
                Uri photoURI = getUriForFile(profile.this, "com.example.clubbbycloset", photoFile);
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
                Intent voteView = new Intent(profile.this, voteView.class);
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
            try {
                String[] d = load(FILE_USER).split(";;");
                for(int i=0; i<d.length; i++){
                    String[] src = d[i].split(";");
                    if(src[0].split(":")[1].equals(id)){
                        imgId = src[2].split(":")[1];
                    }
                }
                Intent imgVote = new Intent(profile.this, imgView.class);
                imgVote.putExtra("numb", "0");
                imgVote.putExtra("idProfile", id);
                imgVote.putExtra("forTopicFile", ";;" + id + ":" + imgId + ";imgSrc:" +  currentPhotoPath );
                imgVote.putExtra("forImgSet", id + ":" + imgId + ";imgSrc:" +  currentPhotoPath );
                startActivity(imgVote);
            } catch (IOException e) {
                e.printStackTrace();
            }

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

    private void newProfileImg(int resultCode, Intent data){
        if (resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);

           try {
                changeProfileImg(picturePath,FILE_USER);
            } catch (IOException e) {
                e.printStackTrace();
            }
            cursor.close();
            Bitmap bm = BitmapFactory.decodeFile(picturePath);
            Bitmap rotatedBitmap=null;
            try {
                rotatedBitmap = rotateImage(picturePath, bm);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap resized = Bitmap.createScaledBitmap(rotatedBitmap, 200, 200, false);
            Bitmap conv_bm = getRoundedRectBitmap(resized, 200);
            bprofileImg.setImageBitmap(conv_bm);
        }

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
                    Intent voteView = new Intent(profile.this, voteView.class);
                    voteView.putExtra("numb", "0");
                    voteView.putExtra("idProfile", id);
                    startActivity(voteView);
                }
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
            try {
                String[] d = load(FILE_USER).split(";;");
                for(int i=0; i<d.length; i++){
                    String[] src = d[i].split(";");
                    if(src[0].split(":")[1].equals(id)){
                        imgId = src[2].split(":")[1];
                    }
                }
                Intent imgVote = new Intent(profile.this, imgView.class);
                imgVote.putExtra("numb", "0");
                imgVote.putExtra("idProfile", id);
                imgVote.putExtra("forTopicFile", ";;" + id + ":" + imgId + ";imgSrc:" +  picturePath );
                imgVote.putExtra("forImgSet", id + ":" + imgId + ";imgSrc:" +  picturePath );
                startActivity(imgVote);
            } catch (IOException e) {
                e.printStackTrace();
            }
            cursor.close();
        }
    }

    private void loadVoteImg(String picPath, String FILE_NAME) throws IOException {
        String t = load(FILE_NAME) + ";voteSrc:" + picPath;
        FileOutputStream fos = null;
        fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
        fos.write(t.getBytes());
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
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

    public void changeProfileImg(String picPath, String FILE_NAME) throws IOException {
        String[] t =load(FILE_NAME).split(";;");
        String toAdd ="";
        for (int i = 0; i<t.length; i++){
            if(t[i].split(";")[0].split(":")[1].equals(id)){
               toAdd =  toAdd + t[i].split(";")[0] + ";" +t[i].split(";")[1] + ";" + t[i].split(";")[2].split(":")[0] + ":" + picPath + ";;";
            }else{
                toAdd = toAdd + t[i] + ";;";
            }
        }
        //Toast.makeText(getApplicationContext(), "to addd " + toAdd,Toast.LENGTH_SHORT).show();
        FileOutputStream fos = null;
        fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
        fos.write(toAdd.getBytes());
        //Toast.makeText(getApplicationContext(), "Scritto   " + t,Toast.LENGTH_SHORT).show();
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}