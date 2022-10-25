package com.example.advanced.service;

import com.example.advanced.controller.response.ResponseDto;
import com.example.advanced.domain.Files;
import com.example.advanced.domain.Post;
import com.example.advanced.repository.FileRepository;
import com.example.advanced.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class FileUpdateService {

    private final FileService fileService;
    private final PostRepository postRepository;
    private final FileRepository fileRepository;

    public ResponseDto<?> update(MultipartFile multipartFile){

        String fileUrl = "";

        try {
            fileUrl = fileService.uploadFile(multipartFile);
        } catch (IOException e) {
        }

        Post post = Post.builder()
                .images(fileUrl)
                        .build();

        postRepository.save(post);

        Files files = Files.builder()
                .post(post)
                .url(fileUrl)
                .build();
        fileRepository.save(files);

        return ResponseDto.success("이미지 업로드에 성공했습니다.");
    }
}

