package dgucomai.tableorder.repository;

import dgucomai.tableorder.domain.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    // 메뉴랑 카테고리를 한 번에 가져오는 쿼리
    @Query("SELECT m FROM MenuItem m JOIN FETCH m.category")
    List<MenuItem> findAllWithCategory();
}