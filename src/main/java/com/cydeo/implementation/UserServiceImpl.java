package com.cydeo.implementation;

import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Project;
import com.cydeo.entity.Task;
import com.cydeo.entity.User;
import com.cydeo.exception.TicketingProjectException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.ProjectRepository;
import com.cydeo.repository.TaskRepository;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private ProjectRepository projectRepository;
    private TaskRepository taskRepository;
    private MapperUtil mapperUtil;


    @Override
    public List<UserDTO> listAllUsers() {
        List<User> list = userRepository.findAll(Sort.by("firstName"));
        // convert to dto
        return list.stream().map(obj -> {return mapperUtil.convert(obj, new UserDTO());}).collect(Collectors.toList());
    }

    @Override
    public UserDTO findByUserName(String username) {
        User user = userRepository.findByUserName(username);
        return mapperUtil.convert(user, new UserDTO());
    }

    @Override
    public void save(UserDTO dto) {
        User user = mapperUtil.convert(dto, new User());
        userRepository.save(user);
    }

    @Override
    public UserDTO update(UserDTO dto) {
        User user = userRepository.findByUserName(dto.getUserName());
        User convertedUser = mapperUtil.convert(dto, new User());
        convertedUser.setId(user.getId());
        userRepository.save(convertedUser);
        return findByUserName(dto.getUserName());
    }

    @Override
    public void delete(String username) throws TicketingProjectException {
      deleteByUsername(username);
    }

    // hard delete is not preferred, and we use soft delete below
    @Override
    public void deleteByUsername(String username) throws TicketingProjectException {
        User user = userRepository.findByUserName(username);

        if(user == null) throw new TicketingProjectException("User does not exist");

        if (!checkIfUserCanBeDeleted(user)) {
            throw new TicketingProjectException("User can not be deleted. It is linked by a project or task or only admin");
        }

        user.setUserName(user.getUserName()+ "-"+user.getId());
        user.setIsDeleted(true);
        userRepository.save(user);

    }

    @Override
    public List<UserDTO> listAllByRole(String role) {
        List<User> users = userRepository.findAllByRoleDescriptionIgnoreCase(role);
        return users.stream().map(obj -> {return mapperUtil.convert(obj, new UserDTO());}).collect(Collectors.toList());
    }

    @Override
    public Boolean checkIfUserCanBeDeleted(User user) {
        switch (user.getRole().getDescription().toLowerCase(Locale.ROOT)){
            case "manager":
           //     Project project = projectRepository.findByProjectCode("P004");
                List<Project> projects = projectRepository.findAllByAssignedManager(user);
                return projects.size() == 0;
            case "employee":
                List<Task> tasks = taskRepository.findAllByAssignedEmployee(user);
                return tasks.size() == 0;
            default:
                List<User> users = userRepository.findAllByRoleDescriptionIgnoreCase(user.getRole().getDescription());
                return users.size() > 1;
        }
    }
}
