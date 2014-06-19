package cn.jetoo.numbermgr.numbers;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import cn.jetoo.numbermgr.R;
import cn.jetoo.numbermgr.numbers.bean.NumberDetail;
import cn.jetoo.numbermgr.numbers.data.NumberDataMgr;
import cn.jetoo.numbermgr.utils.FeatureConfig;

public class NumberSetDetailActivity extends Activity implements OnItemClickListener {

    private static final String TAG = "NumberSetDetailActivity";
    private static final boolean DEBUG = FeatureConfig.DEBUG_LOG;

    private ListView mListView;
    private List<NumberDetail> mNumberList;
    private NumberSetDetailAdapter mAdapter;

    private class LoadNumbersTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            mNumberList = NumberDataMgr.getInstance(NumberSetDetailActivity.this).querySet(
                    NumberSetDetailActivity.this, params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mAdapter.setNumberDetailList(mNumberList);
            super.onPostExecute(result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.number_set_list_detail);
        mNumberList = new ArrayList<NumberDetail>();
        mListView = (ListView) this.findViewById(R.id.lv_number_set_list_detail);
        mAdapter = new NumberSetDetailAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        String sortName = getIntent().getStringExtra("sort_name");
        new LoadNumbersTask().execute(sortName);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        String phoneNumber = mNumberList.get(position).phoneNumber.trim();
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

}
