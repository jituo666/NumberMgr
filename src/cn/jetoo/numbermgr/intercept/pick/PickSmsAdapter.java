package cn.jetoo.numbermgr.intercept.pick;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import cn.jetoo.numbermgr.R;
import cn.jetoo.numbermgr.query.codec.NaiveLocationCodec;

public class PickSmsAdapter extends BaseAdapter {
    private List<SmsPickItem> mList;
    private Context mContext;
    private SimpleDateFormat mFormat;
    private boolean mIsUsedForPicking = false;

    public PickSmsAdapter(Context context, boolean usedForPicking) {
        mIsUsedForPicking = usedForPicking;
        mList = new ArrayList<SmsPickItem>();
        mContext = context;
        mFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
    }

    public void setListItem(List<SmsPickItem> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void addItem(SmsPickItem item) {
        mList.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public SmsPickItem getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final InterceptBLViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.intercept_pick_list_item, null);
            viewHolder = new InterceptBLViewHolder();
            viewHolder.tvPhoneNumber = (TextView) convertView.findViewById(R.id.tv_phone_number);
            viewHolder.tvSmsContent = (TextView) convertView.findViewById(R.id.tv_left_content);
            viewHolder.tvLocation = (TextView) convertView.findViewById(R.id.tv_phone_location);
            viewHolder.tvSmsDate = (TextView) convertView.findViewById(R.id.tv_right_content);
            viewHolder.cbSelected = (CheckBox) convertView.findViewById(R.id.cb_selected);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (InterceptBLViewHolder) convertView.getTag();
        }
        viewHolder.tvPhoneNumber.setText(mList.get(position).phoneNumber);
        viewHolder.tvSmsContent.setText(mList.get(position).lastSms);
        viewHolder.tvLocation.setText(NaiveLocationCodec.getInstance().getLocation(mList.get(position).phoneNumber));
        viewHolder.tvSmsDate.setText(mFormat.format(new Date(mList.get(position).lastSmsTime)));
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
        }
        else {
            viewHolder.cbSelected.setVisibility(View.GONE);
        }
        return convertView;
    }

    public class InterceptBLViewHolder {
        TextView tvPhoneNumber;
        TextView tvSmsContent;
        TextView tvLocation;
        TextView tvSmsDate;
        CheckBox cbSelected;
    }
}
