package cn.jetoo.numbermgr.intercept;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import cn.jetoo.numbermgr.R;
import cn.jetoo.numbermgr.intercept.bean.BlackListItem;
import cn.jetoo.numbermgr.intercept.data.BlackListDao;
import cn.jetoo.numbermgr.intercept.pick.PickFromCallLogActivity;
import cn.jetoo.numbermgr.intercept.pick.PickFromContactActivity;
import cn.jetoo.numbermgr.intercept.pick.PickFromSmsActivity;

public class BlackListFragment extends Fragment implements View.OnClickListener {
    private static final int PICK_BLACK_LIST_ITEM_FROM_RECENT_CALLS = 0;
    private static final int PICK_BLACK_LIST_ITEM_FROM_CONTACT_LIST = 1;
    private static final int PICK_BLACK_LIST_ITEM_FROM_SMS_LIST = 2;
    private static final int PICK_BLACK_LIST_ITEM_MANNUALLY_INPUT = 3;
    //
    private static final int REQUEST_CODE_RECENT_CALLS = 10;
    private static final int REQUEST_CODE__CONTACT_LIST = 11;
    private static final int REQUEST_CODE__SMS_LIST = 12;

    private ListView mListView;
    private ImageView mAdd;

    private InterceptBLAdapter mAdapter;
    private List<BlackListItem> mBlackList;

    private class LoadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mBlackList = BlackListDao.queryBlackList(getActivity());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mBlackList != null && mBlackList.size() > 0) {
                mAdapter.setItems(mBlackList);
            }
            super.onPostExecute(result);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.intercept_list, container, false);

        mListView = (ListView) rootView.findViewById(R.id.lv_content_list);
        mAdapter = new InterceptBLAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mAdd = (ImageView) rootView.findViewById(R.id.bt_operate);
        mAdd.setImageResource(R.drawable.op_add_item);
        mAdd.setOnClickListener(this);
        new LoadDataTask().execute();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.intercept_add)
                .setItems(R.array.intercept_blacklist_add_entries, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        handlerPickEvent(which);
                    }
                })
                .create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_RECENT_CALLS: {
            }
            case REQUEST_CODE__CONTACT_LIST: {
            }
            case REQUEST_CODE__SMS_LIST: {
                new LoadDataTask().execute();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handlerPickEvent(int pickFrom) {
        switch (pickFrom) {
            case PICK_BLACK_LIST_ITEM_FROM_RECENT_CALLS: {
                Intent intent = new Intent();
                intent.setClass(getActivity(), PickFromCallLogActivity.class);
                startActivityForResult(intent, REQUEST_CODE_RECENT_CALLS);
                break;
            }
            case PICK_BLACK_LIST_ITEM_FROM_CONTACT_LIST: {
                Intent intent = new Intent();
                intent.setClass(getActivity(), PickFromContactActivity.class);
                startActivityForResult(intent, REQUEST_CODE__CONTACT_LIST);
                break;
            }
            case PICK_BLACK_LIST_ITEM_FROM_SMS_LIST: {
                Intent intent = new Intent();
                intent.setClass(getActivity(), PickFromSmsActivity.class);
                startActivityForResult(intent, REQUEST_CODE__SMS_LIST);
                break;
            }
            case PICK_BLACK_LIST_ITEM_MANNUALLY_INPUT: {
                LayoutInflater factory = LayoutInflater.from(getActivity());
                final View textEntryView = factory.inflate(R.layout.intercept_black_list_manully_input, null);
                final TextView tvNumber = (TextView) textEntryView.findViewById(R.id.phone_number_edit);
                new AlertDialog.Builder(getActivity())
                        .setIconAttribute(android.R.attr.alertDialogIcon)
                        .setTitle(R.string.intercept_add)
                        .setView(textEntryView)
                        .setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // add
                                tvNumber.getText().toString();
                            }
                        })
                        .setNegativeButton(R.string.common_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .create().show();
                break;
            }
        }
    }
}
