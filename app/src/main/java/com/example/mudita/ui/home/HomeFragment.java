package com.example.mudita.ui.home;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mudita.DBtoPhone;
import com.example.mudita.DiskpersistenceHelper;
import com.example.mudita.Mainscreen1;
import com.example.mudita.Medtimeobj;
import com.example.mudita.Myadapter2;
import com.example.mudita.NotificationHelper;
import com.example.mudita.R;
import com.example.mudita.Token;
import com.example.mudita.Welcomescreen;
import com.example.mudita.addactivity;
import com.example.mudita.missedact;
import com.example.mudita.statistics;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static com.facebook.FacebookSdk.getApplicationContext;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private ImageView tempsignout, profilepic, Trail, missed, add;
    private FirebaseAuth firebaseAuth;
    private LoginManager loginManager;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView texttips, usernametxt;
    private int tipno,count;
    private String tipstr, usernamestr, profileurl, sr;
    private static final String TAG = "facebook";
    private static final long TIME_IN_MILLIS_CONST = 172800000;
    private TextView daytxt, hourtxt, mintxt, sectxt,medtxt;
    private CountDownTimer countDownTimer;
    private long TIME_IN_MILLIS = TIME_IN_MILLIS_CONST;
    private int curmin, curhour, nxtmin, nxthour, getCurmin, getNxtmin, getNxthour, getCurhour;
    private long diffhour, diffmin;
    private  String Medicinestr=new String();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        //Profile photo and username



        //Tips
        tipno = new Random().nextInt(15);
        tipstr = "tip" + tipno;


        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ///final TextView textView = root.findViewById(R.id.text_home);


        //username and profile photo from facebook/
        try {
            usernamestr = getArguments().getString("username");
        } catch (Exception e) {
            Log.d(TAG, "username: " + e);
            usernamestr = "noname";
        }
        try {
            profileurl = getArguments().getString("photourl");
        } catch (Exception e) {
            Log.d(TAG, "url: " + e);
            profileurl = "nophoto";
        }
      /*  try{ sr=getParentFragment().toString();
            Log.d(TAG,"Parent Frag"+sr);}
        catch (Exception e)
        {Log.d(TAG,"Parent Frag"+sr);}*/
      SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences(getString(R.string.usernameshpref),Context.MODE_PRIVATE);



        usernametxt = (TextView) root.findViewById(R.id.usernamemain);
        profilepic = (ImageView) root.findViewById(R.id.usericon);
        Log.d(TAG, "We are on HomeFrag: " + usernamestr);
        Log.d(TAG, "We are on HomeFrag: " + profileurl);



       //Dekhlo jo tum dhund rhe ho khi woh yehi to nhi
        if (!usernamestr.equals("noname")) {
            usernametxt.setText(usernamestr);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("usernamecurr",usernamestr.trim());
            editor.apply();
        /*  DatabaseReference rootdb=FirebaseDatabase.getInstance().getReference();
        FirebaseUser user=firebaseAuth.getCurrentUser();*/
       FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {

            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(!task.isSuccessful())
                { }
                else
                {
                    String refreshtoken=task.getResult().getToken();
                    updateToken(refreshtoken);
                }

            }
        });





         /* rootdb.child("Token").child(usernamestr).setValue();*/
        } else if (usernamestr == null || usernamestr == "noname") {
            usernametxt.setText("UserName");
        }

        if (!profileurl.equals("nophoto")) {
            profileurl = profileurl + "?type=large";
            Picasso.get().load(profileurl).into(profilepic);
        } else if (profileurl == "nophoto" || profileurl == null) {
            profilepic.setImageResource(R.drawable.usericon);
        }

        //Health Tips
        texttips = root.findViewById(R.id.puratip);
        texttips.setText(getResources().getIdentifier(tipstr, "string", getActivity().getPackageName()));

        //add button oncicklistener
        add = (ImageView) root.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentadd = new Intent(getActivity(), addactivity.class);
                startActivity(intentadd);
            }
        });


        //Temp Signout
        tempsignout = (ImageView) root.findViewById(R.id.card3);
        tempsignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth = FirebaseAuth.getInstance();
                loginManager = LoginManager.getInstance();


                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    firebaseAuth.signOut();
                    loginManager.logOut();
                    mGoogleSignInClient.signOut();

                    Intent intent = new Intent(getActivity(), Welcomescreen.class);
                    startActivity(intent);


                }

            }
        });
        missed = (ImageView) root.findViewById(R.id.missed);

        //Statistics onclicklistener
        Trail = (ImageView) root.findViewById(R.id.stats);
        Trail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getActivity(), statistics.class);
                startActivity(intent2);

            }
        });
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });

        //missed onclicklistener
        missed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent intent3 = new Intent(getActivity(), missedact.class);
                startActivity(intent3);
            }
        });


        //Timer
        daytxt = (TextView) root.findViewById(R.id.daystxt);
        hourtxt = (TextView) root.findViewById(R.id.hourstxt);
        mintxt = (TextView) root.findViewById(R.id.minstxt);
        sectxt = (TextView) root.findViewById(R.id.sectxt);
        medtxt=(TextView)root.findViewById(R.id.pandingtask);
