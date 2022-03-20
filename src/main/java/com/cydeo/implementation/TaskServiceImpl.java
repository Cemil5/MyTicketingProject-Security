package com.cydeo.implementation;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.entity.Project;
import com.cydeo.entity.Task;
import com.cydeo.entity.User;
import com.cydeo.mapper.ProjectMapper;
import com.cydeo.mapper.TaskMapper;
import com.cydeo.repository.ProjectRepository;
import com.cydeo.repository.TaskRepository;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.TaskService;
import com.cydeo.utils.Status;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private ProjectRepository projectRepository;
    private TaskMapper taskMapper;
    private ProjectMapper projectMapper;

    @Override
    public List<TaskDTO> findAll() {
        List<Task> list = taskRepository.findAll();
        return list.stream().map(taskMapper::convertToDto).collect(Collectors.toList());
      //  return list.stream().map(obj -> taskMapper.convertToDto(obj)).collect(Collectors.toList());
    }

    @Override
    public TaskDTO findById(Long id) {
        return taskMapper.convertToDto(taskRepository.getById(id));
    }

    @Override
    public TaskDTO save(TaskDTO dto) {
        Task task = taskMapper.convertToEntity(dto);
        task.setTaskStatus(Status.OPEN);
        task.setAssignedDate(LocalDate.now());
        //userDTO doesn't have id, and it uses username as a reference. I find user by username
        User user = userRepository.findByUserName(dto.getAssignedEmployee().getUserName());
        Project project = projectRepository.findByProjectCode(dto.getProject().getProjectCode());
        System.out.println(user.getUserName() + " " + user.getFirstName());
        task.setAssignedEmployee(user);
        task.setProject(project);

        taskRepository.save(task);
        return taskMapper.convertToDto(task);
      //  return taskMapper.convertToDto(taskRepository.findById(dto.getTaskId()).get());
    }

    @Override
    public void deleteById(Long id) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) {
            task.get().setIsDeleted(true);
            taskRepository.save(task.get());
        }
    }

    @Override
    public TaskDTO update(TaskDTO dto) {
        Task task = taskRepository.getById(dto.getId());
        Task converted = taskMapper.convertToEntity(dto);
        converted.setId(task.getId());
        //userDTO doesn't have id, and it uses username as a reference. I find user by username
//        User user = userRepository.findByUserName(dto.getAssignedEmployee().getUserName());
//        Project project = projectRepository.findByProjectCode(dto.getProject().getProjectCode());
//        converted.setAssignedEmployee(user);
//        converted.setProject(project);
        converted.setAssignedDate(task.getAssignedDate());
        converted.setTaskStatus(task.getTaskStatus());
        taskRepository.save(converted);
        return taskMapper.convertToDto(task);
    }

    @Override
    public void updateTaskStatus(TaskDTO dto) {
        Task task = taskRepository.getById(dto.getId());
     //   Optional<Task> task1 = taskRepository.findById(dto.getId());
        task.setTaskStatus(dto.getTaskStatus());
        taskRepository.save(task);
    }

    @Override
    public Integer totalNonCompletedTasks(String projectCode) {
        return taskRepository.totalNonCompleteTasks(projectCode);
    }

    @Override
    public Integer totalCompletedTasks(String projectCode) {
        return taskRepository.totalCompleteTasks(projectCode);
    }

    @Override
    public void deleteByProject(ProjectDTO projectDTO) {
        List<Task> tasks = taskRepository.findAllByProject(projectMapper.convertToEntity(projectDTO));
        tasks.forEach(task -> deleteById(task.getId()));
    }

    @Override
    public List<TaskDTO> listAllTasksByStatusIsNot(Status status) {
        List<Task> tasks = taskRepository.getNonCompleteTasks("a@b.c4");
        return tasks.stream().map(taskMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> listAllTasksByStatusIs(Status status) {
        List<Task> tasks = taskRepository.getCompletedTasks("a@b.c4");
        return tasks.stream().map(taskMapper::convertToDto).collect(Collectors.toList());
    }
//
//    @Override
//    public List<TaskDTO> listAllTasksByEmployee() {
//        User user = userRepository.findByUserName("a@b.c4");
//        List<Task> tasks = taskRepository.findAllByTaskStatusIsNotAndAssignedEmployee_UserName(Status.COMPLETE, user.getUserName());
//        return tasks.stream().map(taskMapper::convertToDto).collect(Collectors.toList());
//    }
}
