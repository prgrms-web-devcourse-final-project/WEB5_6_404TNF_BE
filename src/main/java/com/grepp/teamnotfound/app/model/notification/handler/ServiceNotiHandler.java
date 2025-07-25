package com.grepp.teamnotfound.app.model.notification.handler;

import com.grepp.teamnotfound.app.model.notification.code.NotiType;
import com.grepp.teamnotfound.app.model.notification.dto.NotiServiceCreateDto;
import com.grepp.teamnotfound.app.model.notification.dto.NotiUserDto;
import com.grepp.teamnotfound.app.model.user.entity.User;

public interface ServiceNotiHandler {
    NotiUserDto handle(User user, NotiType notiType, NotiServiceCreateDto dto);
}
