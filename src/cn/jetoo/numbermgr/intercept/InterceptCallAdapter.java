package cn.jetoo.numbermgr.intercept;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cn.jetoo.numbermgr.R;
import cn.jetoo.numbermgr.intercept.bean.InterceptCall;

public class InterceptCallAdapter extends BaseAdapter {

    private List<InterceptCall> mList;
    private Context mContext;
    private SimpleDateFormat mDateFormat;

    public InterceptCallAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<InterceptCall>();
        mDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
    }

    public void setItems(List<InterceptCall> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public InterceptCall getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final InterceptCallViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.intercept_list_item, null);
            viewHolder = new InterceptCallViewHolder();
            viewHolder.tvPhoneNumber = (TextView) convertView.findViewById(R.id.tv_phone_number);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_intercept_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (InterceptCallViewHolder) convertView.getTag();
        }
        viewHolder.tvPhoneNumber.setText(mList.get(position).phoneNumber);
        viewHolder.tvContent.setText(mDateFormat.format(new Date(mList.get(position).callTime)));
        return convertView;
    }

    public class InterceptCallViewHolder {
        TextView tvPhoneNumber;
        TextView tvContent;
    }
}
