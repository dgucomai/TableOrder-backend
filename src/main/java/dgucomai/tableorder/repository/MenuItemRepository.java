package dgucomai.tableorder.repository;

import dgucomai.tableorder.domain.MenuItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

  @Query("SELECT m FROM MenuItem m JOIN FETCH m.category")
  List<MenuItem> findAllWithCategory();
}
