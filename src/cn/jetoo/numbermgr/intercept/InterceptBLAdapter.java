package cn.jetoo.numbermgr.intercept;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.jetoo.numbermgr.R;
import cn.jetoo.numbermgr.intercept.bean.BlackListItem;
import cn.jetoo.numbermgr.intercept.data.BlackListDao;

public class InterceptBLAdapter extends BaseAdapter {

    private List<BlackListItem> mList;
    private Context mContext;

    public InterceptBLAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<BlackListItem>();
    }

    public void setItems(List<BlackListItem> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void addItem(BlackListItem item) {
        mList.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public BlackListItem getItem(int position) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.intercept_list_item, null);
            viewHolder = new InterceptBLViewHolder();
            viewHolder.tvPhoneNumber = (TextView) convertView.findViewById(R.id.tv_phone_number);
            viewHolder.tvLocation = (TextView) convertView.findViewById(R.id.tv_intercept_content);
            viewHolder.mOperate = (ImageView) convertView.findViewById(R.id.bt_item_operate);
            viewHolder.mOperate.setVisibility(View.VISIBLE);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (InterceptBLViewHolder) convertView.getTag();
        }
        viewHolder.tvPhoneNumber.setText(mList.get(position).phoneNumber);
        viewHolder.tvLocation.setText(mList.get(position).locaton);
        final int index = position;
        viewHolder.mOperate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BlackListDao.deleteBlackListItem(mContext, mList.get(index).phoneNumber);
                mList.remove(index);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    public class InterceptBLViewHolder {
        TextView tvPhoneNumber;
        TextView tvLocation;
        ImageView mOperate;
    }
}
