package cn.jetoo.numbermgr;

import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;

import cn.jetoo.numbermgr.R;
import cn.jetoo.numbermgr.intercept.InterceptActivity;
import cn.jetoo.numbermgr.numbers.NumberSetActivity;
import cn.jetoo.numbermgr.query.QueryLocationActivity;
import cn.jetoo.numbermgr.settings.SettingsActivity;

public class MainActivity extends Activity implements View.OnClickListener {

    private View mQueryLocation;
    private View mPhoneIntercept;
    private View mCommonNumbers;
    private View mSettings;

    public void initViews() {
        setContentView(R.layout.main_screen);
        mQueryLocation = findViewById(R.id.item_query_location);
        mQueryLocation.setOnClickListener(this);
        mPhoneIntercept = findViewById(R.id.item_phone_intercept);
        mPhoneIntercept.setOnClickListener(this);
        mCommonNumbers = findViewById(R.id.item_common_numbers);
        mCommonNumbers.setOnClickListener(this);
        mSettings = findViewById(R.id.item_global_settings);
        mSettings.setOnClickListener(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initViews();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {

        if (v == mQueryLocation) {
            startActivity(QueryLocationActivity.class);
        } else if (v == mPhoneIntercept) {
            startActivity(InterceptActivity.class);
        } else if (v == mCommonNumbers) {
            startActivity(NumberSetActivity.class);
        } else if (v == mSettings) {
            startActivity(SettingsActivity.class);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_more_settings: {
                startActivity(SettingsActivity.class);
                break;
            }
            case R.id.menu_more_about: {
                startActivity(AboutActivity.class);
                break;
            }
        }
        return true;
    }

    private void startActivity(Class<?> cls) {
        Intent it = new Intent(this, cls);
        startActivity(it);
    }
}
