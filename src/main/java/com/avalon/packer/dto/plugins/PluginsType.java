package com.avalon.packer.dto.plugins;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class PluginsType {

    public PluginsType () {
    }

    public static List<typeItem> GetPluginsTypes () {
        List<typeItem> list = new ArrayList<>();
        list.add(new typeItem(1, "广告检测"));
        list.add(new typeItem(2, "广告变现"));
        list.add(new typeItem(3, "推送"));
        list.add(new typeItem(4, "统计"));
        list.add(new typeItem(5, "分享"));
        list.add(new typeItem(6, "QuickSDK服务"));
        list.add(new typeItem(7, "其他"));
        return list;
    }
}

@Data
class typeItem {
    private String type;
    private String typeName;

    public typeItem () {}

    public typeItem (Integer type, String typeName) {
        this.type = type.toString();
        this.typeName = typeName;
    }
}