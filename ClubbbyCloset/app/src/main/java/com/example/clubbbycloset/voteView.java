package com.example.clubbbycloset;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
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

public class voteView extends AppCompatActivity {
    ImageView bhome, bsearch, badd, bvote, bprofile , f1, f2, buttonmenu;
    EditText edDesc, edLoc, edTime;
    TextView bsave, rv, lv;
    public static String id;


    private  static final String FILE_WHOVOTED = "whoVoted.txt";
    private  static final String FILE_ALLVOTE = "allVote.txt";
    private static final String FILE_USERVOTE ="uservote.txt";

    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_LOAD_VOTE = 2;
    private static int REQUEST_IMAGE_CAPTURE = 3;
    private static int REQUEST_VOTE_IMAGE_CAPTURE = 4;
    String currentPhotoPath;
    String currentVotePhotoPath1;
    String currentVotePhotoPath2;

    int n;

    private static final String FILE_ALLUSERS = "allUsersData.txt";
    private static final String FILE_USER = "userdata.txt";

    private String picturePath = "";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_view);

        Bundle Extra = getIntent().getExtras();
        id = Extra.getString("idProfile");

        bhome = (ImageView) this.findViewById(R.id.home);
        bsearch = (ImageView) this.findViewById(R.id.search);
        badd = (ImageView) this.findViewById(R.id.add);
        bvote = (ImageView) this.findViewById(R.id.vote);
        bprofile = (ImageView) this.findViewById(R.id.profile);
        f1= (ImageView) this.findViewById(R.id.foto1);
        f2= (ImageView) this.findViewById(R.id.foto2);
        edDesc = (EditText) this.findViewById(R.id.description);
        edLoc = (EditText) this.findViewById(R.id.location);
        edTime = (EditText) this.findViewById(R.id.time);
        bsave = (TextView) this.findViewById(R.id.save);
        rv = (TextView) this.findViewById(R.id.right_vote);
        lv = (TextView) this.findViewById(R.id.left_vote);

        ImageView[] imgs = {f1, f2};
        EditText[] descri = {edDesc,edLoc,edTime};

        String numb = Extra.getString("numb");
        String imgSrc;
        String descSrc;
        String votes;
        String voteId;
        if (numb.equals("1")){
            voteId = Extra.getString("voteId");
            imgSrc = Extra.getString("imgSrc");
            descSrc = Extra.getString("descrSrc");
            votes = Extra.getString("votes");

            setVoteLayout(imgSrc,descSrc, votes, imgs, descri, voteId);
        }else {
            setPhotosVote(FILE_USERVOTE, imgs);
        }

        bsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String profileImg ="";
                    String toAdd = ";description:" + edDesc.getText().toString() + ":" + edLoc.getText().toString() + ":" + edTime.getText().toString();
                    String[] userData = load(FILE_USER).split(";;");
                    for (int i=0; i<userData.length; i++){
                        if(userData[i].split(";")[0].split(":")[1].equals(id)){
                            profileImg = userData[i].split(";")[2].split(":")[1];
                        }
                    }

                    String[] res = load(FILE_USERVOTE).split(";");
                    String v1= res[res.length-1].split(":")[1];
                    String v2= res[res.length-1].split(":")[2];
                    String toAddAllVote = load(FILE_ALLVOTE) + id + ":" + profileImg + ";voteSrc:" + v1 + ":" + v2  +  toAdd + ";vote:0:0;;";
                    save(FILE_ALLVOTE , toAddAllVote);
                    save(FILE_WHOVOTED, load(FILE_WHOVOTED)+id+ ";voteSrc:" + v1 + ":" + v2 +";users;;");

                }catch (IOException e) {
                     e.printStackTrace();
                }
                Intent profile = new Intent(voteView.this, profile.class);
                profile.putExtra("idProfile", id);
                profile.putExtra("type", "0");
                startActivity(profile);
            }
        });

        bhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(voteView.this, home.class);
                home.putExtra("idProfile", id);
                startActivity(home);
            }
        });

        bsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent search = new Intent(voteView.this, search.class);
                search.putExtra("idProfile", id);
                startActivity(search);
            }
        });

        bprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(voteView.this, profile.class);
                profile.putExtra("type", "0");
                profile.putExtra("idProfile", id);
                startActivity(profile);
            }
        });

        bvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent vote = new Intent(voteView.this, vote.class);
                vote.putExtra("idProfile", id);
                startActivity(vote); // takes the user to the signup activity
            }

        });

        badd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(voteView.this, badd);
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
                                    Uri photoURI = getUriForFile(voteView.this,"com.example.clubbbycloset",photoFile);
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


    private void setPhotosVote(String FILE_NAME, ImageView[] imgs) {
        rv.setVisibility(View.INVISIBLE);
        lv.setVisibility(View.INVISIBLE);
        //Toast.makeText(getApplicationContext(), "numero in func " + numb, Toast.LENGTH_SHORT).show();
        String[] vs = load(FILE_NAME).split(";");
        int j = vs.length - 1;
        String[] res = vs[j].split(":");
        for (int i = 1; i < res.length; i++) {
            if (i <= imgs.length) {
                Bitmap bm = BitmapFactory.decodeFile(res[i]);
                Bitmap rotatedBitmap = null;
                try {
                    rotatedBitmap = rotateImage(res[i], bm);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imgs[i - 1].setImageBitmap(rotatedBitmap);
            }
        }

    }

    private void setVoteLayout(String imgSrc, String descSrc, String votes, ImageView[]imgs, EditText[] des, String voteId) {
        TextView title = (TextView) findViewById(R.id.voteView_title);
        title.setText("Poll");

        if(id.equals(voteId)){
            buttonmenu = (ImageView) findViewById(R.id.menu);
            buttonmenu.setVisibility(View.VISIBLE);
            buttonmenu.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   PopupMenu popup_edit = new PopupMenu(voteView.this, buttonmenu);
                   popup_edit.getMenuInflater().inflate(R.menu.popup_modify_delete_poll, popup_edit.getMenu());
                   popup_edit.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                       @RequiresApi(api = Build.VERSION_CODES.M)
                       public boolean onMenuItemClick(MenuItem item) {
                           if (item.getTitle().equals("Edit poll")) {
                               Toast.makeText(getApplicationContext(), "Function not available now", Toast.LENGTH_LONG).show();
                           }
                           if (item.getTitle().equals("Delete poll")) {
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
                                           delateImage(imgSrc.split(":"));
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
        }
        edDesc.setEnabled(false);
        edTime.setEnabled(false);
        edLoc.setEnabled(false);
        bsave.setClickable(false);
        bsave.setVisibility(View.INVISIBLE);
        rv.setVisibility(View.VISIBLE);
        lv.setVisibility(View.VISIBLE);

       TextView edesc = (TextView)this.findViewById(R.id.edesc);
        edesc.setText(new String(Character.toChars(0x1F4F7)));

        TextView elocation = (TextView)this.findViewById(R.id.elocation);
        elocation.setText(new String(Character.toChars(0x1F4CD)));

        TextView etime = (TextView)this.findViewById(R.id.etime);
        etime.setText(new String(Character.toChars(0x1F552)));

        for (int i = 1; i < descSrc.split(":").length; i++) {
            des[i - 1].setText(descSrc.split(":")[i]);
        }
        lv.setText(votes.split(":")[1]);
        rv.setText(votes.split(":")[2]);
        for (int i =1; i< imgSrc.split(":").length; i++) {
            Bitmap bm;
            if(imgSrc.contains("storage")){
                bm = BitmapFactory.decodeFile(imgSrc.split(":")[i]);
                Bitmap rotatedBitmap = null;
                try {
                    rotatedBitmap = rotateImage(imgSrc.split(":")[i], bm);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imgs[i - 1].setImageBitmap(rotatedBitmap);
            }else{
                int id = getResources().getIdentifier(imgSrc.split(":")[i], "drawable", "com.example.clubbbycloset");
                bm = BitmapFactory.decodeResource(getResources(), id);
                imgs[i - 1].setImageBitmap(bm);
            }

        }

    }

    private void delateImage(String[] imgs) throws IOException {
        String[] res = load(FILE_ALLVOTE).split(";;");
        String toAdd = "";
        for(int i =0; i<res.length; i++){
            if(res[i].split(";")[0].split(":")[0].equals(id) &&  res[i].split(";")[1].split(":")[1].equals(imgs[1]) &&  res[i].split(";")[1].split(":")[2].equals(imgs[2]) ){
                toAdd = toAdd;
            }else{
                toAdd = toAdd + res[i] + ";;";
            }
        }
        save(FILE_ALLVOTE, toAdd);
        Intent profile = new Intent(voteView.this, profile.class);
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
                Uri photoURI = getUriForFile(voteView.this, "com.example.clubbbycloset", photoFile);
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
                Intent voteView = new Intent(voteView.this, voteView.class);
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
            Intent imgVote = new Intent(voteView.this, imgView.class);
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
                    saveVoteImg(paths, FILE_USERVOTE);
                    Intent voteView = new Intent(voteView.this, voteView.class);
                    voteView.putExtra("numb", "0");
                    startActivity(voteView);
                }
            }

        }
    }

    private void saveVoteImg(String picPath, String FILE_NAME) throws IOException {
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
            Intent imgVote = new Intent(voteView.this, imgView.class);
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
