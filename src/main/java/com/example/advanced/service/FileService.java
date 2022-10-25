package com.example.advanced.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.advanced.controller.exception.ErrorCode;
import com.example.advanced.controller.exception.FileConvertException;
import com.example.advanced.controller.exception.RemoveFileException;
import com.example.advanced.controller.handler.CustomException;
import com.example.advanced.shared.MutipartToFileConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;
import java.util.UUID;

@Service
@Component
@Slf4j
@RequiredArgsConstructor
public class FileService {
    @Value("${aws.s3.bucket.name}")
    private String BUCKET;


    @Value("${aws.s3.path.url}")
    private String BUCKET_PATH;
    private final AmazonS3 amazonS3;

    private final MutipartToFileConverter mutipartToFileConverter;

    public String uploadFile(MultipartFile multipartFile
    ) throws IOException {

        if (multipartFile.isEmpty()) {
            throw new CustomException(ErrorCode.EMPTY_MULTIPART_FILE);
        }

        File licenseFile = mutipartToFileConverter.convert(multipartFile)
                .orElseThrow(FileConvertException::new);

        if (multipartFile.isEmpty()) {
            throw new CustomException(ErrorCode.EMPTY_MULTIPART_FILE);
        }

        String now = Instant
                .now().atZone(ZoneId.of("Asia/Seoul")).toString()
                .replace("T", "-")
                .replace("Z", "")
                .replace("[Asia/Seoul]", "");
        String fileName = BUCKET_PATH + now + UUID.randomUUID() + "."
                + Objects.toString(multipartFile.getOriginalFilename()).split("\\.")[1];

        System.out.println("fileName = " + fileName);

        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentType(multipartFile.getContentType());
        objMeta.setContentLength(multipartFile.getInputStream().available());

        //PutObjectRequest는 Aws S3 버킷에 업로드할 객체 메타 데이터와 파일 데이터로 이루어져있다.
        amazonS3.putObject(BUCKET, fileName, multipartFile.getInputStream(),objMeta);
        removeNewFile(licenseFile);

        return amazonS3.getUrl(BUCKET, fileName).toString();

    }

    public void removeNewFile(File targetFile) {
        if (!targetFile.delete()) {
            throw new RemoveFileException();
        }
    }
/*
    public File downloadImage(String imageUrl,String fileName) {
        URL url;
        //읽기 객체
        InputStream is;
        //쓰기 객체
        OutputStream os;
        try {
            url = new URL(imageUrl);

            is = url.openStream();

            os = new FileOutputStream(fileName);

            byte[] buffer = new byte[1024*16];

            while (true) {
                int inputData = is.read(buffer);
                if(inputData == -1)break;
                os.write(buffer,0,inputData);
            }

            is.close();
            os.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

        return new File(fileName);
    }*/
}

