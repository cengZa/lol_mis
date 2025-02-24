package com.genius.lol.controller;

import com.genius.lol.util.AliOssUtil;
import com.genius.lol.util.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
public class FileUploadController {

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws Exception {
        // 存储到本地磁盘
        String originalFilename = file.getOriginalFilename();
        // 保证文件名唯一， 防止文件覆盖
        String filename = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
//        file.transferTo(new File("D:\\.projects\\JavaProjects\\lol\\src\\main\\resources\\files\\" + filename));
        String url = AliOssUtil.uploadFile(filename, file.getInputStream());
        return Result.success("文件上传成功",url);
    }
}
