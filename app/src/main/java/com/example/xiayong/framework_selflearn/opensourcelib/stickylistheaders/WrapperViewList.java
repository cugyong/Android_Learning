package com.example.xiayong.framework_selflearn.opensourcelib.stickylistheaders;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

class WrapperViewList extends ListView {

	interface LifeCycleListener {
		void onDispatchDrawOccurred(Canvas canvas);
	}

	private LifeCycleListener mLifeCycleListener;
	private List<View> mFooterViews;
	private int mTopClippingLength;
	private Rect mSelectorRect = new Rect();// for if reflection fails
	private Field mSelectorPositionField;
	private boolean mClippingToPadding = true;
    private boolean mBlockLayoutChildren = false;

	public WrapperViewList(Context context) {
		super(context);

		// Use reflection to be able to change the size/position of the list
		// selector so it does not come under/over the header
		try {
			Field selectorRectField = AbsListView.class.getDeclaredField("mSelectorRect");
			selectorRectField.setAccessible(true);
			mSelectorRect = (Rect) selectorRectField.get(this);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				mSelectorPositionField = AbsListView.class.getDeclaredField("mSelectorPosition");
				mSelectorPositionField.setAccessible(true);
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean performItemClick(View view, int position, long id) {
		if (view instanceof WrapperView) {
			view = ((WrapperView) view).mItem;
		}
		return super.performItemClick(view, position, id);
	}

	private void positionSelectorRect() {
		if (!mSelectorRect.isEmpty()) {
			int selectorPosition = getSelectorPosition();
			if (selectorPosition >= 0) {
				int firstVisibleItem = getFixedFirstVisibleItem();
				View v = getChildAt(selectorPosition - firstVisibleItem);
				if (v instanceof WrapperView) {
					WrapperView wrapper = ((WrapperView) v);
					mSelectorRect.top = wrapper.getTop() + wrapper.mItemTop;
				}
			}
		}
	}

	/**
	 * getChildCount()返回的是当前页面显示的View数目
	 * @return
     */
	private int getSelectorPosition() {
		if (mSelectorPositionField == null) { // not all supported andorid
			// version have this variable
			for (int i = 0; i < getChildCount(); i++) {
				if (getChildAt(i).getBottom() == mSelectorRect.bottom) {
					return i + getFixedFirstVisibleItem();
				}
			}
		} else {
			try {
				return mSelectorPositionField.getInt(this);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		positionSelectorRect();
		if (mTopClippingLength != 0) {
			canvas.save();
			Rect clipping = canvas.getClipBounds();
			clipping.top = mTopClippingLength;
			canvas.clipRect(clipping);
			super.dispatchDraw(canvas);
			canvas.restore();
		} else {
			super.dispatchDraw(canvas);
		}
		mLifeCycleListener.onDispatchDrawOccurred(canvas);
	}

	void setLifeCycleListener(LifeCycleListener lifeCycleListener) {
		mLifeCycleListener = lifeCycleListener;
	}

	@Override
	public void addFooterView(View v) {
		super.addFooterView(v);
		addInternalFooterView(v);
	}

	@Override
	public void addFooterView(View v, Object data, boolean isSelectable) {
		super.addFooterView(v, data, isSelectable);
		addInternalFooterView(v);
	}

	private void addInternalFooterView(View v) {
		if (mFooterViews == null) {
			mFooterViews = new ArrayList<View>();
		}
		mFooterViews.add(v);
	}

	@Override
	public boolean removeFooterView(View v) {
		if (super.removeFooterView(v)) {
			mFooterViews.remove(v);
			return true;
		}
		return false;
	}

	boolean containsFooterView(View v) {
		if (mFooterViews == null) {
			return false;
		}
		return mFooterViews.contains(v);
	}

	void setTopClippingLength(int topClipping) {
		mTopClippingLength = topClipping;
	}

	int getFixedFirstVisibleItem() {
		int firstVisibleItem = getFirstVisiblePosition();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			return firstVisibleItem;
		}

		// first getFirstVisiblePosition() reports items
		// outside the view sometimes on old versions of android
		for (int i = 0; i < getChildCount(); i++) {
			if (getChildAt(i).getBottom() >= 0) {
				firstVisibleItem += i;
				break;
			}
		}

		// work around to fix bug with firstVisibleItem being to high
		// because list view does not take clipToPadding=false into account
		// on old versions of android
		if (!mClippingToPadding && getPaddingTop() > 0 && firstVisibleItem > 0) {
			if (getChildAt(0).getTop() > 0) {
				firstVisibleItem -= 1;
			}
		}

		return firstVisibleItem;
	}

	/**
	 * clipToPadding就是说控件的绘制区域是否在padding里面的，默认该值为true,
	 设置为false那么子控件就能画到padding的区域了
	 */
	@Override
	public void setClipToPadding(boolean clipToPadding) {
		mClippingToPadding = clipToPadding;
		super.setClipToPadding(clipToPadding);
	}

    public void setBlockLayoutChildren(boolean block) {
        mBlockLayoutChildren = block;
    }

	/**
	 * 1、保存当前状态，包括焦点，选中项，仍显示的Item等
	 2、移除所有Item
	 3、根据情况重新填充Item
	 4、其他处理
	 */
    @Override
    protected void layoutChildren() {
        if (!mBlockLayoutChildren) {
            super.layoutChildren();
        }
    }
}
