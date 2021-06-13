package com.leyou.service;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;


@Service
public class UploadService {

    private static final List<String> CONTENT_TYPES = Arrays.asList("image/jpeg","image/gif");
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);

    public String upload(MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        //校验文件类型
        String contentType = file.getContentType();

        if(!CONTENT_TYPES.contains(contentType)){
            //文件类型不合法,直接返回null
            LOGGER.info("文件类型不合法:{}",originalFilename);
            return null;
        }
        try{
            //检验文件内容
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if(bufferedImage==null){
                LOGGER.info("文件内容不合法:{}",originalFilename);
            }
            //保存到服务器
            file.transferTo(new File("C:\\leyou\\images\\"+originalFilename));
            return "http://image.leyou.com/"+originalFilename;
        }catch (Exception e){
            LOGGER.info("服务器内部错误：{}", originalFilename);
            e.printStackTrace();
        }
        return null;
    }
}
