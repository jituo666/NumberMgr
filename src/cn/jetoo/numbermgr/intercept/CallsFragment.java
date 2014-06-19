package cn.jetoo.numbermgr.intercept;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import cn.jetoo.numbermgr.R;
import cn.jetoo.numbermgr.content.PickContent;
import cn.jetoo.numbermgr.intercept.bean.InterceptCall;
import cn.jetoo.numbermgr.intercept.data.BlackListDao;
import cn.jetoo.numbermgr.intercept.data.IntercepCallDao;
import cn.jetoo.numbermgr.intercept.pick.PickFromCallLogActivity;
import cn.jetoo.numbermgr.utils.FeatureConfig;
import cn.jetoo.numbermgr.utils.LogHelper;

public class CallsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "CallsFragment";
    private static final boolean DEBUG = FeatureConfig.DEBUG_LOG;

    private ListView mListView;
    private ImageView mClearBtn;

    private List<InterceptCall> mCallList;
    private InterceptCallAdapter mAdapter;

    private class LoadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mCallList = IntercepCallDao.queryIntercepCallList(getActivity());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mCallList != null && mCallList.size() > 0) {
                mAdapter.setItems(mCallList);
            }
            if (DEBUG) {
                LogHelper.d(TAG, "total call list count:" + mAdapter.getCount());
            }
            super.onPostExecute(result);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.intercept_list, container, false);
        mListView = (ListView) rootView.findViewById(R.id.lv_content_list);
        mCallList = new ArrayList<InterceptCall>();

        mAdapter = new InterceptCallAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mClearBtn = (ImageView) rootView.findViewById(R.id.bt_operate);
        mClearBtn.setImageResource(R.drawable.op_clear_all);
        mClearBtn.setOnClickListener(this);
        new LoadDataTask().execute();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (mClearBtn == v) {
            mCallList.clear();
            mAdapter.notifyDataSetChanged();
            IntercepCallDao.deleteAllIntercepCalls(getActivity());
        }
    }
}
