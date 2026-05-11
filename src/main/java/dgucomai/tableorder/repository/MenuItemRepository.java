package dgucomai.tableorder.repository;

import dgucomai.tableorder.domain.MenuItems;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MenuItemRepository extends JpaRepository<MenuItems, Long> {

  @Query("SELECT m FROM MenuItems m JOIN FETCH m.category")
  List<MenuItems> findAllWithCategory();
}
