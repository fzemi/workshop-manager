package com.fzemi.workshopmanager.part.repository;

import com.fzemi.workshopmanager.part.entity.Part;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartRepository extends JpaRepository<Part, Long>, JpaSpecificationExecutor<Part> {

    List<Part> findAll(Specification<Part> specification);
}
