package cn.jetoo.numbermgr.query;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import cn.jetoo.numbermgr.R;
import cn.jetoo.numbermgr.content.PickContent;
import cn.jetoo.numbermgr.intercept.pick.ContactPickItem;
import cn.jetoo.numbermgr.intercept.pick.PickContactAdapter;
import cn.jetoo.numbermgr.utils.FeatureConfig;
import cn.jetoo.numbermgr.utils.LogHelper;

public class ContactMarkFragment extends Fragment {

    private static final String TAG = "ContactFragment";
    private static final boolean DEBUG = FeatureConfig.DEBUG_LOG;

    private ListView mListView;
    private PickContactAdapter mAdapter;
    private List<ContactPickItem> mPickList;

    private class LoadDataTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            FragmentActivity activity = getActivity();
            if (activity != null)
                mPickList = PickContent.queryContactList(activity);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mPickList != null && mPickList.size() > 0) {
                mAdapter.setListItem(mPickList);
            }
            if (DEBUG) {
                LogHelper.d(TAG, "total ContactFragment log count:" + mAdapter.getCount());
            }
            super.onPostExecute(result);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.query_location_mark_list, container, false);
        mPickList = new ArrayList<ContactPickItem>();
        mAdapter = new PickContactAdapter(getActivity(), false);
        mListView = (ListView) rootView.findViewById(R.id.lv_content_list);
        mListView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        new LoadDataTask().execute();
        super.onActivityCreated(savedInstanceState);
    }

}
