package com.cydeo.controller;

import com.cydeo.dto.UserDTO;
import com.cydeo.exception.TicketingProjectException;
import com.cydeo.service.RoleService;
import com.cydeo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    RoleService roleService;
    @Autowired
    UserService userService;

    @GetMapping({"/create", "/add", "/initialize"})
    public String  createUser(Model model){

        model.addAttribute("user", new UserDTO());
        model.addAttribute("roles", roleService.listAllRoles());
     //   System.out.println("controller : " + roleService.listAllRoles().get(0).getDescription());
        model.addAttribute("users", userService.listAllUsers());

        return "user/create";
    }


    @PostMapping("/create")
    public String insertUser(UserDTO user, Model model){

        userService.save(user);

//        model.addAttribute("user", new UserDTO());
//        model.addAttribute("roles", roleService.findAll());
//        model.addAttribute("users", userService.findAll());
//        return "user/create";

        // instead of writing codes above, we use redirect keyword.
        return "redirect:/user/create";
    }

    @GetMapping("/update/{username}")
    public String editUser(@PathVariable String username, Model model){
        model.addAttribute("user", userService.findByUserName(username));
        model.addAttribute("users", userService.listAllUsers());
        model.addAttribute("roles", roleService.listAllRoles());
        return "user/update";
    }

    @PostMapping("/update/{username}")
    public String updateUser(@PathVariable String username, UserDTO user, Model model){

        userService.update(user);
//        model.addAttribute("user", new UserDTO());
//        model.addAttribute("roles", roleService.findAll());
//        model.addAttribute("users", userService.findAll());
//        return "user/create";

        // instead of writing codes above, we use redirect keyword.
        return "redirect:/user/create";
    }

    @GetMapping("/delete/{username}")
    public String prepToDeleteUser (@PathVariable String username) throws TicketingProjectException {
        userService.deleteByUsername(username);
        return "redirect:/user/create";
    }
}
