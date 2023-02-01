package com.avalon.packer.component;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), metaObject);
        this.setFieldValByName("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update .. ");
        this.setFieldValByName("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),metaObject);
    }
}
