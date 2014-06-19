package cn.jetoo.numbermgr.intercept;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cn.jetoo.numbermgr.R;
import cn.jetoo.numbermgr.intercept.bean.InterceptSms;

public class InterceptSmsAdapter extends BaseAdapter {

    private List<InterceptSms> mList;
    private Context mContext;

    public InterceptSmsAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<InterceptSms>();
    }

    public void setItems(List<InterceptSms> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public InterceptSms getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final InterceptSmsViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.intercept_list_item, null);
            viewHolder = new InterceptSmsViewHolder();
            viewHolder.tvPhoneNumber = (TextView) convertView.findViewById(R.id.tv_phone_number);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_intercept_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (InterceptSmsViewHolder) convertView.getTag();
        }
        viewHolder.tvPhoneNumber.setText(mList.get(position).phoneNumber);
        viewHolder.tvContent.setText(mList.get(position).smsContent);
        return convertView;
    }

    public class InterceptSmsViewHolder {
        TextView tvPhoneNumber;
        TextView tvContent;
    }
}
