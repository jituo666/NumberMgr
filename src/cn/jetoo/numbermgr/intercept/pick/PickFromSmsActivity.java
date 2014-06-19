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

public class PickFromSmsActivity extends Activity implements View.OnClickListener {
    private ListView mListView;
    private ImageView mAdd;

    private PickSmsAdapter mAdapter;
    List<SmsPickItem> mPickList;

    private class LoadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mPickList = PickContent.querySmsList(PickFromSmsActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mPickList != null && mPickList.size() > 0) {
                mAdapter.setListItem(mPickList);
            }
            super.onPostExecute(result);
        }
    }

    private void initViews() {
        setContentView(R.layout.intercept_list);
        mPickList = new ArrayList<SmsPickItem>();
        mAdapter = new PickSmsAdapter(this, true);
        mListView = (ListView) findViewById(R.id.lv_content_list);
        mListView.setAdapter(mAdapter);
        mAdd = (ImageView) findViewById(R.id.bt_operate);
        mAdd.setImageResource(R.drawable.op_add_item);
        mAdd.setOnClickListener(this);
        new LoadDataTask().execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    @Override
    public void onClick(View v) {
        List<BlackListItem> list = new ArrayList<BlackListItem>();
        for (SmsPickItem item :mPickList) {
            if (item.isSelected) {
                BlackListItem blt = new BlackListItem();
                blt.phoneName = item.phoneName;
                blt.phoneNumber = item.phoneNumber;
                list.add(blt);
            }
        }
        if (list.size() > 0) {
            BlackListDao.insertBlackList(this, list);
        }
        finish();
    }

}
