package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.entities.Guest;
import com.bigsquare.ShadiPortal.services.GuestService;
import com.bigsquare.ShadiPortal.services.GuestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.devtools.autoconfigure.ConditionalOnEnabledDevTools;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class GuestController {

    @Autowired
    private GuestServiceImpl guestService;


    @RequestMapping(value = "/guests", method = RequestMethod.GET)
    public String guestController(){
        return "guest";
    }

//    getting guests

    @RequestMapping(value = "/getGuests",method = RequestMethod.GET)
    public String guest(Model model){
        List<Guest> allGuests = guestService.getAllGuests();
        System.out.println(allGuests);
        model.addAttribute("guests",allGuests);

        List<String> names = List.of("akash", "anuj", "arjun");
        model.addAttribute("names",names);
        return "guest";

    }

}
