package com.leyou.good.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static sun.management.snmp.jvminstr.JvmThreadInstanceEntryImpl.ThreadStateMap.Byte0.runnable;


@Service
public class GoodsHtmlService {
    private static final ExecutorService es = Executors.newFixedThreadPool(10);

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private GoodsService goodsService;
    public void createHtml(Long spuId){
        //初始化运行上下文
        Context context = new Context();
        //设置数据模型
        context.setVariables(this.goodsService.loadData(spuId));


        PrintWriter writer=null;
        try {
            //把静态文件生成到服务器本地
           // File file = new File("D:\\ProgramProject\\nginx-1.14.0\\html\\item\\" + spuId + ".html");
           // writer = new PrintWriter(file);
            templateEngine.process("item", context, new FileWriter(new File("D:\\ProgramProject\\nginx-1.14.0\\html\\item\\2.html")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.flush();
            if(writer==null){
                writer.close();
            }
        }
         //this.templateEngine.process("item", context, writer);

    }


}
