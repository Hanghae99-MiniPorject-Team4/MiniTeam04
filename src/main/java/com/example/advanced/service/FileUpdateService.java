package com.example.advanced.service;

import com.example.advanced.domain.Files;
import com.example.advanced.domain.Post;
import com.example.advanced.repository.FileRepository;
import com.example.advanced.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class FileUpdateService {

    private final FileService fileService;
    private final PostRepository postRepository;
    private final FileRepository fileRepository;

    public void update(MultipartFile multipartFile){

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

    }
}

