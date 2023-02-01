package com.avalon.packer.constant;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IamLimitConstants {
    public static Map<String, LimitItem> limitList = new HashMap<>();

    public IamLimitConstants() {
    }

    public static Map<String, LimitItem> getLimits() {
        limitList.put("/packer/admin/app", new LimitItem("/packer/admin/app", "GET", "appLimit"));
        return limitList;
    }

    @Data
    public static class LimitItem {
        private final String route;
        private final String method;
        private final String limitName;

        public LimitItem (String route, String method, String limitName) {
            this.route = route;
            this.method = method;
            this.limitName = limitName;
        }
    }
}
