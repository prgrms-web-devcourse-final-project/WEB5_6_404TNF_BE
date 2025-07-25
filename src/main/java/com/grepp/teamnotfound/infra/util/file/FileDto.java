package com.grepp.teamnotfound.infra.util.file;

import com.grepp.teamnotfound.app.model.board.entity.ArticleImg;
import com.grepp.teamnotfound.app.model.pet.entity.PetImg;
import com.grepp.teamnotfound.app.model.user.entity.UserImg;

public record FileDto(
    String originName,
    String renamedName,
    String depth,
    String savePath
) {

}
