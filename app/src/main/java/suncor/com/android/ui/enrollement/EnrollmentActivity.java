package suncor.com.android.ui.enrollement;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import suncor.com.android.R;

public class EnrollmentActivity extends AppCompatActivity {
    private FrameLayout enrolMainFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment);
        enrolMainFrame = findViewById(R.id.enrolMainFrame);
        makeTransaction();

    }

    private void makeTransaction() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.enrolMainFrame, new CardQuestion());
        ft.commit();
    }
}
