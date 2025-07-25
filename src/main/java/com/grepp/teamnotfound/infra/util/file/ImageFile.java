package com.grepp.teamnotfound.infra.util.file;

import java.time.OffsetDateTime;

public interface ImageFile {

    OffsetDateTime getDeletedAt(); // soft delete 가 적용되는 이미지임을 나타냄
    FileDto toFileDto();
}
