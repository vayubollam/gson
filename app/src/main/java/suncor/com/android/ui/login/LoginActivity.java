package suncor.com.android.ui.login;

import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;
import dagger.android.support.DaggerAppCompatActivity;
import suncor.com.android.R;

public class LoginActivity extends DaggerAppCompatActivity {

    public final static String LOGIN_FROM_ENROLLMENT_EXTRA = "suncor.com.android.fromenrollment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment, LoginFragment.newInstance(getIntent().getBooleanExtra(LOGIN_FROM_ENROLLMENT_EXTRA, false)));
        ft.commit();
    }
}