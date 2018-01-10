package com.example.xiayong.framework_selflearn.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.xiayong.framework_selflearn.R;

/**
 * Created by cugyong on 2018/1/9.
 */

public class XYEditView extends RelativeLayout{

    private static final int FONT = R.dimen.font_size_10;
    private Context mContext;
    private EditText mEditText;
    private ImageView mDelBt;
    private InputMethodManager mImm;
    private TextWatcher mTextWatcher;
    private XYTextWatcher mXYTextWatcher;

    public interface XYTextWatcher{
        public void onTextChanged(XYEditView view, CharSequence s,
                                  int start, int before, int count);
        public void beforeTextChanged(XYEditView view, CharSequence s,
                                  int start, int count, int after);
        public void afterTextChanged(XYEditView view, Editable s);
    }

    class BaseTextWatcher implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (mXYTextWatcher != null){
                mXYTextWatcher.beforeTextChanged(XYEditView.this, charSequence, i, i1, i2);
            }
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (mXYTextWatcher != null){
                mXYTextWatcher.onTextChanged(XYEditView.this, charSequence, i, i1, i2);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (mXYTextWatcher != null){
                mXYTextWatcher.afterTextChanged(XYEditView.this,editable);
            }
            if (editable.length() > 0){
                mDelBt.setVisibility(View.VISIBLE);
            } else {
                mDelBt.setVisibility(View.INVISIBLE);
            }
        }
    }

    public XYEditView(Context context) {
        this(context, null);
    }

    public XYEditView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XYEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        _initView();
    }

    private void _initView(){
        mTextWatcher = new BaseTextWatcher();
        setGravity(Gravity.CENTER_VERTICAL);
        int height = mContext.getResources().getDimensionPixelSize(R.dimen.padding_30);

        mDelBt = new ImageView(mContext);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(height, height);
        layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams1.addRule(RelativeLayout.CENTER_VERTICAL);
        mDelBt.setId(mDelBt.hashCode());
        mDelBt.setLayoutParams(layoutParams1);
        mDelBt.setPadding(0,0,mContext.getResources().getDimensionPixelSize(R.dimen.padding_10),0);
        mDelBt.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.delete));
        mDelBt.setVisibility(View.INVISIBLE);
        mDelBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditText.getEditableText().clear();
            }
        });
        addView(mDelBt);

        mEditText = (EditText) View.inflate(mContext, R.layout.item_edittext, null);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.LEFT_OF, mDelBt.getId());
        mEditText.setLayoutParams(layoutParams);
        mEditText.setHint(mContext.getResources().getString(R.string.city_search));
        mEditText.setHintTextColor(mContext.getResources().getColor(R.color.colorGray));
        mEditText.setTextSize(mContext.getResources().getDimensionPixelSize(FONT));
        mEditText.addTextChangedListener(mTextWatcher);
        addView(mEditText);

        mImm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public void setTextWatcher(XYTextWatcher textWatcher){
        mXYTextWatcher = textWatcher;
    }

    public void hideSoftKeyword(){
        mImm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
