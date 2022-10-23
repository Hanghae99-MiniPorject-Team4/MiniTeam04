package com.example.advanced.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class File {

    private String fileName;

    private String fileUrl;

    private int sort;

    @Builder
    public File(String fileName, String fileUrl, int sort) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.sort = sort;
    }
}
