package com.archive.archive.audit;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocActionRepo extends JpaRepository<DocAction, Integer>{

    // старая статистика
    //@Query(nativeQuery = true, value = "SELECT s.name, COUNT(a.id) FROM actions a FULL OUTER JOIN statuses s USING(status_id) GROUP BY s.name ORDER BY s.name")
    //List<Object[]> statistics();
}
