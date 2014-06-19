package cn.jetoo.numbermgr.numbers;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cn.jetoo.numbermgr.R;
import cn.jetoo.numbermgr.numbers.bean.NumberSort;

public class NumberSetAdapter extends BaseAdapter {

    private List<NumberSort> mList;
    private Context mContext;

    public NumberSetAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<NumberSort>();
    }

    public NumberSetAdapter(Context context, List<NumberSort> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public NumberSort getItem(int position) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.number_set_list_item, null);
            viewHolder = new NumberSetViewHolder();
            viewHolder.tvSetName = (TextView) convertView.findViewById(R.id.tv_number_set);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (NumberSetViewHolder) convertView.getTag();
        }
        viewHolder.tvSetName.setText(mList.get(position).sortName);
        if (mList.get(position).catagory == NumberSort.CATAGORY_ADD) {
            viewHolder.tvSetName.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.number_set_add, 0, 0);
        } else {
            viewHolder.tvSetName.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_launcher, 0, 0);
        }
        return convertView;
    }

    public class NumberSetViewHolder {
        TextView tvSetName;
    }
}
