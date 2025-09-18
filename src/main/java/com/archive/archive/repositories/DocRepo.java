package com.archive.archive.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.archive.archive.models.Doc;

public interface DocRepo extends JpaRepository<Doc, Integer>, JpaSpecificationExecutor<Doc>{
    @Query("SELECT d FROM Doc d WHERE d.name LIKE CONCAT('%',:keyword,'%')")
    List<Doc> searchKeyword(String keyword);


    @Query("SELECT d FROM Doc d WHERE d.department.id = :deptId")
    List<Doc> findByDeptId(Integer deptId);

    @Query("SELECT d FROM Doc d WHERE d.docDate = :docDate")
    List<Doc> getByDate(LocalDate docDate);

    @Query("SELECT d FROM Doc d WHERE d.deletionDate <= :today ORDER BY d.folder")
    List<Doc> getDocsToDelete(LocalDate today);

    @Query(nativeQuery = true, value = "SELECT s.name, COUNT(d.id) FROM docs d FULL OUTER JOIN departments s USING(dept_id) GROUP BY s.name ORDER BY s.name")
    List<Object[]> deptStatistics();
    


}
