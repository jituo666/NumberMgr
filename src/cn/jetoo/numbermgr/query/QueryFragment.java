package cn.jetoo.numbermgr.query;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cn.jetoo.numbermgr.R;
import cn.jetoo.numbermgr.query.codec.LocationCodec;
import cn.jetoo.numbermgr.query.codec.NaiveLocationCodec;
import cn.jetoo.numbermgr.utils.FeatureConfig;
import cn.jetoo.numbermgr.utils.LogHelper;
import cn.jetoo.numbermgr.utils.Utils;

public class QueryFragment extends DialogFragment {

    private static final String TAG = "QueryFragment";
    private static final boolean DEBUG = FeatureConfig.DEBUG_LOG;

    private static final int STARTWITH_1_MAX_LENGTH = 11;
    private static final int STARTWITH_9_MAX_LENGTH = 8;
    private ListView mQueryResult;
    private Button mOK;
    private TextView mQueryEmptyResult;
    private LocationCodec mLocationCodec;
    private List<QueryResultData> mList;
    private ResultAdapter mAdapter;

    private class ResultAdapter extends BaseAdapter {

        @Override
        public int getCount() {

            return mList.size();
        }

        @Override
        public QueryResultData getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ResultViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.query_location_result_list_item, null);
                viewHolder = new ResultViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.tv_query_result_title);
                viewHolder.content = (TextView) convertView.findViewById(R.id.tv_query_result_content);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ResultViewHolder) convertView.getTag();
            }
            viewHolder.title.setText(mList.get(position).key);
            viewHolder.content.setText(mList.get(position).content);
            return convertView;
        }

        protected class ResultViewHolder {
            TextView title;
            TextView content;
        }

    }

    public class QueryResultData {
        public String key = "";
        public String content = "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.query_location_result, container, false);
        mQueryResult = (ListView) rootView.findViewById(R.id.lv_query_result);
        mOK = (Button) rootView.findViewById(R.id.bt_ok);
        mOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mQueryEmptyResult = (TextView) rootView.findViewById(R.id.tv_query_result_nothing);
        mList = new ArrayList<QueryResultData>();
        mAdapter = new ResultAdapter();
        mQueryResult.setAdapter(mAdapter);
        mLocationCodec = NaiveLocationCodec.getInstance();
        query(getTag().toString());
        getDialog().setTitle(R.string.query_location);
        return rootView;
    }

    private void query(String query) {
        if (!Utils.isPhoneNumber(query)) {
            if (query.length() > 0) {
                Toast.makeText(getActivity(), "wong format", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        if (query.length() < 3) {
            mQueryResult.setVisibility(View.GONE);
            return;
        }
        String result = queryLocation(query);
        if (result != null && result.length() > 0)
            formatResult(result);
    }

    private void formatResult(String result) {
        final String[] array = result.split("[|]");
        mList.clear();
        if (array == null || array.length == 0) {
            mQueryResult.setVisibility(View.GONE);
            mQueryEmptyResult.setVisibility(View.VISIBLE);
            mQueryEmptyResult.setText("没有查讯结果");
        } else if (array.length == 3) {
            QueryResultData data = new QueryResultData();
            data.key = "省份";
            data.content = array[0];
            mList.add(data);
            data = new QueryResultData();
            data.key = "城市";
            data.content = array[1];
            mList.add(data);
            data = new QueryResultData();
            data.key = "运营商";
            data.content = array[2];
            mList.add(data);

            mAdapter.notifyDataSetChanged();
            mQueryResult.setVisibility(View.VISIBLE);
        } else if (array.length == 1) {
            QueryResultData data = new QueryResultData();
            data.key = "查询结果";
            data.content = array[0];
            mList.add(data);

            mAdapter.notifyDataSetChanged();
            mQueryResult.setVisibility(View.VISIBLE);
        }
    }

    private String queryLocation(String phoneNumber) {
        String result = "";
        if (mLocationCodec == null) {
            return result;
        }
        try {
            result = mLocationCodec.getLocationWithSplitor(phoneNumber);
            if (result == null || result.length() == 0) {
                if (!phoneNumber.startsWith("1") && !phoneNumber.startsWith("9")
                        && !phoneNumber.startsWith("+") && !phoneNumber.startsWith("-")
                        && !phoneNumber.startsWith("(") && !phoneNumber.startsWith(")")
                        && !phoneNumber.startsWith("0")) {
                    result = getString(R.string.local_phone);
                } else if ((phoneNumber.startsWith("1") && phoneNumber.length() >= STARTWITH_1_MAX_LENGTH)
                        || (phoneNumber.startsWith("9") && phoneNumber.length() >= STARTWITH_9_MAX_LENGTH)) {
                    result = getString(R.string.no_phone_location_info);
                }
            }
        } catch (Exception e) {
            LogHelper.e(TAG, e.getMessage());
        }
        return result;
    }
}
