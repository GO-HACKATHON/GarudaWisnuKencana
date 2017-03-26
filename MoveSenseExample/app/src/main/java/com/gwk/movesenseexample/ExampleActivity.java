package com.gwk.movesenseexample;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Michinggun on 3/26/2017.
 */

public class ExampleActivity extends AppCompatActivity {
    public static final int STATE_HOME = 0;
    public static final int STATE_WEATHER = 1;
    public static final int STATE_TIME = 2;
    public static final int STATE_PARSER = 3;
    public static final int[] NAV_ICON = {
            R.drawable.icon_scooter_passive,
            R.drawable.icon_cloud_passive,
            R.drawable.icon_time_passive,
            R.drawable.icon_parser_passive,
    };
    public static final String[] NAV_TITLE = {
            "Home",
            "Weathers",
            "Time",
            "Parser"
    };
    @BindView(R.id.home_tab_layout)
    TabLayout mTabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        ButterKnife.bind(this);
        initTab();
    }

    private void initTab() {
        try {
            for (int i = 0; i < NAV_TITLE.length; i++) {
                mTabLayout.addTab(mTabLayout.newTab());
                setTabAt(i, NAV_ICON[i], NAV_TITLE[i]);
            }
        } catch (Exception e) {
            Log.e("TabLayout", e.getMessage());
        }
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                openPage(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        openPage(STATE_HOME);
    }

    private void setTabAt(int position, @DrawableRes int drawableId, String title) {
        View tab = LayoutInflater.from(this).inflate(R.layout.tab_icon_layout, null);
        TextView text = ButterKnife.findById(tab, R.id.tab_icon);
        Drawable image = ContextCompat.getDrawable(this, drawableId);
        image.setBounds(0, 0, (int) ConvertDpToPixel(this, 32), (int) ConvertDpToPixel(this, 32));
        text.setCompoundDrawables(null, image, null, null);
        text.setText(title);
        mTabLayout.getTabAt(position).setCustomView(tab);
    }

    private void openPage(int position) {
        if (isFinishing()) return;
        Fragment fragment;
        switch (position) {
            case STATE_HOME:
                //fragment = HomeFragment.newInstance();
                fragment = HomeFragment.newInstance();
                break;
            case STATE_WEATHER:
                fragment = WeatherFragment.newInstance();
                break;
            case STATE_TIME:
                fragment = TimeFragment.newInstance();
                break;
            case STATE_PARSER:
                fragment = ParserFragment.newInstance();
                break;
            default:
                fragment = HomeFragment.newInstance();
                break;
        }
        if (!mTabLayout.getTabAt(position).isSelected()) {
            mTabLayout.getTabAt(position).select();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_container, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public static float ConvertDpToPixel(Context context, float dp) {
        if (context == null) return 0;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }
}
