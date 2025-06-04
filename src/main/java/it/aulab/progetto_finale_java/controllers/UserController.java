package it.aulab.progetto_finale_java.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.progetto_finale_java.dtos.UserDto;
import it.aulab.progetto_finale_java.models.User;
import it.aulab.progetto_finale_java.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new UserDto());
        return "auth/register";
    }

    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
       
        User existingUser = userService.findUserByEmail(userDto.getEmail());
        
       if(existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()){
         result.rejectValue("email", null, "There is already an account registered with the  same email");
       }

       if(result.hasErrors()){
         model.addAttribute("user", userDto);
         return "auth/register";
       }

       userService.saveUser(userDto, redirectAttributes, request, response);

       redirectAttributes.addFlashAttribute("successMessage", "Registrazione avvenuta!");
       return "redirect:/";
       

    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
}
