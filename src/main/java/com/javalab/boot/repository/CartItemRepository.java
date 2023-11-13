package com.javalab.boot.repository;

import com.javalab.boot.dto.CartDetailDto;
import com.javalab.boot.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 카트 아이디와 상품(Item)을 이용해서 상품이 장바구니에 있는지 조회
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);

    /**
     * 장바구니 목록에 전달할 CartDetailDto 리스트를 조회하는 JPQL
     * @param cartId
     * @return
     */
    @Query("select new com.javalab.boot.dto.CartDetailDto(ci.id, i.itemNm, i.price, ci.count, CONCAT(im.uuid, '_', im.fileName)) " +
            "from CartItem ci, ItemImg im " +
            "join ci.item i " +
            "where ci.cart.id = :cartId " +
            "and im.item.id = ci.item.id " +
            "and im.repimgYn = 'Y' " +
            "order by ci.regDate desc"
            )
    List<CartDetailDto> findCartDetailDtoList(@Param("cartId") Long cartId);

}