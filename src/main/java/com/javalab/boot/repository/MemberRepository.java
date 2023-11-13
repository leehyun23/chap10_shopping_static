package com.javalab.boot.repository;

import com.javalab.boot.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email);

//    필요시 최종 로그인 시간 업데이트
//    static final String UPDATE_MEMBER_LAST_LOGIN = "UPDATE Member "
//            + "SET LAST_LOGIN_TIME = :lastLoginTime "
//            + "WHERE EMAIL = :email";

//    @Transactional
//    @Modifying
//    @Query(value=UPDATE_MEMBER_LAST_LOGIN, nativeQuery = true)
//    public int updateMemberLastLogin(@Param("email") String email, @Param("lastLoginTime") LocalDateTime lastLoginTime);

}