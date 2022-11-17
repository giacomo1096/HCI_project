package com.example.clubbbycloset;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


public class signup extends AppCompatActivity {
    private static final String FILE_USER = "userdata.txt";
    private static final String FILE_USERVOTE ="uservote.txt";
    private static final String FILE_ALLUSERS = "allUsersData.txt";

    Button b1;
    TextView b2;
    EditText ed2,ed3,ed4;
    String username;
    CheckBox show_hide_password;
    ImageView info ;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        b1 = (Button)findViewById(R.id.btn_register);
        ed2 = (EditText)findViewById(R.id.et_email);
        ed3 = (EditText)findViewById(R.id.et_password);
        ed4 = (EditText)findViewById(R.id.et_repassword);
        b2 = (TextView) this.findViewById(R.id.Accedi);
        info = (ImageView)findViewById(R.id.info);
        show_hide_password = (CheckBox) this.findViewById(R.id.show_hide_password);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed2.getText().toString().contains(":") || ed2.getText().toString().contains(";") || ed3.getText().toString().contains(":") ||
                        ed3.getText().toString().contains(";") || ed4.getText().toString().contains(":") || ed4.getText().toString().contains(":;") ) {
                    Toast.makeText(getApplicationContext(), "Invalid String: ';' and ':' are not allow",Toast.LENGTH_SHORT).show();
                }else{
                    if (!isEmail(ed2)) {
                        Toast.makeText(getApplicationContext(), "Wrong Mail", Toast.LENGTH_SHORT).show();

                    } else if (ed3.getText().toString().equals(ed4.getText().toString())) {
                        String[] email = ed2.getText().toString().split("@");
                        String psw = ed3.getText().toString();
                        username = email[0];
                        String dataret = load(FILE_USER);
                        String[] data = null;
                        if(dataret != null){
                            data = load(FILE_USER).split(";;");
                            for (int i = 0; i < data.length; i++) {
                                if (data[i].split(";")[0].split(":")[1].equals(username)) {
                                    Toast.makeText(getApplicationContext(),
                                            "Username " + username + " not available!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        }

                        String[] datauser = load(FILE_ALLUSERS).split(";;");

                        for (int i = 0; i < datauser.length; i++) {
                            if (datauser[i].split(";")[0].split(":")[0].equals(username)) {
                                Toast.makeText(getApplicationContext(),
                                        "Username " + username + " not available!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        Toast.makeText(getApplicationContext(),
                                "Registered " + username, Toast.LENGTH_SHORT).show();
                        try {
                            saveFile(FILE_USER, "username:" + username + ";password:" + psw + ";profileImg:imgSrc;;");
                            save(FILE_USERVOTE, "username:" + username);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Intent login = new Intent(signup.this, home.class);
                        login.putExtra("idProfile", username);
                        startActivity(login); // takes the user to the signup activity

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(signup.this, login.class);
                startActivity(login); // takes the user to the signup activity
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_info, null);
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                // dismiss the popup window when touched
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });

            }
        });

        show_hide_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                if (isChecked) {
                    ed3.setInputType(InputType.TYPE_CLASS_TEXT);
                    ed3.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());// show password
                    ed4.setInputType(InputType.TYPE_CLASS_TEXT);
                    ed4.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());// show password
                } else {
                    ed3.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ed3.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());// hide password

                    ed4.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ed4.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());// hide password

                }

            }
        });
    }

    public void saveFile(String FILE_NAME, String txt) throws IOException {
        String res = load(FILE_NAME);
        if (res == null){
            save(FILE_NAME, txt);
        }else {
            save(FILE_NAME, res+txt);
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

    public void save(String FILE_NAME, String text) throws IOException {
        FileOutputStream fos = null;
        fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
        fos.write(text.getBytes());
        ed2.getText().clear();
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
}