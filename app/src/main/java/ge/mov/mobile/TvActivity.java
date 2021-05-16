package ge.mov.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import ge.mov.mobile.ui.activity.main.MainActivity;
import ge.mov.mobile.util.Const;

public class TvActivity extends Activity {
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_tv);

        Const.Companion.setTV(true);
        intent = new Intent(this, MainActivity.class);
        start();
    }

    private void start() {
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }
}
