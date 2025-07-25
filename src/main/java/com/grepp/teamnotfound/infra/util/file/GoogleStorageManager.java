package com.grepp.teamnotfound.infra.util.file;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.grepp.teamnotfound.infra.error.exception.CommonException;
import com.grepp.teamnotfound.infra.error.exception.code.CommonErrorCode;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
public class GoogleStorageManager extends AbstractFileManager {

    @Value("${google.cloud.storage.bucket}")
    private String bucket;
    private final String storageBaseUrl = "https://storage.googleapis.com/";

    @Override
    protected void uploadFile(MultipartFile file, FileDto fileDto) throws IOException {
        Storage storage = StorageOptions.getDefaultInstance().getService();

        if (file.getOriginalFilename() == null) {
            throw new CommonException(CommonErrorCode.FILE_INVALID_NAME);
        }

        String renamedName = fileDto.renamedName();
        BlobId blobId = BlobId.of(bucket, fileDto.depth() + "/" + renamedName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        storage.create(blobInfo, file.getBytes());
    }

    @Override
    public void delete(List<FileDto> fileDtos) {
        if (fileDtos == null || fileDtos.isEmpty()) {
            return;
        }
        for (FileDto fileDto : fileDtos) {
            deleteFile(fileDto);
        }
    }

    @Override
    protected void deleteFile(FileDto fileDto) {
        // fileDto.savePath() = storageBaseUrl + bucket + / + depth + /
        Storage storage = StorageOptions.getDefaultInstance().getService();
        String objectName = fileDto.depth() + "/" + fileDto.renamedName();
        BlobId blobId = BlobId.of(bucket, objectName);

        boolean deleted = storage.delete(blobId);
        if (!deleted) {
            log.warn("GCS 파일 삭제 실패: {}", objectName);
        } else {
            log.info("GCS 파일 삭제 성공: {}", objectName);
        }
    }

    @Override
    protected String createSavePath(String depth) {
        return storageBaseUrl + bucket + "/" + depth + "/";
    }
}
