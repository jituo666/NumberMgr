package cn.jetoo.numbermgr.numbers;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import cn.jetoo.numbermgr.R;
import cn.jetoo.numbermgr.numbers.bean.NumberSort;
import cn.jetoo.numbermgr.numbers.data.NumberDataMgr;
import cn.jetoo.numbermgr.utils.FeatureConfig;
import cn.jetoo.numbermgr.utils.LogHelper;

public class NumberSetActivity extends Activity implements OnItemClickListener,
        SearchView.OnQueryTextListener {


    private static final String TAG = "NumberSetActivity";
    private static final boolean DEBUG = FeatureConfig.DEBUG_LOG;

    private static final int NUMBER_SET_MAX_SIZE = 106;
    private GridView mNumberSetGridView;
    private NumberSetAdapter mNumberSetAdapter;
    private List<NumberSort> mNumberSort = new ArrayList<NumberSort>();
    private SearchView mSearchView;

    private class LoadSetDataTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... params) {
            return NumberDataMgr.getInstance(NumberSetActivity.this).loadList(NumberSetActivity.this);
        }

        @Override
        protected void onPostExecute(List<String> result) {

            final int len = Math.min(NUMBER_SET_MAX_SIZE , result.size());
            for (int index = 0; index < len; index++) {
                NumberSort sort = new NumberSort();
                sort.sortName = result.get(index);
                sort.catagory = NumberSort.CATAGORY_NORMAL;
                sort.sortIconResId = 0;
                mNumberSort.add(sort);
            }
            NumberSort lastSet = new NumberSort();
            lastSet.sortName = "添加";
            lastSet.catagory = NumberSort.CATAGORY_ADD;
            mNumberSort.add(lastSet);
            if (mNumberSort.size() > 0)
                mNumberSetAdapter.notifyDataSetChanged();
            super.onPostExecute(result);
        }
    }

    private void initViews() {
        setContentView(R.layout.number_set_list);
        mNumberSetAdapter = new NumberSetAdapter(this, mNumberSort);
        mNumberSetGridView = (GridView) findViewById(R.id.gv_number_set_list);
        mNumberSetGridView.setAdapter(mNumberSetAdapter);
        mNumberSetGridView.setOnItemClickListener(this);
        setupSearchView();
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupSearchView() {
        mSearchView = (SearchView) findViewById(R.id.sv_phone_search_view);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) {
            LogHelper.e(TAG, "A:" + "onCreate");
        }
        initViews();
        new LoadSetDataTask().execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        // mNumberSet.get(position).setName;
        Intent it = new Intent();
        it.setClass(this, NumberSetDetailActivity.class);
        it.putExtra("sort_name", mNumberSort.get(position).sortName);
        startActivity(it);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

}
