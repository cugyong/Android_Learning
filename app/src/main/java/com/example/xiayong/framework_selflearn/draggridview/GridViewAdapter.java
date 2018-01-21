package com.example.xiayong.framework_selflearn.draggridview;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xiayong.framework_selflearn.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by cugyong on 2018/1/19.
 */

public class GridViewAdapter extends BaseAdapter{

    private Context mContext;
    private List<DataItem> mDatas;
    private HashMap<View, Integer> mDelViewToDataIndex = new HashMap<>();

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int index = mDelViewToDataIndex.get(view);
            int size = mDatas.size();
            DataItem dataItem = mDatas.remove(index);
            // 除了添加、删除按钮，其他子项目都被删除时，需要同时移除删除按钮
            if (mDatas.size() <= 2){
                mDatas.remove(mDatas.size() - 1);
            }
            Toast.makeText(mContext, "you have deleted: " + dataItem.getText(), Toast.LENGTH_SHORT).show();
            notifyDataSetChanged();
        }
    };

    public GridViewAdapter(Context context, List<DataItem> dataItems){
        mContext = context;
        mDatas = dataItems;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return mDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // 告诉GridView在某一位置（position）的Item View样式是什么
    @Override
    public int getItemViewType(int position) {
       return super.getItemViewType(position);
    }

    // 告诉GridView需要加载多少种类型的Item View
    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        HoldView holdView;
        if (view == null){
            view = View.inflate(mContext, R.layout.item_grid_view, null);
            holdView = new HoldView();
            holdView.imageView = (ImageView) view.findViewById(R.id.iv);
            holdView.textView = (TextView) view.findViewById(R.id.tv);
            holdView.delImageView = (ImageView) view.findViewById(R.id.del_iv);
            view.setTag(holdView);
        }else {
              holdView = (HoldView) view.getTag();
        }
        holdView.imageView.setBackgroundDrawable(mDatas.get(i).getRes());
        holdView.delImageView.setBackgroundDrawable(mDatas.get(i).getDel());
        holdView.delImageView.setOnClickListener(mOnClickListener);
        holdView.textView.setText(mDatas.get(i).getText());
        if (mDatas.get(i).isDelShow()){
            holdView.delImageView.setVisibility(View.VISIBLE);
        } else {
            holdView.delImageView.setVisibility(View.INVISIBLE);
        }
        mDelViewToDataIndex.put(holdView.delImageView, i);
        return view;
    }

    // isDel表示是否添加的是删除按钮
    public void addData(DataItem dataItem, boolean isDel){
        if (isDel){
            mDatas.add(dataItem);
        } else {
            int size = mDatas.size();
            if (size >= 2){
                mDatas.add(size - 2, dataItem);
            }else {
                mDatas.add(dataItem);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 替换源数据
     * @param src 被替换数据的位置
     * @param replace 替换数据的位置
     * @param isNotifyDataChange 是否通知数据改变
     */
    public void changeData(int src, int replace, boolean isNotifyDataChange){
        DataItem srcData = mDatas.get(src);
        DataItem replaceData = mDatas.get(replace);
        srcData.setDel(replaceData.getDel());
        srcData.setDelShow(replaceData.isDelShow());
        srcData.setText(replaceData.getText());
        srcData.setRes(replaceData.getRes());
        if (isNotifyDataChange){
            notifyDataSetChanged();
        }
    }

    /**
     *
     * @param src 被替换数据的位置
     * @param replaceData 替换数据
     * @param isNotifyDataChange 是否通知数据改变
     */
    public void changeData(int src, DataItem replaceData, boolean isNotifyDataChange){
        DataItem srcData = mDatas.get(src);
        srcData.setDel(replaceData.getDel());
        srcData.setDelShow(replaceData.isDelShow());
        srcData.setText(replaceData.getText());
        srcData.setRes(replaceData.getRes());
        if (isNotifyDataChange){
            notifyDataSetChanged();
        }
    }

    public void showOrHideDelView(){
        int size = mDatas.size();
        if (size > 2 && !mDatas.get(0).isDelShow()){ // 有子项目，同时右上删除按钮没显示
            for (int i=0; i<size - 2; i++){
                mDatas.get(i).setDelShow(true);
            }
            setDelButtonText(false);
            notifyDataSetChanged();
        } else if (size > 2 && mDatas.get(0).isDelShow()){ // 有子项目，同时右上删除按钮显示
            for (int i=0; i<size - 2; i++){
                mDatas.get(i).setDelShow(false);
            }
            setDelButtonText(true);
            notifyDataSetChanged();
        }
    }

    public void setDelButtonText(boolean isDel){
        int size = mDatas.size() - 1;
        if (isDel){
            mDatas.get(size).setText(mContext.getResources().
                    getString(R.string.delete));
        } else{
            mDatas.get(size).setText(mContext.getResources().
                    getString(R.string.cancel));
        }
    }

    private static class HoldView{
        public TextView textView;
        public ImageView imageView;
        public ImageView delImageView;
    }
}
