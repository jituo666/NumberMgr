package cn.jetoo.numbermgr.numbers;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.jetoo.numbermgr.R;
import cn.jetoo.numbermgr.numbers.bean.NumberDetail;

public class NumberSetDetailAdapter extends BaseAdapter {

    private List<NumberDetail> mList;
    private Context mContext;

    public NumberSetDetailAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<NumberDetail>();
    }

    public void setNumberDetailList(List<NumberDetail> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public NumberDetail getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final NumberSetViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.number_set_list_detail_item, null);
            viewHolder = new NumberSetViewHolder();
            viewHolder.tvPhoneName = (TextView) convertView.findViewById(R.id.tv_phone_name);
            viewHolder.tvPhoneNumber = (TextView) convertView.findViewById(R.id.tv_phone_number);
            viewHolder.ibCall = (ImageView) convertView.findViewById(R.id.ib_item_call);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (NumberSetViewHolder) convertView.getTag();
        }
        viewHolder.tvPhoneName.setText(mList.get(position).phoneName);
        viewHolder.tvPhoneNumber.setText(mList.get(position).phoneNumber);
        return convertView;
    }

    public class NumberSetViewHolder {
        TextView tvPhoneName;
        TextView tvPhoneNumber;
        ImageView ibCall;

    }
}
