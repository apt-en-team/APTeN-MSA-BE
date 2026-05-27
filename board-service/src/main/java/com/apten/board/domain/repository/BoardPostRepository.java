package com.apten.board.domain.repository;

import com.apten.board.domain.entity.BoardPost;
import com.apten.board.domain.enums.BoardCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardPostRepository extends JpaRepository<BoardPost, Long> {

    Page<BoardPost> findByComplexIdAndIsDeletedFalseOrderByCreatedAtDesc(Long complexId, Pageable pageable);

    Page<BoardPost> findByComplexIdAndCategoryAndIsDeletedFalseOrderByCreatedAtDesc(Long complexId, BoardCategory category, Pageable pageable);

    @Query("SELECT p FROM BoardPost p WHERE p.complexId = :complexId AND p.isDeleted = false AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%) ORDER BY p.createdAt DESC")
    Page<BoardPost> searchByKeyword(@Param("complexId") Long complexId, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM BoardPost p WHERE p.complexId = :complexId AND p.category = :category AND p.isDeleted = false AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%) ORDER BY p.createdAt DESC")
    Page<BoardPost> searchByKeywordAndCategory(@Param("complexId") Long complexId, @Param("category") BoardCategory category, @Param("keyword") String keyword, Pageable pageable);

    Optional<BoardPost> findByIdAndComplexIdAndIsDeletedFalse(Long id, Long complexId);

    Page<BoardPost> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<BoardPost> findTop10ByComplexIdAndIsDeletedFalseOrderByLikeCountDescCreatedAtDesc(Long complexId);

    long countByComplexIdAndIsDeletedFalse(Long complexId);

    Optional<BoardPost> findByIdAndComplexId(Long id, Long complexId);
}