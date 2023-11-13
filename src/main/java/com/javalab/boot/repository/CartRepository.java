package com.javalab.boot.repository;

import com.javalab.boot.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    // 현재 로그인 회원의 카트 조회
    Cart findByMemberId(Long memberId);

}