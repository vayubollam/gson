package suncor.com.android.ui.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;
import dagger.android.support.DaggerAppCompatActivity;
import suncor.com.android.R;
import suncor.com.android.ui.main.MainActivity;
import suncor.com.android.ui.main.profile.info.PersonalInfoFragment;

public class LoginActivity extends DaggerAppCompatActivity {

    public final static String LOGIN_FROM_ENROLLMENT_EXTRA = "suncor.com.android.fromenrollment";
    public final static String LOGIN_FROM_RESET_PASSWORD_EXTRA = "RESET_PASSWORD";
    public static final String IS_COMING_FROM_DEEPLINK = "IS_COMING_FROM_DEEPLINK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment, LoginFragment.newInstance(getIntent().getBooleanExtra(LOGIN_FROM_ENROLLMENT_EXTRA, false),
                getIntent().getBooleanExtra(LOGIN_FROM_RESET_PASSWORD_EXTRA, false),
                getIntent().getStringExtra(PersonalInfoFragment.EMAIL_EXTRA)));
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       boolean isComingFromDeepLink = getIntent().getBooleanExtra(LoginActivity.IS_COMING_FROM_DEEPLINK, false);
        if(isComingFromDeepLink){
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return;
        }
    }
}