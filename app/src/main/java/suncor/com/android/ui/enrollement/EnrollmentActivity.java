package suncor.com.android.ui.enrollement;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import suncor.com.android.R;
import suncor.com.android.ui.common.OnBackPressedListener;

public class EnrollmentActivity extends AppCompatActivity {
    private FrameLayout enrolMainFrame;
    private OnBackPressedListener onBackPressedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment);
        enrolMainFrame = findViewById(R.id.enrollment_main_frame);
        makeTransaction();
    }

    private void makeTransaction() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.enrollment_main_frame, new CardQuestion());
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null) {
            onBackPressedListener.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }
}
