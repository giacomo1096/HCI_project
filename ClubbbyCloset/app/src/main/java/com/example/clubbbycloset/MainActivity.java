package com.example.clubbbycloset;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private static final String FILE_ALLUSERS = "allUsersData.txt";
    private String users = "ginger:gin0;imgSrc:gin3;descrizione:American streetwear:School or a walk with friends:Afternoon:Tshirt bit.ly/3d7wXXM;;" +
            "ginger:gin0;imgSrc:gin5;descrizione:Today is the last day!!!:School:Morning:Shoes bit.ly/3qhOeCY;;" +
            "ginger:gin0;imgSrc:gin6;descrizione:Lipstick makes everything better:Elegant party:Evening:Earrings bit.ly/2UuYcoK;;" +
            "ginger:gin0;imgSrc:gin7;descrizione:We love the same style, don’t we?:School trip:All day:Bandana bit.ly/35KNDjw;;" +
            "ginger:gin0;imgSrc:gin8;descrizione:I will have to take off my hat, but it is an essential part of my outfit:School day:Morning:Hat bit.ly/3xIXz9m;;" +
            //"ginger:gin0;imgSrc:gin9;descrizione:Summer is magic and retro style:Sea:Afternoon:No link available;;" +
            "ginger:gin0;imgSrc:gin10;descrizione:I feel like a business woman:Pizza restaurant or pub:Afternoon or evening:Coat bit.ly/3wV73hU;;" +
            "ginger:gin0;imgSrc:gin11;descrizione:Happy birthday to me!:Holiday farm:Evening:No link available;;" +
            "ginger:gin0;imgSrc:gin12;descrizione:After school, I have found a job in order to save some money:Part-time babysitting:All day:Jeans bit.ly/3gXqcJc;;" +
            "giulia_:giu0;imgSrc:giu5;descrizione:All day out moving with public transport:University:Morning or afternoon:Hoodie bit.ly/2TVJsPx;;" +
            "giulia_:giu0;imgSrc:giu6;descrizione:Today I make a toast with my friends and a glass of wine!:City centre or special restaurant:Afternoon or evening:Coat bit.ly/3h1Bjkc;;" +
            "giulia_:giu0;imgSrc:giu7;descrizione:Really comfortable, but with a touch of style:Lecture day at university, then work in a hurry:All day:Sneakers bit.ly/2SRlDs0;;" +
            "giulia_:giu0;imgSrc:giu8;descrizione:This never goes out of fashion!:Job or a trip with friends:All day:Shirt bit.ly/3gREdJH;;" +
            "giulia_:giu0;imgSrc:giu9;descrizione:My best friend has officially graduated!!!:Pool party:Evening:Earrings bit.ly/35JIUic;;" +
            "giulia_:giu0;imgSrc:giu10;descrizione:Tonight I will try a new cute restaurant:Typical bistrot:Summer night:Dress bit.ly/2TYtkNb;;" +
            "giulia_:giu0;imgSrc:giu11;descrizione:Chilling after a working day:Cocktail bar:Afternoon or evening:Blazer amzn.to/3qmyrTp;;" +
            "giulia_:giu0;imgSrc:giu12;descrizione:Do you feel like taking a walk?:Park or university:All day:Pants bit.ly/3wUVJlJ;;" +
            /*"giuseppe05:peppe0;imgSrc:peppe1;descrizione:provadescri2:provalocatio2:provaora2:provalink2;;" +
            "giuseppe05:peppe0;imgSrc:peppe2;descrizione:provadescri2:provalocatio2:provaora2:provalink2;;" +
            "giuseppe05:peppe0;imgSrc:peppe3;descrizione:provadescri2:provalocatio2:provaora2:provalink2;;" +
            "giuseppe05:peppe0;imgSrc:peppe4;descrizione:provadescri2:provalocatio2:provaora2:provalink2;;" +
            "giuseppe05:peppe0;imgSrc:peppe5;descrizione:provadescri2:provalocatio2:provaora2:provalink2;;" +
            "giuseppe05:peppe0;imgSrc:peppe6;descrizione:provadescri2:provalocatio2:provaora2:provalink2;;" +
            "giuseppe05:peppe0;imgSrc:peppe7;descrizione:provadescri2:provalocatio2:provaora2:provalink2;;" +
            "gloria.z:glo0;imgSrc:glo3;descrizione:provadescri3:provalocatio3:provaora3:provalink3;;" +
            "gloria.z:glo0;imgSrc:glo4;descrizione:provadescri3:provalocatio3:provaora3:provalink3;;" +
            "gloria.z:glo0;imgSrc:glo5;descrizione:provadescri3:provalocatio3:provaora3:provalink3;;" +
            "gloria.z:glo0;imgSrc:glo6;descrizione:provadescri3:provalocatio3:provaora3:provalink3;;" +
            "gloria.z:glo0;imgSrc:glo7;descrizione:provadescri3:provalocatio3:provaora3:provalink3;;" +
            "gloria.z:glo0;imgSrc:glo8;descrizione:provadescri3:provalocatio3:provaora3:provalink3;;" +
            "gloria.z:glo0;imgSrc:glo9;descrizione:provadescri3:provalocatio3:provaora3:provalink3;;" +*/
            "manuelito:manu0;imgSrc:manu5;descrizione:A walk in the street market:Neighborhood:Afternoon:Outfit bit.ly/3j3AKsL;;" +
            "manuelito:manu0;imgSrc:manu6;descrizione:Chilling!:City centre:Morning or afternoon:Andrea Pompilio limited edition shirt bit.ly/3qjKOj3;;" +
            "manuelito:manu0;imgSrc:manu7;descrizione:Matching outfits are the best:Pub for a beer:Morning or afternoon:MSGM limited capsule collection bit.ly/2UAbf8x;;" +
            "manuelito:manu0;imgSrc:manu8;descrizione:First day of work, hoping for the best:Dress shop:All day:Jumper bit.ly/3j6kION;;" +
            "manuelito:manu0;imgSrc:manu9;descrizione:My idea of suit:City centre:Afternoon:No link available;;" +
            "manuelito:manu0;imgSrc:manu10;descrizione:Workout day:Park:Afternoon:Sneakers bit.ly/3qlWrWT;;" +
            "manuelito:manu0;imgSrc:manu11;descrizione:I have some errands to run:City centre, then gym:Afternoon:Trousers bit.ly/3gRNEsg;;" +
            "manuelito:manu0;imgSrc:manu12;descrizione:Studio session done:Music studio:Night:No link available;;" +
            "manuelito:manu0;imgSrc:manu13;descrizione:Another training session, finally outdoor!:Park:Morning:Tracksuit Limited Edition bit.ly/3qsHYII;;"
            /*"willB:will0;imgSrc:will3;descrizione:provadescri5:provalocatio5:provaora5:provalink5;;" +
            "willB:will0;imgSrc:will4;descrizione:provadescri5:provalocatio5:provaora5:provalink5;;" +
            "willB:will0;imgSrc:will5;descrizione:provadescri5:provalocatio5:provaora5:provalink5;;" +
            "willB:will0;imgSrc:will6;descrizione:provadescri5:provalocatio5:provaora5:provalink5;;" +
            "willB:will0;imgSrc:will7;descrizione:provadescri5:provalocatio5:provaora5:provalink5;;"*/;

    private static final String FILE_TOPICS = "topics.txt";
    private String topics = "topic:Special Occasion:occasion:special:evento:wedding:party:pool:birthday:cerimony:graduation;;" +
            "ginger:gin0;imgSrc:gin6;descrizione:Lipstick makes everything better:Elegant party:Evening:Earrings bit.ly/2UuYcoK;;" +
            //"gloria.z:glo0;imgSrc:glo5;descrizione:provadescri1:provalocatio1:provaora1:bit.ly/2TZXvTV;;" +
            //"giuseppe05:peppe0;imgSrc:peppe4;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            //"giuseppe05:peppe0;imgSrc:peppe3;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            "giulia_:giu0;imgSrc:giu10;descrizione:Tonight I will try a new cute restaurant:Typical bistrot:Summer night:Dress bit.ly/2TYtkNb;;" +
            "giulia_:giu0;imgSrc:giu9;descrizione:My best friend has officially graduated!!!:Pool party:Evening:Earrings bit.ly/35JIUic" +

            ";;;topic:Sport Vibes:sport:run:corsa:passeggiata:workout:walking:friends:gym;;" +
            "manuelito:manu0;imgSrc:manu13;descrizione:Another training session, finally outdoor!:Park:Morning:Tracksuit Limited Edition bit.ly/3qsHYII;;" +
            //"willB:will0;imgSrc:will7;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            "manuelito:manu0;imgSrc:manu11;descrizione:I have some errands to run:City centre, then gym:Afternoon:Trousers bit.ly/3gRNEsg;;" +
            "manuelito:manu0;imgSrc:manu10;descrizione:Workout day:Park:Afternoon:Sneakers bit.ly/3qlWrWT;;" +
            "manuelito:manu0;imgSrc:manu5;descrizione:A walk in the street market:Neighborhood:Afternoon:Outfit bit.ly/3j3AKsL;;" +
            //"gloria.z:glo0;imgSrc:glo9;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            //"giuseppe05:peppe0;imgSrc:peppe6;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            "giulia_:giu0;imgSrc:giu12;descrizione:Do you feel like taking a walk?:Park or university:All day:Pants bit.ly/3wUVJlJ;;" +
            "ginger:gin0;imgSrc:gin3;descrizione:American streetwear:School or a walk with friends:Afternoon:Tshirt bit.ly/3d7wXXM" +

            ";;;topic:Happy Hour:ape:aperitivo:friends:appointment:cocktail:beer:party;;" +
            "manuelito:manu0;imgSrc:manu6;descrizione:Chilling!:City centre:Morning or afternoon:Andrea Pompilio limited edition shirt bit.ly/3qjKOj3;;" +
            //"gloria.z:glo0;imgSrc:glo6;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            //"willB:will0;imgSrc:will3;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            "manuelito:manu0;imgSrc:manu7;descrizione:Matching outfits are the best:Pub for a beer:Morning or afternoon:MSGM limited capsule collection bit.ly/2UAbf8x;;" +
            //"gloria.z:glo0;imgSrc:glo3;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            //"giuseppe05:peppe0;imgSrc:peppe5;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            //"giuseppe05:peppe0;imgSrc:peppe1;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            "giulia_:giu0;imgSrc:giu11;descrizione:Chilling after a working day:Cocktail bar:Afternoon or evening:Blazer amzn.to/3qmyrTp;;" +
            "giulia_:giu0;imgSrc:giu9;descrizione:My best friend has officially graduated!!!:Pool party:Evening:Earrings bit.ly/35JIUic;;" +
            "giulia_:giu0;imgSrc:giu6;descrizioneToday I make a toast with my friends and a glass of wine!:City centre or special restaurant:Afternoon or evening:Coat bit.ly/3h1Bjkc;;" +
            "ginger:gin0;imgSrc:gin10;descrizione:I feel like a business woman:Pizza restaurant or pub:Afternoon or evening:Coat bit.ly/3wV73hU;;" +
            "ginger:gin0;imgSrc:gin11;descrizione:Happy birthday to me!:Holiday farm:Evening:No link available" +

            ";;;topic:Back to school:school:scuola:liceo:college:university:library:caffee;;" +
            "ginger:gin0;imgSrc:gin12;descrizione:After school, I have found a job in order to save some money:Part-time babysitting:All day:Jeans bit.ly/3gXqcJc;;" +
            "manuelito:manu0;imgSrc:manu8;descrizione:First day of work, hoping for the best:Dress shop:All day:Jumper bit.ly/3j6kION;;" +
            "manuelito:manu0;imgSrc:manu6;descrizione:Chilling!:City centre:Morning or afternoon:Andrea Pompilio limited edition shirt bit.ly/3qjKOj3;;" +
            //"gloria.z:glo0;imgSrc:glo7;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            //"gloria.z:glo0;imgSrc:glo4;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            //"willB:will0;imgSrc:will3;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            //"giuseppe05:peppe0;imgSrc:peppe6;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            "giulia_:giu0;imgSrc:giu12;descrizione:Do you feel like taking a walk?:Park or university:All day:Pants bit.ly/3wUVJlJ;;" +
            "giulia_:giu0;imgSrc:giu7;descrizione:Really comfortable, but with a touch of style:Lecture day at university, then work in a hurry:All day:Sneakers bit.ly/2SRlDs0;;" +
            "giulia_:giu0;imgSrc:giu8;descrizione:This never goes out of fashion!:Job or a trip with friends:All day:Shirt bit.ly/3gREdJH;;" +
            "giulia_:giu0;imgSrc:giu5;descrizione:All day out moving with public transport:University:Morning or afternoon:Hoodie bit.ly/2TVJsPx;;" +
            "ginger:gin0;imgSrc:gin8;descrizione:I will have to take off my hat, but it is an essential part of my outfit:School day:Morning:Hat bit.ly/3xIXz9m;;" +
            "ginger:gin0;imgSrc:gin7;descrizione:We love the same style, don’t we?:School trip:All day:Bandana bit.ly/35KNDjw;;" +
            "ginger:gin0;imgSrc:gin5;descrizione:Today is the last day!!!:School:Morning:Shoes bit.ly/3qhOeCY;;" +
            "ginger:gin0;imgSrc:gin3;descrizione:American streetwear:School or a walk with friends:Afternoon:Tshirt bit.ly/3d7wXXM" +

            ";;;topic:Dinner Night:dinner:appointment:friends:restourant:party:night;;" +
            "giulia_:giu0;imgSrc:giu10;Tonight I will try a new cute restaurant:Typical bistrot:Summer night:Dress bit.ly/2TYtkNb;;" +
            //"gloria.z:glo0;imgSrc:glo8;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            //"giuseppe05:peppe0;imgSrc:peppe3;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            //"giuseppe05:peppe0;imgSrc:peppe2;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            //"willB:will0;imgSrc:will7;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            "giulia_:giu0;imgSrc:giu6;descrizione:Today I make a toast with my friends and a glass of wine!:City centre or special restaurant:Afternoon or evening:Coat bit.ly/3h1Bjkc;;" +
            "ginger:gin0;imgSrc:gin11;descrizione:Happy birthday to me!:Holiday farm:Evening:No link available;;" +
            "ginger:gin0;imgSrc:gin10;descrizione:I feel like a business woman:Pizza restaurant or pub:Afternoon or evening:Coat bit.ly/3wV73hU" +

            ";;;topic:Working time:working:work:formal:job:office:interview:smart;;" +
            "giulia_:giu0;imgSrc:giu8;descrizione:This never goes out of fashion!:Job or a trip with friends:All day:Shirt bit.ly/3gREdJH;;" +
            //"giuseppe05:peppe0;imgSrc:peppe5;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            //"gloria.z:glo0;imgSrc:glo7;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            "manuelito:manu0;imgSrc:manu8;descrizione:First day of work, hoping for the best:Dress shop:All day:Jumper bit.ly/3j6kION;;" +
            //"gloria.z:glo0;imgSrc:glo4;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            //"gloria.z:glo0;imgSrc:glo3;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            //"giuseppe05:peppe0;imgSrc:peppe2;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            //"giuseppe05:peppe0;imgSrc:peppe1;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            "giulia_:giu0;imgSrc:giu11;descrizione:Chilling after a working day:Cocktail bar:Afternoon or evening:Blazer amzn.to/3qmyrTp;;" +
            "giulia_:giu0;imgSrc:giu7;descrizione:Really comfortable, but with a touch of style:Lecture day at university, then work in a hurry:All day:Sneakers bit.ly/2SRlDs0;;" +
            "ginger:gin0;imgSrc:gin12;descrizione:After school, I have found a job in order to save some money:Part-time babysitting:All day:Jeans bit.ly/3gXqcJc;;" +
            "ginger:gin0;imgSrc:gin10;descrizione:I feel like a business woman:Pizza restaurant or pub:Afternoon or evening:Coat bit.ly/3wV73hU;;" +
            "ginger:gin0;imgSrc:gin6;descrizione:Lipstick makes everything better:Elegant party:Evening:Earrings bit.ly/2UuYcoK" +

            ";;;topic:On journay:journay:sportivo:vacation:trip:sea:lake:mountain:friends:family;;"+
            //"willB:will0;imgSrc:will3;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            //"giuseppe05:peppe0;imgSrc:peppe6;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            //"giuseppe05:peppe0;imgSrc:peppe5;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            //"giuseppe05:peppe0;imgSrc:peppe1;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            //"gloria.z:glo0;imgSrc:glo9;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            //"gloria.z:glo0;imgSrc:glo6;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            //"gloria.z:glo0;imgSrc:glo4;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            "manuelito:manu0;imgSrc:manu7;descrizione:Matching outfits are the best:Pub for a beer:Morning or afternoon:MSGM limited capsule collection bit.ly/2UAbf8x;;" +
            "giulia_:giu0;imgSrc:giu12;descrizione:Do you feel like taking a walk?:Park or university:All day:Pants bit.ly/3wUVJlJ;;" +
            "giulia_:giu0;imgSrc:giu8;descrizione:This never goes out of fashion!:Job or a trip with friends:All day:Shirt bit.ly/3gREdJH;;" +
            "giulia_:giu0;imgSrc:giu5;descrizione:All day out moving with public transport:University:Morning or afternoon:Hoodie bit.ly/2TVJsPx;;" +
            "ginger:gin0;imgSrc:gin12;descrizione:After school, I have found a job in order to save some money:Part-time babysitting:All day:Jeans bit.ly/3gXqcJc;;" +
            "ginger:gin0;imgSrc:gin7;descrizione:We love the same style, don’t we?:School trip:All day:Bandana bit.ly/35KNDjw;;" +
            "ginger:gin0;imgSrc:gin5;descrizione:Today is the last day!!!:School:Morning:Shoes bit.ly/3qhOeCY;;" +
            "ginger:gin0;imgSrc:gin3;descrizione:American streetwear:School or a walk with friends:Afternoon:Tshirt bit.ly/3d7wXXM" +

            ";;;topic:Summertime:bikini:beach:sea:surf:friends:family:sun:sunny:summer;;"+
            "giulia_:giu0;imgSrc:giu4;descrizione:Can’t wait for taking a swim:Beach resort:Morning or afternoon:Shorts bit.ly/2TZXvTV;;" +
            //"willB:will0;imgSrc:will5;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            //"willB:will0;imgSrc:will6;descrizione:provadescri1:provalocatio1:provaora1:provalink1;;" +
            "manuelito:manu0;imgSrc:manu2;descrizione:Hawaiian vibes:Happy hour on the beach:Afternoon:No link available;;" +
            "giulia_:giu0;imgSrc:giu10;descrizione:Tonight I will try a new cute restaurant:Typical bistrot:Summer night:Dress bit.ly/2TYtkNb;;" +
            "giulia_:giu0;imgSrc:giu9;descrizione:My best friend has officially graduated!!!:Pool party:Evening:Earrings bit.ly/35JIUic;;" +
            "ginger:gin0;imgSrc:gin11;descrizione:Happy birthday to me!:Holiday farm:Evening:No link available" ;
            //"ginger:gin0;imgSrc:gin9;descrizione:Summer is magic and retro style:Sea:Afternoon:No link available";

    private  static final String FILE_ALLVOTE = "allVote.txt";
    private String votes = "giulia_:giu0;voteSrc:giu3:giu4;description:Which one is more comfortable for going to the beach? Tomorrow will be very sunny and hot!:Sea:All day;vote:30:28;;" +
            "manuelito:manu0;voteSrc:manu1:manu2;description:Which one?:Happy hour on the beach:Afternoon;vote:5:13;;" +
            "ginger:gin0;voteSrc:gin1:gin2;description:Mum’s wedding and these are my two options:Refined restaurant:From 4 pm to late night;vote:2:8;;" +
            "giulia_:giu0;voteSrc:giu1:giu2;description:Help! Black or white today?:Happy hour with my besties:Afternoon;vote:0:0;;" +
            //"gloria.z:glo0;voteSrc:glo1:glo2;description:provasedcrizione2:provalovation2:provaorario2;vote:0:0;;" +
            "manuelito:manu0;voteSrc:manu3:manu4;description:Tomorrow I’m going to Milan:Train journey:Evening;vote:40:20;;";
            //"willB:will0;voteSrc:will1:will2;description:provasedcrizione4:provalovation4:provaorario4;vote:10:3;;" ;

    private  static final String FILE_WHOVOTED = "whoVoted.txt";
    private String whovoted = "ginger;voteSrc:gin1:gin2;users;;" +
            "giulia_;voteSrc:giu3:giu4;users;;" +
            "ginger;voteSrc:gin3:gin4;users;;" +
            "giulia_;voteSrc:giu1:giu2;users;;" +
            //"gloria.z;voteSrc:glo1:glo2;users;;" +
            "manuelito;voteSrc:manu1:manu2;users;;" +
            "manuelito;voteSrc:manu3:manu4;users;;" ;
            //"willB;voteSrc:will1:will2;users;;" ;
    private  static final String FILE_USERBIO = "userBio.txt";
    private String userBio = "ginger:17 yo - London - Shopping lover;" +
            "giulia_:24 yo - Milan - Freelance&Student;" +
            "gloria.z:28 yo - Naples - Student;" +
            "manuelito:30 yo - Barcelona - DJ & Producer;" +
            "giuseppe05:26 yo - Berlin - Junior Manager;" +
            "willB: 15 yo - NY - Tv show addicted;" ;

    private static final String FILE_USER = "userdata.txt";
    Button b1;
    TextView b2,forgot;
    EditText ed1,ed2;
    CheckBox show_hide_password;
    ImageView info ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        b1 = (Button)findViewById(R.id.btnlogin);
        ed1 = (EditText)findViewById(R.id.et_email);
        ed2 = (EditText)findViewById(R.id.et_password);
        b2 = (TextView) findViewById(R.id.newAccount);
        forgot = (TextView) findViewById(R.id.forgot);
        info = (ImageView)findViewById(R.id.info);
        show_hide_password = (CheckBox) this.findViewById(R.id.show_hide_password);

        try {
            saveFile(users, FILE_ALLUSERS);
            saveFile(topics, FILE_TOPICS);
            saveFile(votes, FILE_ALLVOTE);
            saveFile(whovoted, FILE_WHOVOTED);
            saveFile(userBio, FILE_USERBIO);
        } catch (IOException e) {
            e.printStackTrace();
        }

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ret = load(FILE_USER);
                String name= null;
                String psw= null;
                int ok = -1;
                if (ret!=null){
                    String[] t = ret.split(";;");
                    for(int i =0; i<t.length; i++) {
                        name = t[i].split(";")[0].split(":")[1];
                        psw = t[i].split(";")[1].split(":")[1];
                        if (name.equals(ed1.getText().toString()) && psw.equals(ed2.getText().toString())) {
                            ok = 1;
                            Intent home = new Intent(MainActivity.this, home.class);
                            home.putExtra("idProfile", name);
                            startActivity(home); // takes the user to the signup activity
                        }
                    }
                    if(ok < 1) {
                        Toast.makeText(getApplicationContext(), "WrongCredentials", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "No Valid account please SIGN UP", Toast.LENGTH_SHORT).show();
                }
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup = new Intent(MainActivity.this, signup.class);
                startActivity(signup); // takes the user to the signup activity
            }

        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupView = inflater.inflate(R.layout.popup_forgot, null);
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
                    ed2.setInputType(InputType.TYPE_CLASS_TEXT);
                    ed2.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());// show password
                } else {
                    ed2.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ed2.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());// hide password

                }

            }
        });

    }

    public void saveFile(String txt,String FILE_NAME) throws IOException {

        String res = load(FILE_NAME);
        if (res == null){
            save(txt, FILE_NAME);
        }else {
            save(res,FILE_NAME);
        }
    }

    public void save(String text, String FILE_NAME) throws IOException {
        FileOutputStream fos = null;
        fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
        fos.write(text.getBytes());
        //Toast.makeText(getApplicationContext(), "Scritto "+text, Toast.LENGTH_SHORT).show();
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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


}