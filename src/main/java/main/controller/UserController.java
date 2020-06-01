package main.controller;


import main.model.Role;
import main.model.User;
import main.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Controller
public class UserController {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/")
    public String greeting() {
        return "hello";
    }

    @GetMapping("/newAdmin")
    public String newAdmin() {
        HashSet<Role> roles = new HashSet<>();
        roles.add(Role.ADMIN);
        roles.add(Role.USER);
        User user = new User("ADMIN", passwordEncoder.encode("ADMIN"), roles);
        userRepo.save(user);
        return "/hello";
    }

    @GetMapping("/admin/addUserPage")
    public String addUserPage() {
        return "addUserPage";
    }

    @PostMapping("/admin/add")
    public String addUser(User user, @RequestParam("role") String role) {
        Set<Role> userRoles = getRoles(role);
        User userNew = new User();
        userNew.setUserRoles(userRoles);
        userNew.setName(user.getName());
        userNew.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(userNew);
        return "redirect:/admin/adminPage";
    }

    @GetMapping("/admin/adminPage")
    public ModelAndView listUsers(User user) {
        Iterable<User> list = userRepo.findAll();
        ModelAndView modelAndView = new ModelAndView("adminPage");
        modelAndView.getModelMap().addAttribute("listUsers", list);
        return modelAndView;
    }

    @PostMapping("/admin/adminPage")
    public ModelAndView viewAdminPage(User user) {


        Iterable<User> list = userRepo.findAll();
        ModelAndView modelAndView = new ModelAndView("adminPage");
        modelAndView.getModelMap().addAttribute("listUsers", list);
        return modelAndView;
    }


    @PostMapping("/admin/editUserPage")
    public ModelAndView viewEditPage(@RequestParam Long id) {
        Iterable<User> list = userRepo.findAllById(Collections.singleton(id));
        ModelAndView modelAndView = new ModelAndView("editUserPage");
        modelAndView.getModelMap().addAttribute("listUsers", list);
        return modelAndView;
    }

    @PostMapping("/admin/edit")
    public String editUser(User user, @RequestParam("role") String role) {
        User byId = userRepo.findById(user.getId()).get();
        Set<Role> userRoles = getRoles(role);
        byId.setUserRoles(userRoles);
        byId.setName(user.getName());

        byId.setPassword(passwordEncoder.encode( user.getPassword()));
        userRepo.save(byId);
        return "redirect:/admin/adminPage";
    }

    @PostMapping("/admin/delete")
    public String delUser(@RequestParam("id") Long id) {
        userRepo.deleteById(id);
        return "redirect:/admin/adminPage";
    }

    @PostMapping("/admin/delUserPage")
    public ModelAndView viewDelPage(@RequestParam("id") Long id) {
        Iterable<User> list = userRepo.findAllById(Collections.singleton(id));
        ModelAndView modelAndView = new ModelAndView("delUserPage");
        modelAndView.getModelMap().addAttribute("listUsers", list);
        return modelAndView;
    }

    @GetMapping("/user/userPageInfo")
    public ModelAndView printWelcome(User user) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User byName = userRepo.findByName(name);
        Iterable<User> list = userRepo.findAllById(Collections.singleton(byName.getId()));
        ModelAndView modelAndView = new ModelAndView("userPageInfo");
        modelAndView.getModelMap().addAttribute("listUsers", list);
        return modelAndView;
    }

    @PostMapping("/user/userPageInfo")
    public ModelAndView printWelcomeasd(User user) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User byName = userRepo.findByName(name);
        Iterable<User> list = userRepo.findAllById(Collections.singleton(byName.getId()));
        ModelAndView modelAndView = new ModelAndView("userPageInfo");
        modelAndView.getModelMap().addAttribute("listUsers", list);
        return modelAndView;
    }

    private Set<Role> getRoles(@RequestParam("role") String role) {
        Set<Role> userRoles = new HashSet<>();
        String[] split = role.split(",");
        for (String s : split) {
            userRoles.add(Role.valueOf(s));
        }
        return userRoles;
    }

    @GetMapping("/reg")
    private String showRegisterPage() {
        return "reg";
    }

    @PostMapping("/reg")
    private String showRegisterPageByAddNewUser(User user, Map<String, Object> model) {
        User byName = userRepo.findByName(user.getName());
        if (byName != null) {
            model.put("message", "This isn't new User");
            return "reg";
        }
        user.setUserRoles(Collections.singleton(Role.ADMIN));
        userRepo.save(user);
        return "redirect:/login";
    }

}
