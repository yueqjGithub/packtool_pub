package com.avalon.packer.utils;

import com.avalon.packer.dto.BasePage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @ClassName: PageUtils
 * @Author: wxb
 * @Description:
 */
public class PageUtils {
    /**
     * 构建分页参数
     * @param basePage
     * @param <T>
     * @return
     */
    public static  <T> Page<T> buildPage(BasePage basePage){
        Page<T> page = new Page<>(basePage.getPage(), basePage.getPageSize());
        return page;
    }

    public int computePageStartNum(BasePage basePage){
        int page = basePage.getPage();
        int pageSize = basePage.getPageSize();
        int start = page*pageSize;
        return start;
    }
}
