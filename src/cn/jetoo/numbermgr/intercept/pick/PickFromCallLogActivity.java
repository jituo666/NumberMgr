package cn.jetoo.numbermgr.intercept.pick;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import cn.jetoo.numbermgr.R;
import cn.jetoo.numbermgr.content.PickContent;
import cn.jetoo.numbermgr.intercept.bean.BlackListItem;
import cn.jetoo.numbermgr.intercept.data.BlackListDao;
import cn.jetoo.numbermgr.utils.FeatureConfig;
import cn.jetoo.numbermgr.utils.LogHelper;

public class PickFromCallLogActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "PickFromCallLogActivity";
    private static final boolean DEBUG = FeatureConfig.DEBUG_LOG;

    private ListView mListView;
    private ImageView mAdd;
    private PickCallLogAdapter mAdapter;

    private List<CallLogPickItem> mPickList;

    private class LoadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mPickList = PickContent.queryCallLogList(PickFromCallLogActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mPickList != null && mPickList.size() > 0) {
                mAdapter.setListItem(mPickList);
            }
            if (DEBUG) {
                LogHelper.d(TAG, "total call log count:" + mAdapter.getCount());
            }
            super.onPostExecute(result);
        }
    }

    private void initViews() {
        setContentView(R.layout.intercept_list);
        mPickList = new ArrayList<CallLogPickItem>();
        mAdapter = new PickCallLogAdapter(this, true);
        mListView = (ListView) findViewById(R.id.lv_content_list);
        mListView.setAdapter(mAdapter);
        mAdd = (ImageView) findViewById(R.id.bt_operate);
        mAdd.setImageResource(R.drawable.op_add_item);
        mAdd.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        new LoadDataTask().execute();
    }

    @Override
    public void onClick(View v) {
        if (v == mAdd) {
            List<BlackListItem> list = new ArrayList<BlackListItem>();
            for (CallLogPickItem item :mPickList) {
                if (item.isSelected) {
                    BlackListItem blt = new BlackListItem();
                    blt.phoneName = item.phoneName;
                    blt.phoneNumber = item.phoneNumber;
                    blt.locaton = item.loc;
                    list.add(blt);
                }
            }
            if (list.size() > 0) {
                BlackListDao.insertBlackList(this, list);
            }
            finish();
        }
    }
}
