package com.example.advanced.service;

import com.example.advanced.domain.Files;
import com.example.advanced.domain.Member;
import com.example.advanced.domain.Post;
import com.example.advanced.jwt.TokenProvider;
import com.example.advanced.repository.FileRepository;
import com.example.advanced.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
@Service
public class FileUpdateService {

    private final TokenProvider tokenProvider;
    private final FileService fileService;
    private final PostRepository postRepository;
    private final FileRepository fileRepository;


    @Transactional
    public void createImage(MultipartFile multipartFile, HttpServletRequest request){

        Member member = validateMember(request);
        String fileUrl = "";

        try {
            fileUrl = fileService.uploadFile(multipartFile);
        } catch (IOException e) {
        }

        Post post = Post.builder()
                .member(member)
                .images(fileUrl)
                .build();

        postRepository.save(post);

        Files files = Files.builder()
                .url(fileUrl)
                .member(member)
                .build();
        fileRepository.save(files);
    }
    @Transactional
    public void update(MultipartFile multipartFile, HttpServletRequest request){
        Member member = validateMember(request);

        String fileUrl = "";

        try {
            fileUrl = fileService.uploadFile(multipartFile);
        } catch (IOException e) {
        }

        Post post = Post.builder()
                .member(member)
                .images(fileUrl)
                .build();

        postRepository.save(post);

        Files files = Files.builder()
                .url(fileUrl)
                .build();
        fileRepository.save(files);

    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }

}