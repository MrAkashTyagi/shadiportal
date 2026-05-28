package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.entities.Guest;
import com.bigsquare.ShadiPortal.serviceImpl.GuestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Controller
@RequestMapping("/guests")
public class GuestController {

    @Autowired
    private GuestServiceImpl guestService;

    @RequestMapping("/getAllGuests")
    public List<Guest> getAllGuests(){
        return this.guestService.getAllGuests();
    }

//    create Guests

    @PostMapping
    public Guest createGuest(@RequestBody Guest guest){
        Guest guest1 = this.guestService.saveGuest(guest);
        return guest1;
    }

//    get guest by id

    @GetMapping("/{id}")
    public Guest getById(@PathVariable Integer id){
        return this.guestService.getById(id);
    }

//    update guest

    @PutMapping("/{id}")
    public Guest updateGuest(@PathVariable Integer id, @RequestBody Guest guest){
        return this.guestService.updateGuest(id, guest);
    }

//    delete guest

    @DeleteMapping("/{id}")
    public void deleteGuest(@PathVariable Integer id){
        this.guestService.delete(id);
    }

    @RequestMapping(value = "/guestList", method = RequestMethod.GET)
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
