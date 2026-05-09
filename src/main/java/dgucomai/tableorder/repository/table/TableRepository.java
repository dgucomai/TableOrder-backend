package dgucomai.tableorder.repository.table;

import dgucomai.tableorder.domain.entity.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableRepository extends JpaRepository<TableEntity, Long> {
}
