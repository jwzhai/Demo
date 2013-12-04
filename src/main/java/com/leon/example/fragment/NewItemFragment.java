package com.leon.example.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.leon.example.R;

/**
 * Created by hualu on 13-11-18.
 */
public class NewItemFragment extends Fragment {
    private OnNewItemAddedListener onNewItemAddedListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onNewItemAddedListener = (OnNewItemAddedListener) activity;
            Log.d("NewItem", "onAttach is invoked!");
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnNewItemAddedListenerl");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_item_fragment, container, false);

        final EditText nameEditText = (EditText) view.findViewById(R.id.nameEditText);
        final EditText numberEditText = (EditText) view.findViewById(R.id.numberEditText);
        final Button btnAddItem = (Button) view.findViewById(R.id.btnAddItem);
        numberEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String numberStr = numberEditText.getText().toString();
                Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
                if (name.equals("")) {
                    nameEditText.startAnimation(shake);
                    return;
                } else if (numberStr.equals("")) {
                    numberEditText.startAnimation(shake);
                    return;
                }
                int number = Integer.valueOf(numberEditText.getText().toString());
                onNewItemAddedListener.onNewItemAdded(name, number);
                nameEditText.setText("");
                numberEditText.setText("");
            }
        });
        return view;
    }

    public interface OnNewItemAddedListener {

        /**
         * NewItemFragment添加新的数据到列表中时调用。
         *
         * @param name   列表项的name字段
         * @param number 列表项的number字段
         */
        public void onNewItemAdded(String name, int number);
    }
}
