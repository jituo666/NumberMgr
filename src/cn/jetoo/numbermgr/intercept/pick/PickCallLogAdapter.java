package cn.jetoo.numbermgr.intercept.pick;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import cn.jetoo.numbermgr.R;
import cn.jetoo.numbermgr.intercept.pick.PickContactAdapter.InterceptBLViewHolder;
import cn.jetoo.numbermgr.query.codec.NaiveLocationCodec;
import cn.jetoo.numbermgr.telephony.TelephonyUtils;
import cn.jetoo.numbermgr.utils.FeatureConfig;
import cn.jetoo.numbermgr.utils.LogHelper;

public class PickCallLogAdapter extends BaseAdapter {

    private static final String TAG = "PickCallLogAdapter";
    private static final boolean DEBUG = FeatureConfig.DEBUG_LOG;

    private List<CallLogPickItem> mList;
    private Context mContext;
    private SimpleDateFormat mFormat;
    private boolean mIsUsedForPicking = false;
    private static final int CALL_TYPE_RES_IDS[] = new int[] {
            R.string.common_incoming_call,
            R.string.common_outgoing_call,
            R.string.common_missed_call,
    };

    public PickCallLogAdapter(Context context, boolean usedForPicking) {
        mIsUsedForPicking = usedForPicking;
        mList = new ArrayList<CallLogPickItem>();
        mContext = context;
        mFormat = new SimpleDateFormat("MM-dd HH:mm");
    }

    public void setListItem(List<CallLogPickItem> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void addItem(CallLogPickItem item) {
        mList.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CallLogPickItem getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final PickCallLogViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.intercept_pick_list_item, null);
            viewHolder = new PickCallLogViewHolder();
            viewHolder.tvPhoneNumber = (TextView) convertView.findViewById(R.id.tv_phone_number);
            viewHolder.tvLocation = (TextView) convertView.findViewById(R.id.tv_phone_location);
            viewHolder.tvType = (TextView) convertView.findViewById(R.id.tv_left_content);
            viewHolder.tvLastCalledTime = (TextView) convertView.findViewById(R.id.tv_right_content);
            viewHolder.cbSelected = (CheckBox) convertView.findViewById(R.id.cb_selected);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (PickCallLogViewHolder) convertView.getTag();
        }
        String phoneNumberText = TelephonyUtils.stripPrefix(mList.get(position).phoneNumber);
        if (mList.get(position).phoneName != null && mList.get(position).phoneName.length() > 0)
            phoneNumberText += " <" + mList.get(position).phoneName + ">";

        viewHolder.tvPhoneNumber.setText(phoneNumberText);
        viewHolder.tvType.setText(CALL_TYPE_RES_IDS[mList.get(position).type - 1]);
        viewHolder.tvLocation.setText(NaiveLocationCodec.getInstance().getLocation(mList.get(position).phoneNumber));
        viewHolder.tvLastCalledTime.setText(mFormat.format(new Date(mList.get(position).lastCallTime)));
        if (mIsUsedForPicking) {
            viewHolder.cbSelected.setChecked(mList.get(position).isSelected);
            final int index = position;
            viewHolder.cbSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.cbSelected.setChecked(viewHolder.cbSelected.isChecked());
                    mList.get(index).isSelected = viewHolder.cbSelected.isChecked();
                }
            });
        } else {
            viewHolder.cbSelected.setVisibility(View.GONE);
        }
        return convertView;
    }

    public class PickCallLogViewHolder {
        TextView tvPhoneNumber;
        TextView tvType;
        TextView tvLocation;
        TextView tvLastCalledTime;
        CheckBox cbSelected;
    }

}
