package com.example.happymealapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.auth.oauth2.GoogleCredentials;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.google.cloud.translate.*;

public class ProfileActivity extends AppCompatActivity {

    Profile profile = Profile.getCurrentProfile();

    private TextView logout, aboutus, PrivacyPolicy, ChangeLanguage;
    private TextView profileName, profileEmail;
    private ImageView profileImage;
    public static String userName = "Anonymous";
    public static String translateCode = "en";
    public static String profileURL = "https://image.flaticon.com/icons/png/512/21/21294.png";

    Translate translate;

    String[] Country_Language;
    HashMap<String,String> Country_code = new HashMap<>();
    private static int selectedIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profileactivity);
        try {
            getTranslateService();

            List<Language> languages = translate.listSupportedLanguages(Translate.LanguageListOption.targetLanguage("en"));

            int idx = 0;
            Country_Language = new String[languages.size()];
            for (Language language : languages) {
                Country_Language[idx++] = language.getName();
                Country_code.put(language.getName(), language.getCode());
            }
        }
        catch(Exception e){

        }
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //set Settings  selected.
        bottomNavigationView.setSelectedItemId(R.id.settings);

        //PerformItemSelectedListener.
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.scanner:
                        startActivity(new Intent(getApplicationContext(),Scanner.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.ar:
                        startActivity(new Intent(getApplicationContext(), AugmentedReality.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.favorites:
                        startActivity(new Intent(getApplicationContext(), Favorites.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.community:
                        startActivity(new Intent(getApplicationContext(),Community.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.settings:
                        return true;

                }
                return false;
            }
        });

        if(AccessToken.getCurrentAccessToken()!=null){
                profileName =  (TextView) findViewById(R.id.username);
                userName = Profile.getCurrentProfile().getName();
                profileName.setText(Profile.getCurrentProfile().getName());

                profileImage = (ImageView) findViewById(R.id.profile);
                profileURL = profile.getProfilePictureUri(150,150).toString();
                Picasso.get().load(profile.getProfilePictureUri(150,150)).into(profileImage);
        }

        logout = (TextView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                userName = "Anonymous";
                profileURL = "https://image.flaticon.com/icons/png/512/21/21294.png";
                if(AccessToken.getCurrentAccessToken()==null){
                    startActivity(new Intent(getApplicationContext(),Scanner.class));
                }
            }
        });

        aboutus = (TextView) findViewById(R.id.aboutus);
        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(),PopupActivity.class);
                in.putExtra("data", "<제작팀>\n Team HappyMeal\n\n 이태희\n\n 조우석\n\n 이준호\n\n 모로 압둘 카림");
                startActivityForResult(in, 1);
            }
        });

        PrivacyPolicy = (TextView) findViewById(R.id.PrivacyPolicy);
        PrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(),PopupActivity.class);
                in.putExtra("data", "HappyMeal built the HappyMeal app as a Free app. This SERVICE is provided by HappyMeal at no cost and is intended for use as is.\n" +
                        "\n" +
                        "This page is used to inform visitors regarding our policies with the collection, use, and disclosure of Personal Information if anyone decided to use our Service.\n" +
                        "\n" +
                        "If you choose to use our Service, then you agree to the collection and use of information in relation to this policy. The Personal Information that we collect is used for providing and improving the Service. We will not use or share your information with anyone except as described in this Privacy Policy.\n" +
                        "\n" +
                        "The terms used in this Privacy Policy have the same meanings as in our Terms and Conditions, which is accessible at HappyMeal unless otherwise defined in this Privacy Policy.\n" +
                        "\n" +
                        "Information Collection and Use\n" +
                        "\n" +
                        "For a better experience, while using our Service, we may require you to provide us with certain personally identifiable information. The information that we request will be retained by us and used as described in this privacy policy.\n" +
                        "\n" +
                        "The app does use third party services that may collect information used to identify you.\n" +
                        "\n" +
                        "Link to privacy policy of third party service providers used by the app\n" +
                        "\n" +
                        "Google Play Services\n" +
                        "Log Data\n" +
                        "\n" +
                        "We want to inform you that whenever you use our Service, in a case of an error in the app we collect data and information (through third party products) on your phone called Log Data. This Log Data may include information such as your device Internet Protocol (“IP”) address, device name, operating system version, the configuration of the app when utilizing our Service, the time and date of your use of the Service, and other statistics.\n" +
                        "\n" +
                        "Cookies\n" +
                        "\n" +
                        "Cookies are files with a small amount of data that are commonly used as anonymous unique identifiers. These are sent to your browser from the websites that you visit and are stored on your device's internal memory.\n" +
                        "\n" +
                        "This Service does not use these “cookies” explicitly. However, the app may use third party code and libraries that use “cookies” to collect information and improve their services. You have the option to either accept or refuse these cookies and know when a cookie is being sent to your device. If you choose to refuse our cookies, you may not be able to use some portions of this Service.\n" +
                        "\n" +
                        "Service Providers\n" +
                        "\n" +
                        "We may employ third-party companies and individuals due to the following reasons:\n" +
                        "\n" +
                        "To facilitate our Service;\n" +
                        "To provide the Service on our behalf;\n" +
                        "To perform Service-related services; or\n" +
                        "To assist us in analyzing how our Service is used.\n" +
                        "We want to inform users of this Service that these third parties have access to your Personal Information. The reason is to perform the tasks assigned to them on our behalf. However, they are obligated not to disclose or use the information for any other purpose.\n" +
                        "\n" +
                        "Security\n" +
                        "\n" +
                        "We value your trust in providing us your Personal Information, thus we are striving to use commercially acceptable means of protecting it. But remember that no method of transmission over the internet, or method of electronic storage is 100% secure and reliable, and we cannot guarantee its absolute security.\n" +
                        "\n" +
                        "Links to Other Sites\n" +
                        "\n" +
                        "This Service may contain links to other sites. If you click on a third-party link, you will be directed to that site. Note that these external sites are not operated by us. Therefore, we strongly advise you to review the Privacy Policy of these websites. We have no control over and assume no responsibility for the content, privacy policies, or practices of any third-party sites or services.\n" +
                        "\n" +
                        "Children’s Privacy\n" +
                        "\n" +
                        "These Services do not address anyone under the age of 13. We do not knowingly collect personally identifiable information from children under 13. In the case we discover that a child under 13 has provided us with personal information, we immediately delete this from our servers. If you are a parent or guardian and you are aware that your child has provided us with personal information, please contact us so that we will be able to do necessary actions.\n" +
                        "\n" +
                        "Changes to This Privacy Policy\n" +
                        "\n" +
                        "We may update our Privacy Policy from time to time. Thus, you are advised to review this page periodically for any changes. We will notify you of any changes by posting the new Privacy Policy on this page.\n" +
                        "\n" +
                        "This policy is effective as of 2020-11-22\n" +
                        "\n" +
                        "Contact Us\n" +
                        "\n" +
                        "If you have any questions or suggestions about our Privacy Policy, do not hesitate to contact us at Abdul.moro4u@gmail.com.");
                startActivityForResult(in, 1);
            }
        });
        ChangeLanguage = (TextView) findViewById(R.id.Change_Language);
        ChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ProfileActivity.this);
                    dialog.setTitle("Select Language")
                            .setSingleChoiceItems(Country_Language, 0, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int position) {
                                    selectedIndex = position;
                                }
                            })
                            .setPositiveButton("select", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(),Country_Language[selectedIndex],Toast.LENGTH_LONG).show();
                                    translateCode = Country_code.get(Country_Language[selectedIndex]);
                                    Log.d("Profile - Code",translateCode);
                                }
                            }).create().show();
                }
                catch(Exception e){
                    Log.d("Change Language Error", String.valueOf(e));
                }
            }
        });
    }

    public void getTranslateService() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try (InputStream is = getResources().openRawResource(R.raw.credentials)) {
            //Get credentials:
            final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);

            //Set credentials and get translate service:
            TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
            translate = translateOptions.getService();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //데이터 받기
                String result = data.getStringExtra("result");
                aboutus.setText(result);
            }
        }
    }

    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}