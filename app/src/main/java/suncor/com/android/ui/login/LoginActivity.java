package suncor.com.android.ui.login;

import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;
import dagger.android.support.DaggerAppCompatActivity;
import suncor.com.android.R;

public class LoginActivity extends DaggerAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment, new LoginFragment());
        ft.commit();
    }
}