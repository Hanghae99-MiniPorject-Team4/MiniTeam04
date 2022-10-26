package com.example.advanced.service;

import com.example.advanced.controller.exception.ErrorCode;
import com.example.advanced.controller.handler.CustomException;
import com.example.advanced.controller.response.FileResponseDto;
import com.example.advanced.controller.response.ResponseDto;
import com.example.advanced.domain.Files;
import com.example.advanced.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FileService {

    private final FileS3Service fileS3Service;
    private final FileRepository fileRepository;


    @Transactional
    public ResponseDto<?> createImage(MultipartFile multipartFile){

        String fileUrl = "";

        try {
            fileUrl = fileS3Service.uploadFile(multipartFile);
        } catch (IOException e) {
        }

        /*Post post = Post.builder()
                .member(member)
                .images(fileUrl)
                .build();

        postRepository.save(post);*/

        Files files = Files.builder()
                .url(fileUrl)
                .build();
        fileRepository.save(files);

        return ResponseDto.success(FileResponseDto.builder().id(files.getId()).url(fileUrl).build());

    }
    @Transactional
    public ResponseDto<?> updateImage(Long id, MultipartFile multipartFile){
        String fileUrl = "";

        try {
            fileUrl = fileS3Service.uploadFile(multipartFile);
        } catch (IOException e) {
        }

        /*Post post = Post.builder()
                .member(member)
                .images(fileUrl)
                .build();

        postRepository.save(post);
*/
        Files files = Files.builder()
                .url(fileUrl)
                .build();
        fileRepository.save(files);

        return ResponseDto.success(FileResponseDto.builder().id(files.getId()).url(fileUrl).build());

    }

    @Transactional
    public ResponseDto<?> deleteImage(Long id){
        Files files = isPresentFile(id);
        if (null == files) {
            return CustomException.toResponse(new CustomException(ErrorCode.NOT_FOUND_POST));
        }
        fileRepository.delete(files);
        return ResponseDto.success("delete success");
    }
    @Transactional(readOnly = true)
    public Files isPresentFile(Long id) {
        Optional<Files> optionalFile = fileRepository.findById(id);
        return optionalFile.orElse(null);
    }

}