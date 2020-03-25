package com.zx.landdisaster.module.system.ui;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zx.landdisaster.R;
import com.zx.landdisaster.module.system.func.SelectMoreAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @name 自定义多选控件
 */
public class SelectMoreView implements View.OnClickListener {
    private TextView selectView;
    private TextView aSelectView_title, aSelectView_canle, aSelectView_ok, aSelectView_all;
    private ListView aSelectView_select;
    private Dialog mDialog;
    private Context context;
    private JSONArray data;
    private String title;
    private String key = "item";
    private String key2 = "id";
    private int have = 1;//判断选中的项是否已经存在，存在则直接返回
    private List<Boolean> selects = new ArrayList<>();
    private String fgf;//分隔符
    private SelectMoreAdapter adapter;

    /**
     * 构造方法
     *
     * @param context
     * @param data
     * @param selectView
     * @param title
     */
    public SelectMoreView(Context context, JSONArray data, TextView selectView, String title, String fgf) {
        if (data == null || data.length() == 0) {
            return;
        }
        this.context = context;
        this.data = data;
        this.title = title;
        this.selectView = selectView;
        this.fgf = fgf;
        init();
    }

    /**
     * 自定义key
     * 标题，分隔符，久有数据，key名称，keyID
     */
    public SelectMoreView(Context context, JSONArray data, TextView selectView, String title, String fgf, String name, String id) {
        if (data == null || data.length() == 0) {
            return;
        }
        this.context = context;
        this.data = data;
        this.title = title;
        this.selectView = selectView;
        this.fgf = fgf;
        this.key = name;
        this.key2 = id;
        init();
    }

    public int dip2Px(int dip) {
        // px/dip = density;
        float density = context.getResources().getDisplayMetrics().density;
        int px = (int) (dip * density + .5f);
        return px;
    }

    public void init() {
        mDialog = new Dialog(context, R.style.MyDialogStyleBottom);
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_select_more, null);
        aSelectView_title = (TextView) inflate.findViewById(R.id.aSelectView_title);
        aSelectView_canle = (TextView) inflate.findViewById(R.id.aSelectView_canle);
        aSelectView_ok = (TextView) inflate.findViewById(R.id.aSelectView_ok);
        aSelectView_all = (TextView) inflate.findViewById(R.id.aSelectView_all);
        aSelectView_select = (ListView) inflate.findViewById(R.id.listview);
        //动态调整高度
        if (data.length() > 7) {
            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dip2Px(300));
            aSelectView_select.setLayoutParams(ll);
        }
        selects = new ArrayList<>();
        String[] yixuan = null;
        if (!selectView.getText().toString().isEmpty()) {
            yixuan = selectView.getText().toString().split(fgf);
        }
        for (int i = 0; i < data.length(); i++) {
            JSONObject obj = data.optJSONObject(i);
            boolean add = false;
            if (yixuan != null)
                for (int j = 0; j < yixuan.length; j++) {
                    if (yixuan[j].equals(obj.optString(key))) {
                        add = true;
                        continue;
                    }
                }
            selects.add(add);
        }
        isAll();
        adapter = new SelectMoreAdapter(context, data, selects, key);
        aSelectView_select.setAdapter(adapter);
        aSelectView_select.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selects.set(position, !selects.get(position));
                adapter.setDatalist(data, selects);
                isAll();
            }
        });
        mDialog.setContentView(inflate);
        Window dialogWindow = mDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = ActionBar.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
        mDialog.show();

        aSelectView_title.setText("请选择" + title);
        aSelectView_canle.setOnClickListener(this);
        aSelectView_ok.setOnClickListener(this);
        aSelectView_all.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.aSelectView_ok:
                String name = "";
                String ids = "";
                for (int i = 0; i < selects.size(); i++) {
                    if (selects.get(i)) {//选中的项
                        name += data.optJSONObject(i).optString(key) + fgf;
                        ids += data.optJSONObject(i).optString(key2) + fgf;
                    }
                }
                if (!name.isEmpty()) {
                    name = name.substring(0, name.length() - 1);
                    ids = ids.substring(0, ids.length() - 1);
                }
                if (selectView != null) {
                    selectView.setText(name);
                }
                if (clickListener != null) {
                    clickListener.onClick(ids);
                }
                mDialog.dismiss();
                break;
            case R.id.aSelectView_all://全选
                boolean isAll = aSelectView_all.getText().toString().equals("全选");
                for (int i = 0; i < selects.size(); i++) {
                    selects.set(i, isAll);
                }
                aSelectView_all.setText(isAll ? "反选" : "全选");
                if (adapter != null) adapter.setDatalist(data, selects);
                break;
            case R.id.aSelectView_canle:
                mDialog.dismiss();
                break;
        }
    }

    private void isAll() {
        boolean isAll = true;
        for (int i = 0; i < selects.size(); i++) {
            if (!selects.get(i)) {
                isAll = false;
                aSelectView_all.setText("全选");
                return;
            }
        }
        aSelectView_all.setText("反选");
    }

    public OnClick clickListener;

    public void setClickOK(OnClick clickOK) {
        clickListener = clickOK;
    }

    public interface OnClick {
        void onClick(String ids);
    }
}
