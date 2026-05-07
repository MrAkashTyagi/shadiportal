package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.entities.User;
import com.bigsquare.ShadiPortal.helper.Message;
import com.bigsquare.ShadiPortal.repositories.UserRepo;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    @Autowired
    UserRepo userRepo;
//home handler
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String homeController(Model model){
        model.addAttribute("title","Shadi - Portal to manage everthing");
        return "home";
    }
// about handler
    @RequestMapping(value = "/about", method = RequestMethod.GET)
    public String about(Model model){
        model.addAttribute("title","shadi - a portal to amnage everythign");
        return "about";

    }

//    signup handler
    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signUp(Model model){

        model.addAttribute("user",new User());

        model.addAttribute("title","Register - Shadi Portal");
        return "signup";
    }

//    handler for regestring user
    @RequestMapping(value = "/do_register",method = RequestMethod.POST)
    public String registerUser(@ModelAttribute("user") User user, @RequestParam(value = "agreement",defaultValue = "false")boolean agreement, Model model, HttpSession session){

        try{
            if (!agreement){
                System.out.println("You have nor checked the agreement !!");
                throw new Exception("You have nor checked the agreement !!");
            }

            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImage("default.png");

            User result = userRepo.save(user);

            System.out.println("Agreement : "+agreement);
            System.out.println("User : "+user);


            model.addAttribute("user",new User());

            session.setAttribute("message",new Message("Successfully registered", "aler-success"));

        }
        catch (Exception e){
            e.printStackTrace();
            model.addAttribute("user",user);
            session.setAttribute("message", new Message("Something went wrong!!"+e.getMessage(), "alert-danger"));
            return "signup";

        }

        return "signup";
}

}
