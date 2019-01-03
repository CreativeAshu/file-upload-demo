package com.xyz.upload.demo.service;

import com.xyz.upload.demo.service.dto.StudentDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Student.
 */
public interface StudentService {

    /**
     * Save a student.
     *
     * @param studentDTO the entity to save
     * @return the persisted entity
     */
    StudentDTO save(StudentDTO studentDTO);

    /**
     * Get all the students.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<StudentDTO> findAll(Pageable pageable);


    /**
     * Get the "id" student.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<StudentDTO> findOne(Long id);

    /**
     * Delete the "id" student.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
