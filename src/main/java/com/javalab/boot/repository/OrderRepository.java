package com.javalab.boot.repository;

import com.javalab.boot.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 현재 로그인 사용자의 email로 주문 조회
     * @param email
     * @param pageable
     * @return List<Order>
     */
    @Query("select o from Order o " +
            "where o.member.email = :email " +
            "order by o.orderDate desc")
    List<Order> findOrders(@Param("email") String email, Pageable pageable);

    // 현재 로그인 사용자의 이메일로 주문된 총 건수 조회
    @Query("select count(o) from Order o " +
            "where o.member.email = :email")
    Long countOrder(@Param("email") String email);

    // 추가
    List<Order> findByMemberEmail(String email);
}