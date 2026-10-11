package com.cognevance.coursemgmt.repository;

import com.cognevance.coursemgmt.model.Course;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("select c from Course c where (:search is null or lower(c.title) like lower(concat('%', :search, '%')) "
         + "or lower(c.instructor) like lower(concat('%', :search, '%'))) "
         + "and (:category is null or c.category = :category) and (:level is null or c.level = :level) order by c.id desc")
    List<Course> filter(@Param("search") String search, @Param("category") String category, @Param("level") String level);
}