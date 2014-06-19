package cn.jetoo.numbermgr.query;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.SearchView;
import android.widget.TextView;

import cn.jetoo.numbermgr.R;
import cn.jetoo.numbermgr.utils.FeatureConfig;

public class QueryLocationActivity extends FragmentActivity implements SearchView.OnQueryTextListener,
        ActionBar.TabListener {

    private static final String TAG = "QueryLocationActivity";
    private static final boolean DEBUG = FeatureConfig.DEBUG_LOG;

    private static final int FRAGMENT_INDEX_CALLLOG = 0;
    private static final int FRAGMENT_INDEX_CONTACT = 1;
    private static final int FRAGMENT_INDEX_SMS = 2;

    private final int[] FRAMGMENT_TITLE_RES_IDS = new int[] {
            R.string.location_call_log,
            R.string.location_contact,
            R.string.location_sms
    };

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private SearchView mSearchView;

    private void initViews() {
        setContentView(R.layout.query_location);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.query_location_searchview_in_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setInputType(InputType.TYPE_CLASS_PHONE);
        mSearchView.setQueryHint(getString(R.string.query_location_hint));
        searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
                | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        setupSearchView();
        return super.onCreateOptionsMenu(menu);
    }

    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setOnQueryTextListener(this);
        int id = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) mSearchView.findViewById(id);
        textView.setTextColor(getResources().getColor(R.color.common_white));
        textView.setTextSize(getResources().getDimension(R.dimen.common_font_size_small));
        textView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean isAlwaysExpanded() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        QueryFragment fragment = new QueryFragment();
        fragment.show(getFragmentManager(), query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
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
                case FRAGMENT_INDEX_CALLLOG:
                    return new CallLogMarkFragment();
                case FRAGMENT_INDEX_CONTACT:
                    return new ContactMarkFragment();
                case FRAGMENT_INDEX_SMS:
                    return new SmsMarkFragment();
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
