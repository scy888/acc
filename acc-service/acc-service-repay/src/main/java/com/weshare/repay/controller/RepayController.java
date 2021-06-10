package com.weshare.repay.controller;

import com.weshare.repay.entity.PictureFile;
import com.weshare.repay.repo.PictureFileRepo;
import common.DownloadUtils;
import common.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.repay.controller
 * @date: 2021-04-26 21:46:38
 * @describe:
 */
@RestController
@Slf4j
public class RepayController {

    @Autowired
    private PictureFileRepo pictureFileRepo;

    @GetMapping("/getRepay/{clientName}")
    public String getRepay(@PathVariable String clientName) {
        clientName = "还款服务的服务名是: " + clientName;
        log.info(clientName);
        return clientName;
    }

    @PostMapping("/addPictureFile")
    public String addPictureFile(@RequestBody MultipartFile[] multipartFiles) throws IOException {
        int length = multipartFiles.length;
        log.info("要上传的文件个数:{}", length);
        for (int i = 0; i < multipartFiles.length; i++) {
            MultipartFile multipartFile = multipartFiles[i];
            PictureFile pictureFile = new PictureFile();
            pictureFile.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            pictureFile.setDueBillNo(SnowFlake.getInstance().nextId() + "");
            pictureFile.setFileName(multipartFile.getOriginalFilename());
            pictureFile.setByteArray(multipartFile.getBytes());
            pictureFile.setCreateTime(LocalDateTime.now());
            pictureFileRepo.save(pictureFile);
        }
        return "success";
    }

    @GetMapping("/addPictureFile")
    public String addPictureFile(@RequestParam String url) throws IOException {
        for (File file : Objects.requireNonNull(new File(url).listFiles())) {
            log.info("上传的文件名:{}", file.getName());
            PictureFile pictureFile = new PictureFile();
            pictureFile.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            pictureFile.setDueBillNo(SnowFlake.getInstance().nextId() + "");
            pictureFile.setFileName(file.getName());
            pictureFile.setByteArray(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
            pictureFile.setCreateTime(LocalDateTime.now());
            pictureFileRepo.save(pictureFile);
        }
        return "success";
    }


    @GetMapping("/viewPictureFile/{id}")
    public String viewPictureFile(@PathVariable("id") String id, HttpServletResponse response) throws Exception {
        pictureFileRepo.findById(id).ifPresentOrElse(e -> {
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                byte[] byteArray = e.getByteArray();
                String fileName = e.getFileName();
                outputStream.write(byteArray);
                response.setHeader("Content-disposition", "attachment; filename=" + fileName);
                outputStream.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }, () -> {
            log.info("该id:{},在表中不存在...", id);
        });
        return "success";
    }
}
