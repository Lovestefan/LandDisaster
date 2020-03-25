package com.zx.landdisaster.module.system.bean;

import com.zx.zxutils.entity.KeyValueEntity;

import java.util.ArrayList;
import java.util.List;

public class RegisterData {
    private final static String[] political = {"", "中共党员", "中共预备党员", "共青团员", "民革党员", "民盟盟员", "民建会员", "民进会员",
            "农工党党员", "致公党党员", "九三学社社员", "台盟盟员", "无党派人士", "群众"};

    private final static String[] nation = {"", "汉族", "满族", "蒙古族", "回族", "藏族", "维吾尔族", "苗族", "彝族", "壮族", "布依族",
            "侗族", "瑶族", "白族", "土家族", "哈尼族", "哈萨克族", "傣族", "黎族", "傈僳族", "佤族",
            "畲族", "高山族", "拉祜族", "水族", "东乡族", "纳西族", "景颇族", "柯尔克孜族", "土族", "达斡尔族",
            "仫佬族", "羌族", "布朗族", "撒拉族", "毛南族", "仡佬族", "锡伯族", "阿昌族", "普米族", "朝鲜族",
            "塔吉克族", "怒族", "乌孜别克族", "俄罗斯族", "鄂温克族", "德昂族", "保安族", "裕固族", "京族", "塔塔尔族",
            "独龙族", "鄂伦春族", "赫哲族", "门巴族", "珞巴族", "基诺族"};

    private final static String[] education = {"", "小学", "初级中学", "高级中学、中专、职校、中技", "专科（高职、高专、高技）", "本科",
            "硕士研究生", "博士研究生"};

    public static List<KeyValueEntity> getPolitical() {
        return getList(political);
    }

    public static List<KeyValueEntity> getNation() {
        return getList(nation);
    }

    public static List<KeyValueEntity> getEducation() {
        return getList(education);
    }

    private static List<KeyValueEntity> getList(String[] data) {
        List<KeyValueEntity> list = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            list.add(new KeyValueEntity(data[i], (i + 1) + ""));
        }
        return list;
    }
}
