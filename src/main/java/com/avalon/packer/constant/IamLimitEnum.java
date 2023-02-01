package com.avalon.packer.constant;

import com.avalon.sdk.iam.bo.DataAuthBo;
import com.avalon.sdk.iam.util.DataAuthMapKeyUtils;

import java.util.HashMap;
import java.util.Map;

public enum IamLimitEnum {
    APP("/packer/admin/app", "GET", "appLimit", null);

    public final String path;
    public final String method;
    public final String limitName;
    public final IamLimitEnum dependence;

    IamLimitEnum(String path, String method, String limitName, IamLimitEnum depend) {
        this.method = method;
        this.limitName = limitName;
        this.path = path;
        this.dependence = depend;
    }

    private static Map<String, DataAuthBo> map = new HashMap<>();
    static {
        for (IamLimitEnum current : values()) {
            DataAuthBo target = new DataAuthBo();
            target.setLimitName(current.limitName);
            target.setMethod(current.method);
            target.setPath(current.path);
            map.put(DataAuthMapKeyUtils.generateKey(current.path ,current.method), target);
        }
    }
    public static Map<String, DataAuthBo> getAll () { return map; }
    /**
     * 获取指定名称的枚举
     * @param path 指定名称 + 请求方法  path+method
     * @return 对应枚举
     */
    public static DataAuthBo nameOf(String path) {
        return map.get(path);
    }
}
