package com.zx.landdisaster.base.tool;

import android.util.Log;

import com.zx.landdisaster.base.other.UserManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin123 on 2019/5/9.
 */

public class ActionLog {
    //    uploadLog(306, 1, "查看隐患点图层")
    private static final String[] id_301 = {"登录成功", "注销", "修改密码", "检查更新", "清除缓存", "服务热线", "意见反馈", "消息历史", "工作职责", "注册"};
    private static final String[] id_302 = {"日报", "日报记录", "巡查报告", "巡查报告记录", "灾险情上报", "灾险情暂存", "灾险情上报记录", "提交附件"};
    private static final String[] id_303 = {"日报审核列表", "日报查看", "日报审核", "灾险情审核列表", "灾险情查看", "灾险情审核", "审核日志查看"};
    private static final String[] id_304 = {"新版首页", "旧版首页", "地图首页"};
    private static final String[] id_305 = {"风险预警图", "实况降雨图", "雨量站列表", "气象预报图", "短临预警"};
    private static final String[] id_306 = {"隐患点图层", "隐患点列表", "隐患点详情", "灾险情图层", "灾险情列表", "灾险情详情", "三查监管",
            "值班调度", "专家支撑", "专家日报"};
    public static final String[] id_307 = {"全部信息列表", "预警消息列表", "日报专报列表", "核查报告列表", "上报率发布列表", "会商通知列表",
            "专家调度列表", "政策文件列表", "查看文件"};
    private static final String[] id_308 = {"搜索", "查看人员详情", "查看隐患点详情", "查看灾险情详情"};


    //id：模块id，
    public static Map<String, String> getActionLog(int id, int type, String action) {
        Map<String, String> map = new HashMap<>();
        map.put("model", id + "");
        map.put("type", type + "");
        switch (id) {
            case 301:
                map.put("modelName", "账户");
                map.put("typeName", id_301[type - 1]);
                break;
            case 302:
                map.put("modelName", "上报");
                map.put("typeName", id_302[type - 1]);
                break;
            case 303:
                map.put("modelName", "审核");
                map.put("typeName", id_303[type - 1]);
                break;
            case 304:
                map.put("modelName", "首页");
                map.put("typeName", id_304[type - 1]);
                break;
            case 305:
                map.put("modelName", "预警预报");
                map.put("typeName", id_305[type - 1]);
                break;
            case 306:
                map.put("modelName", "日常管理");
                map.put("typeName", id_306[type - 1]);
                break;
            case 307:
                map.put("modelName", "信息发布");
                map.put("typeName", id_307[type]);
                break;
            case 308:
                map.put("modelName", "综合搜索");
                map.put("typeName", id_308[type - 1]);
                break;
        }
//        try {
//            map.put("action", "APP端：用户：" + UserManager.INSTANCE.getUserName() + action);
//        } catch (Exception e) {
            map.put("action", "APP端：" + action);
//        }

        Log.e("logAction", "操作日志===>>>" + map.get("action"));
        return map;
    }
}
