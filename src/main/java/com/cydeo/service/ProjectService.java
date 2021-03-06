package com.cydeo.service;

import com.cydeo.dto.ProjectDTO;

import java.util.List;

public interface ProjectService {

    ProjectDTO getByProjectCode(String code);
    List<ProjectDTO> listAllProjects();
    ProjectDTO save(ProjectDTO dto);
    ProjectDTO update(ProjectDTO dto);
    void delete(String code);

    void complete(String code);

    List<ProjectDTO> listAllProjectDetails();

    List<ProjectDTO> listAllNonCompletedProjects();
}