//        timerstart();


        return root;
    }

    public ArrayList<Medtimeobj> timerstart(ArrayList<Medtimeobj> arrayList) {
           if(arrayList.size()==0)
           {return arrayList;}
           try {  Collections.sort(arrayList, new Comparator<Medtimeobj>() {
               @Override
               public int compare(Medtimeobj o1, Medtimeobj o2) {
                   int a, b;
                   a = Integer.parseInt(o1.getTime());
                   b = Integer.parseInt(o2.getTime());
                   return a < b ? -1 : 1;

               }
           });

           }
           catch (IllegalArgumentException e)
           {Log.d("Exception"," "+e ,null);
           return arrayList;}


        Calendar calendar = Calendar.getInstance();
        int hour, min, sec;
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);

        String minstr;
        if (min < 10) {
            minstr = "0" + min;
        } else {
            minstr = Integer.toString(min);
        }
        String Curtimestr = "" + hour + minstr;
        String Nexttimestr = new String();

        for (int i = 0; i < arrayList.size(); i++) {
            int cur, nxt;
            cur = Integer.parseInt(Curtimestr);
            nxt = Integer.parseInt(arrayList.get(i).getTime());
            if (cur >= nxt) {
                continue;
            } else {
                Nexttimestr = "" + nxt;
                Medicinestr=arrayList.get(i).getMedicine();
                count=i;
                break;
            }

        }
        Log.d("Timetester", "current " + Curtimestr + " nxt " + Nexttimestr);

        curhour = curmin = nxthour = nxtmin = 0;
        diffhour = diffmin = 0;
        if (!Nexttimestr.isEmpty()) {
            if (Nexttimestr.length() == 4) {
                nxthour = (Nexttimestr.charAt(0) - '0') * 10 + (Nexttimestr.charAt(1) - '0');
                nxtmin = (Nexttimestr.charAt(2) - '0') * 10 + (Nexttimestr.charAt(3) - '0');
            }
            if (Nexttimestr.length() == 3) {
                nxthour = (Nexttimestr.charAt(0) - '0');
                nxtmin = (Nexttimestr.charAt(1) - '0') * 10 + (Nexttimestr.charAt(2) - '0');
            }
            if (Curtimestr.length() == 4) {
                curhour = (Curtimestr.charAt(0) - '0') * 10 + (Curtimestr.charAt(1) - '0');
                curmin = (Curtimestr.charAt(2) - '0') * 10 + (Curtimestr.charAt(3) - '0');
            }
            if (Curtimestr.length() == 3) {
                curhour = (Curtimestr.charAt(0) - '0');
                curmin = (Curtimestr.charAt(1) - '0') * 10 + (Curtimestr.charAt(2) - '0');
            }
                if (Nexttimestr.length() == 2) {
                    nxthour =0;
                    nxtmin = (Nexttimestr.charAt(0) - '0') * 10 + (Nexttimestr.charAt(1) - '0');
            }
            diffmin = (nxtmin - curmin);
            if (diffmin < 0) {
                diffmin = (nxtmin - curmin + 60) * 60 * 1000;
                diffhour = (nxthour - curhour - 1) * 60 * 60 * 1000;
                Log.d("Timetesterlessthnzero", "diffhour " + (diffhour / 3600000) + " diffmin " + (diffmin / 60000), null);
            } else {
                diffhour = (nxthour - curhour) * 60 * 60 * 1000;
                diffmin = (nxtmin - curmin) * 60 * 1000;
                Log.d("Timetestergreaterthnzero", "diffhour " + (diffhour / 3600000) + " diffmin " + (diffmin / 60000), null);
            }
            Log.d("Timetester", "curhour " + curhour + " curmin " + curmin + " nxthour " + nxthour + " nxtmin " + nxtmin);
        }
        long sec1 = 60;
        if (diffhour != 0 || diffmin != 0) {
            sec1 = calendar.get(Calendar.SECOND);
            TIME_IN_MILLIS = diffhour + diffmin - sec1 * 1000;
        }
        Log.d("Timetester", "TIMEINMILLIS " + TIME_IN_MILLIS);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(TIME_IN_MILLIS, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                int d, h, m, s;
                TIME_IN_MILLIS = millisUntilFinished;
                d = (int) (((TIME_IN_MILLIS / 1000) / 60) / 60) / 24;
                h = (int) (((TIME_IN_MILLIS / 1000) / 60) / 60) % 24;
                m = (int) (((TIME_IN_MILLIS / 1000) / 60)) % 60;
                s = (int) (((TIME_IN_MILLIS / 1000) % 60));
                String dd, hh, mm, ss;
                dd = Integer.toString(d);
                hh = Integer.toString(h);
                mm = Integer.toString(m);
                ss = Integer.toString(s);
                daytxt.setText(dd);
                hourtxt.setText(hh);
                mintxt.setText(mm);
                sectxt.setText(ss);
                StringBuilder med=new StringBuilder();
                if(Medicinestr.length()>5)
                { med.append(Medicinestr,0,Medicinestr.length()-5);
                medtxt.setText(med.toString());}
                else
                { medtxt.setText(".......");}


            }

            @Override
            public void onFinish() {

                Thread thread=new Thread(new Runnable() {
                    @Override
                    public void run() {

                            Intent intent = getActivity().getIntent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            getActivity().overridePendingTransition(0, 0);
                            getActivity().finish();

                            getActivity().overridePendingTransition(0, 0);
                            startActivity(intent);



                    }
                });
                try {
                    thread.sleep(12000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                thread.start();


            }
        }.start();
       /* Medtimeobj obj=new Medtimeobj();
        obj.setTime(Nexttimestr);
        StringBuilder stb=new StringBuilder();
        stb.append(Medicinestr,0,Medicinestr.length()-5);
        obj.setMedicine(stb.toString());*/
        for (int i = 0; i < arrayList.size();) {
            int cur, nxt;
            cur = Integer.parseInt(Curtimestr);
            nxt = Integer.parseInt(arrayList.get(i).getTime());
            if (cur >= nxt) {
                arrayList.remove(i);
            } else {
                break;
            }
        }
            for(int i=0;i<arrayList.size()-1;i++)
            {for(int j=i+1;j<arrayList.size();)
            {if(arrayList.get(i).getMedicine().equals(arrayList.get(j).getMedicine())&&arrayList.get(i).getTime().equals(arrayList.get(j).getTime()))
            {arrayList.remove(j);}
            else {j++;}
            }
            }


    return arrayList;}

   /* public void Alarmsetter() {
        Calendar cal = Calendar.getInstance();
        getCurhour = cal.get(Calendar.HOUR_OF_DAY);
        getCurmin = cal.get(Calendar.MINUTE);
        getNxthour = nxthour;
        getNxtmin = nxtmin;*/
      /*  Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(2000);
        }*/
        //AlarmTone ka Channel







       /*




        }


    }*/
       private void updateToken(String refreshtoken) {
           FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
           Token token1=new Token(refreshtoken);
           SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences(getString(R.string.usernameshpref),getApplicationContext().MODE_PRIVATE);
           String username=sharedPreferences.getString("usernamecurr",null);
           FirebaseDatabase.getInstance().getReference().child("Tokens").child(username).setValue(token1);
       }

}
