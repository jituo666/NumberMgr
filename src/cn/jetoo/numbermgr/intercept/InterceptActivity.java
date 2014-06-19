package cn.jetoo.numbermgr.intercept;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import cn.jetoo.numbermgr.R;

public class InterceptActivity extends FragmentActivity implements ActionBar.TabListener {

    private static final int FRAGMENT_INDEX_SMS = 0;
    private static final int FRAGMENT_INDEX_CALLS = 1;
    private static final int FRAGMENT_INDEX_BLACKLIST = 2;

    private final int[] FRAMGMENT_TITLE_RES_IDS = new int[] {
            R.string.intercept_sms,
            R.string.intercept_calls,
            R.string.intercept_blacklist
    };

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.intercept_main);
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case FRAGMENT_INDEX_SMS:
                    return new SmsFragment();
                case FRAGMENT_INDEX_CALLS:
                    return new CallsFragment();
                case FRAGMENT_INDEX_BLACKLIST:
                    return new BlackListFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return FRAMGMENT_TITLE_RES_IDS.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(FRAMGMENT_TITLE_RES_IDS[position]);
        }

    }
}
