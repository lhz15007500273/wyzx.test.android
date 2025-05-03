package com.example.wyzx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.wyzx.R;
import com.example.wyzx.datebase.DBHelper;
import com.example.wyzx.models.CommodityTypeModel;

import java.util.List;

/**
 * 要显示的主菜单的adapter
 */
public class MenuDialogAdapter extends BaseAdapter {
    private Context mContext;
    private List<CommodityTypeModel> menuDatas; // 改为使用CommodityTypeModel
    private int selectedPos = -1;
    private DBHelper dbHelper;

    public MenuDialogAdapter(Context mContext) {
        this.mContext = mContext;
        this.dbHelper = new DBHelper(mContext);
        this.menuDatas = dbHelper.getAllCommodityTypes(); // 使用正确的查询方法
    }

    //选中的position,及时更新数据
    public void setSelectedPos(int selectedPos) {
        this.selectedPos = selectedPos;
        notifyDataSetChanged();
    }

    //添加绑定的数据源
    public void setData(List<CommodityTypeModel> data) {
        this.menuDatas = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return menuDatas == null ? 0 : menuDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return menuDatas == null ? null : menuDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.main_menu_item, null);
        }

        TextView nameText = convertView.findViewById(R.id.menu_item_textview);

        //修改item高度
        ViewGroup.LayoutParams lp = nameText.getLayoutParams();
        lp.height = parent.getHeight() / 10;

        //获取选中的item的标题
        CommodityTypeModel menuData = menuDatas.get(position);
        nameText.setText(menuData.getName());

        convertView.setSelected(selectedPos == position);
        nameText.setSelected(selectedPos == position);

        //选中后的标题字体及RadioButton颜色
        nameText.setTextColor(selectedPos == position ? 0xFF387ef5 : 0xFF222222);

        return convertView;
    }
}