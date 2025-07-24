package com.grepp.teamnotfound.app.model.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminServiceTest {

    // 없는 신고 exception : not found
    @Test
    void rejectReport() {

    }

    // pending 상태가 아닌 신고 exception : already complete

    // 요청 enum 입력이 잘못 된 경우 exception

    // 성공 - pending 상태인 신고 reject 처리 + 신고 updatedAt + 신고 adminReason 삽입

    // 성공2 - 다른 reportId, 같은 contentId, 같은 신고 카테고리, 같은 타입, 같은 상태(pending)
    // reject 처리

    // 성공2 - 다른 reportId, 같은 contentId, 같은 신고 카테고리, 같은 타입, 다른 상태(reject)
    // 다른 reportId는 reject 미처리

    // 성공3 - 다른 reportId, 같은 contentId, 같은 신고 카테고리, 같은 타입, 다른 상태(accept)
    // 다른 reportId는 reject 미처리

    // 성공4 - 다른 reportId, 같은 contentId, 같은 신고 카테고리, 다른 타입, 같은 상태(pending)
    // 다른 reportId reject 미처리

    // 성공5 - 다른 reportId, 같은 contentId, 다른 신고 카테고리, 같은 타입, 같은 상태(pending)
    // 다른 reportId reject 미처리


    // 없는 신고 exception
    @Test
    void acceptReportAndSuspendUser() {
    }

    // pending 상태가 아닌 신고 exception : already complete

    // 요청 enum 입력이 잘못 된 경우 exception

    // 성공 1 - 신고 타입이 board인 경우, 해당 article 숨김 처리(reportedAt) + updatedAt

    // 성공 2 - 신고 타입이 reply인 경우, 해당 reply 숨김 처리(reportedAt) + updatedAt

    // 성공 3 - 신고 타입이 reply인 경우, 해당 reply가 있는 article은 숨김 처리 x

    // 성공 4 - pending 상태인 신고 accept 처리 + 신고 updatedAt + 신고 adminReason 삽입

    // 성공5 - 다른 reportId, 같은 contentId, 같은 신고 카테고리, 같은 타입, 같은 상태(pending)
    // accept 처리

    // 성공6 - 다른 reportId, 같은 contentId, 같은 신고 카테고리, 같은 타입, 다른 상태(reject)
    // 다른 reportId는 accept 미처리 / updatedAt x

    // 성공7 - 다른 reportId, 같은 contentId, 같은 신고 카테고리, 같은 타입, 다른 상태(accept)
    // 다른 reportId는 accept 미처리 / updatedAt x

    // 성공8 - 다른 reportId, 같은 contentId, 같은 신고 카테고리, 다른 타입, 같은 상태(pending)
    // 다른 reportId accept 미처리

    // 성공9 - 다른 reportId, 같은 contentId, 다른 신고 카테고리, 같은 타입, 같은 상태(pending)
    // 다른 reportId accept 미처리

    // 성공 10 - 입력된 period 만큼 해당 article의 작성자 suspendedEndDate, updatedAt

    // 성공 11 - 입력된 period 만큼 해당 reply의 작성자 suspendedEndDate, updatedAt

    // 성공 12 - 영구 정지 입력 시, 해당 article의 작성자 suspendedEndDate, updatedAt


}