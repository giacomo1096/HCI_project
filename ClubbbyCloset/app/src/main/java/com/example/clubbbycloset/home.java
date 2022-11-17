package com.example.clubbbycloset;

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
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
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
import java.util.Date;
import java.util.Random;

import static androidx.core.content.FileProvider.getUriForFile;


public class home extends AppCompatActivity {
    TextView edesc, elocation, etime, elink;
    ImageView bhome, bsearch, badd, bvote, bprofile;
    LinearLayout scroll;
    public static String id;
    private static final String FILE_ALLUSERS = "allUsersData.txt";
    private static final String FILE_USER = "userdata.txt";
    private static final String FILE_USERVOTE ="uservote.txt";

    private String picturePath = "";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_LOAD_VOTE = 2;
    private static int REQUEST_IMAGE_CAPTURE = 3;
    private static int REQUEST_VOTE_IMAGE_CAPTURE = 4;
    String currentPhotoPath;
    String currentVotePhotoPath1;
    String currentVotePhotoPath2;

    String fileAllUsersResult;
    String fileUserVoteResult;

    int n;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Bundle Extra = getIntent().getExtras();
        id = Extra.getString("idProfile");

        
        fileAllUsersResult = load(FILE_ALLUSERS);

        scroll = (LinearLayout) this.findViewById(R.id.homeScroll);
        try {
            setHomeLayout(fileAllUsersResult,scroll);
        } catch (IOException e) {
            e.printStackTrace();
        }


        bhome = (ImageView) this.findViewById(R.id.home);
        bsearch = (ImageView) this.findViewById(R.id.search);
        badd = (ImageView) this.findViewById(R.id.add);
        bvote = (ImageView) this.findViewById(R.id.vote);
        bprofile = (ImageView) this.findViewById(R.id.profile);

        bhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(home.this, home.class);
                home.putExtra("idProfile", id);
                startActivity(home);
            }
        });
        bsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent search = new Intent(home.this,search.class);
                search.putExtra("idProfile", id);
                startActivity(search);
            }
        });
        bprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(home.this, profile.class);
                profile.putExtra("type", "0");
                profile.putExtra("idProfile", id);
                startActivity(profile);
            }
        });
        bvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent vote = new Intent(home.this, vote.class);
                vote.putExtra("idProfile", id);
                startActivity(vote); // takes the user to the signup activity
            }

        });
        badd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(home.this, badd);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
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
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                // Create the File where the photo should go
                                File photoFile = null;
                                try {
                                    photoFile = createImageFile();
                                } catch (IOException ex) {
                                }
                                // Continue only if the File was successfully created
                                if (photoFile != null) {

                                    Uri photoURI = getUriForFile(home.this,"com.example.clubbbycloset",photoFile);
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                }
                            }

                        }
                        else if (item.getTitle().equals("Take pictures")){
                            Toast.makeText(getApplicationContext(), "Take two pictures", Toast.LENGTH_SHORT).show();
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

    private void setHomeLayout(String fileAllusersResult, LinearLayout scroll) throws IOException {
        String[] res = fileAllusersResult.split(";;");
        int max = 10;
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < max; i++) {
            Integer j = new Random().nextInt(res.length);
            String[] r = res[j].split(";");
            if (r.length == 3) {
                String username = r[0].split(":")[0];
                String userProfileImageSrc = r[0].split(":")[1];
                String imgSrc = r[1].split(":")[1];
                String[] desc = r[2].split(":");

                View view = inflater.inflate(R.layout.img_desc_frame, null);

                ImageView img = (ImageView) view.findViewById(R.id.foto);

                if (imgSrc.contains("storage")) {
                    Bitmap bml = BitmapFactory.decodeFile(imgSrc);
                    Bitmap rotatedBitmap = null;
                    try {
                        rotatedBitmap = rotateImage(imgSrc, bml);
                        img.setImageBitmap(rotatedBitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else {
                    int id = getResources().getIdentifier(imgSrc, "drawable", "com.example.clubbbycloset");
                    //Bitmap bm = BitmapFactory.decodeResource(getResources(), id);
                    //img.setImageBitmap(bm);
                    img.setImageResource(id);
                }

                ImageView userProfileImage = (ImageView)view.findViewById(R.id.userProfileImg);
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


                edesc = (TextView)view.findViewById(R.id.edesc);
                edesc.setText(new String(Character.toChars(0x1F4F7)));

                elocation = (TextView)view.findViewById(R.id.elocation);
                elocation.setText(new String(Character.toChars(0x1F4CD)));

                etime = (TextView)view.findViewById(R.id.etime);
                etime.setText(new String(Character.toChars(0x1F552)));

                elink = (TextView)view.findViewById(R.id.elink);
                elink.setText(new String(Character.toChars(0x1F517)));


                TextView description = (TextView) view.findViewById(R.id.description);
                TextView location = (TextView) view.findViewById(R.id.location);
                TextView time = (TextView) view.findViewById(R.id.time);
                TextView link = (TextView) view.findViewById(R.id.link);
                TextView[] arr ={description,location,time,link};
                for(int k=1; k<desc.length; k++){
                    arr[k-1].setText(desc[k]);
                    if(k==4 && desc[k].contains(".")){
                        int finalK = k;
                        arr[k-1].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri uri = Uri.parse("http://" + desc[finalK].split(" ")[1]); // missing 'http://' will cause crashed
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        });
                    }
                }


                TextView user = (TextView) view.findViewById(R.id.username);
                user.setText(username);
                user.setVisibility(View.VISIBLE);
                user.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profilo = new Intent(home.this, usersProfile.class);
                        profilo.putExtra("user", username);
                        profilo.putExtra("type", "0");
                        profilo.putExtra("idProfile", id);
                        startActivity(profilo);
                    }
                });
                scroll.addView(view);
            }
            //j = (j+1)%(res.length-1);
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
                Uri photoURI = getUriForFile(home.this, "com.example.clubbbycloset", photoFile);
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
                Intent voteView = new Intent(home.this, voteView.class);
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
            Intent imgVote = new Intent(home.this, imgView.class);
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
                        //Toast.makeText(getApplicationContext(), "LETTO  " + paths,Toast.LENGTH_SHORT).show();
                        cursor.close();
                    }
                    loadVoteImg(paths, FILE_USERVOTE);
                    Intent voteView = new Intent(home.this, voteView.class);
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
            String[] d = load(FILE_USER).split(";;");
            for(int i=0; i<d.length; i++){
                String[] src = d[i].split(";");
                if(src[0].split(":")[1].equals(id)){
                    imgId = src[2].split(":")[1];
                }
            }
            Intent imgVote = new Intent(home.this, imgView.class);
            imgVote.putExtra("numb", "0");
            imgVote.putExtra("idProfile", id);
            imgVote.putExtra("forTopicFile", ";;" + id + ":" + imgId + ";imgSrc:" +  picturePath );
            imgVote.putExtra("forImgSet", id + ":" + imgId + ";imgSrc:" +  picturePath );
            startActivity(imgVote);
            cursor.close();
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

}
