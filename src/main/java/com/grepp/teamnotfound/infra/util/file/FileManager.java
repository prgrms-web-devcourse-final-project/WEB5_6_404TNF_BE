package com.grepp.teamnotfound.infra.util.file;

import java.io.File;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

public class FileManager extends AbstractFileManager {

    @Value("${upload.path}")
    private String filePath;

    @Override
    protected void uploadFile(MultipartFile file, FileDto fileDto) throws IOException {
        File path = new File(filePath + fileDto.savePath());
        if (!path.exists()) {
            path.mkdirs(); // 경로에 포함된 모든 존재하지 않는 부모 디렉토리까지 포함하여 생성
        }

        File target = new File(filePath + fileDto.savePath() + fileDto.renamedName());
        file.transferTo(target);
    }
}
