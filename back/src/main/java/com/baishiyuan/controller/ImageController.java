package com.baishiyuan.controller;

import com.baishiyuan.domain.WebResult;
import com.baishiyuan.utils.StringConst;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.InputStream;
import java.util.UUID;

@RestController
@RequestMapping("/image")
public class ImageController {

    private static final Logger logger = Logger.getLogger(ImageController.class);

    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    private GridFsTemplate gridFsTemplate;

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public WebResult uploadFile(HttpServletRequest request) throws Exception {
        Part part = request.getPart("file");
        // 获得提交的文件名
        String fileName = part.getSubmittedFileName();
        // 获得文件输入流
        InputStream ins = part.getInputStream();
        // 获得文件类型
        String contentType = part.getContentType();
        // 将文件存储到mongodb中,mongodb 将会返回这个文件的具体信息

        String uuid = UUID.randomUUID().toString();

        DBObject metadata = new BasicDBObject();
        metadata.put("uuid", uuid);
        gridFsTemplate.store(ins, fileName, "image/png", metadata);
        return new WebResult(StringConst.ERRCODE_SUCCESS, "文件上传成功", "/image/" + uuid);
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
    public WebResult uploadFile(@PathVariable String uuid, HttpServletRequest request) throws Exception {
        gridFsTemplate.delete(new Query(Criteria.where("metadata.uuid").is(uuid)));
        return new WebResult(StringConst.ERRCODE_SUCCESS, "文件删除成功", uuid);
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    @ResponseBody
    public void getImage(@PathVariable String uuid, HttpServletResponse response) {
        try {
            GridFSFile gridfs = gridFsTemplate.findOne(new Query(Criteria.where("metadata.uuid").is(uuid)));
            GridFsResource gridFsResource = gridFsTemplate.getResource(gridfs);
            IOUtils.copy(gridFsResource.getInputStream(), response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
