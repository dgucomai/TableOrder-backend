package dgucomai.tableorder.repository.table;

import dgucomai.tableorder.domain.entity.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

//DB에서 테이블 데이터를 꺼내오는 역할
public interface TableRepository extends JpaRepository<TableEntity, Long> {
}
