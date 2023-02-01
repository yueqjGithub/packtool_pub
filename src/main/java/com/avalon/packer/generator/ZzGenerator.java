package com.avalon.packer.generator;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码生成器的类
 */
public class ZzGenerator {
    public static void main(String[] args) {
        //拿到prop对象
        ZzGenerateProperties instance = ZzGenerateProperties.getInstance();
        // 创建一个代码生成器
        AutoGenerator gen = new AutoGenerator();
        //设置代码生成器的必要参数
        gen.setDataSource(new DataSourceConfig().setDbType(DbType.MYSQL).setDriverName("com.mysql.cj.jdbc.Driver")
                .setUrl(instance.getUrl()).setUsername(instance.getUsername()).setPassword(instance.getPassword()));

        /**
         * 全局配置
         */
        gen.setGlobalConfig(new GlobalConfig()
                // ----------------------------------------注意---------------------------
                .setOutputDir(instance.getBaseProjectPath() + "/src/main/java")// 输出目录
                .setFileOverride(true)// 是否覆盖文件
                .setActiveRecord(true)// 开启 activeRecord 模式
                .setEnableCache(false)// XML 二级缓存
                .setBaseResultMap(true)// XML ResultMap
                .setBaseColumnList(true)// XML columList
                .setOpen(true)// 生成后打开文件夹
                .setSwagger2(true).setAuthor(instance.getAuthorName())
                .setIdType(IdType.ASSIGN_UUID)
                // 自定义文件命名，注意 %s 会自动填充表实体属性！
                .setMapperName("%sMapper").setXmlName("%sMapper").setServiceName("%sService")
                .setServiceImplName("%sServiceImpl").setControllerName("%sController"));

        // 策略配置
        gen.setStrategy(new StrategyConfig().setNaming(NamingStrategy.underline_to_camel)// 表名生成策略
                .setInclude(instance.getTables()) // 需要生成的表
                .setTablePrefix("T_")//设置统一前缀
                .setRestControllerStyle(true).setColumnNaming(NamingStrategy.underline_to_camel).setEntityLombokModel(true)
                .setRestControllerStyle(true).setControllerMappingHyphenStyle(true));

        /**
         * 包配置
         */
        gen.setPackageInfo(new PackageConfig()
                // .setModuleName("demo")
                .setParent(instance.getBasePackage())// 自定义包路径
                // .setController("controller")// 这里是控制器包名，默认 web
                .setEntity("model").setMapper("mapper").setService("service").setServiceImpl("service.impl"));

        /**
         * 注入自定义配置
         */
        // 注入自定义配置，可以在 VM 中使用 cfg.abc 设置的值
        InjectionConfig abc = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<>();
                this.setMap(map);
            }
        };

        // 自定义文件输出位置（非必须）
        List<FileOutConfig> fileOutList = new ArrayList<>();
        fileOutList.add(new FileOutConfig("/templates/mapper.xml.ftl") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return instance.getBaseProjectPath() + "/src/main/resources/" + tableInfo.getEntityName()
                        + "Mapper.xml";
            }
        });
        abc.setFileOutConfigList(fileOutList);
        gen.setCfg(abc);

        /**
         * 指定模板引擎 默认是VelocityTemplateEngine ，需要引入相关引擎依赖
         */
        gen.setTemplateEngine(new FreemarkerTemplateEngine());

        /**
         * 模板配置
         */
        gen.setTemplate(
                // 关闭默认 xml 生成，调整生成 至 根目录
                new TemplateConfig().setXml(null));
        // 执行生成
        gen.execute();

    }

}
