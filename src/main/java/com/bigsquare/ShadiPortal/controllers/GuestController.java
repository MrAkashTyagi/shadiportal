package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.dto.GuestCategorySummaryDto;
import com.bigsquare.ShadiPortal.dto.GuestSummaryDto;
import com.bigsquare.ShadiPortal.entities.Guest;
import com.bigsquare.ShadiPortal.serviceImpl.GuestServiceImpl;

import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@Controller
@RequestMapping("/guests")
@CrossOrigin(origins = "http://localhost:4200")
public class GuestController {

    @Autowired
    private GuestServiceImpl guestService;

    // get guests as per pagination
    @GetMapping("/guest")
    public Page<Guest> getAllGuests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "") String gender,
            @RequestParam(required = false, defaultValue = "") String adultOrchild,
            @RequestParam(required = false, defaultValue = "") String gift,
            @RequestParam(required = false, defaultValue = "") String cash,
            @RequestParam(required = false, defaultValue = "") String guestCategory,
            @RequestParam(required = false, defaultValue = "") String stay,
            @RequestParam(required = false, defaultValue = "") Boolean invitationSent
    ) {
        return this.guestService.getGuestWithPagination(
                page,
                size,
                search,
                gender,
                adultOrchild,
                gift,
                cash,
                guestCategory,
                stay,
                invitationSent
        );
    }

    @RequestMapping(value = "/getAllGuests", method = RequestMethod.GET)
    public List<Guest> getAllGuests() {
        return this.guestService.getAllGuests();
    }

//    create Guests

    @PostMapping
    public Guest createGuest(@RequestBody Guest guest) {
        Guest guest1 = this.guestService.saveGuest(guest);
        return guest1;
    }

//    get guest by id

    @GetMapping("/{id}")
    public Guest getById(@PathVariable Integer id) {
        return this.guestService.getById(id);
    }

    //    update guest
    @PutMapping("/{id}")
    public Guest updateGuest(@PathVariable Integer id, @RequestBody Guest guest) {
        guest.setId(id);
        return this.guestService.updateGuest(id, guest);
    }

//    delete guest

    @DeleteMapping("/{id}")
    public void deleteGuest(@PathVariable Integer id) {
        this.guestService.delete(id);
    }

    @RequestMapping(value = "/guestList", method = RequestMethod.GET)
    public String guestController() {
        return "guest";
    }

//    getting guests

    @RequestMapping(value = "/getGuests", method = RequestMethod.GET)
    public String guest(Model model) {
        List<Guest> allGuests = guestService.getAllGuests();
        System.out.println(allGuests);
        model.addAttribute("guests", allGuests);

        List<String> names = List.of("akash", "anuj", "arjun");
        model.addAttribute("names", names);
        return "guest";

    }

//    @RequestMapping("/download")
//    public ResponseEntity<Resource> downloadExcel() throws IOException {
//
//        String fileName = "products.xlsx";
//        ByteArrayInputStream actualData = this.guestService.getActualData();
//        InputStreamResource file = new InputStreamResource(actualData);
//        ResponseEntity<Resource> body = ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; fileName"+fileName)
//                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
//                .body(file);
//
//        return body;
//    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadExcel(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "") String gender,
            @RequestParam(required = false, defaultValue = "") String adultOrchild,
            @RequestParam(required = false, defaultValue = "") String guestCategory,
            @RequestParam(required = false, defaultValue = "") String gift,
            @RequestParam(required = false, defaultValue = "") String stay,
            @RequestParam(required = false, defaultValue = "") String cash,
            @RequestParam(
                    required = false
            )
            Boolean invitationSent
    ) throws IOException {

        ByteArrayInputStream actualData =
                this.guestService.getFilteredActualData(
                        search,
                        gender,
                        adultOrchild,
                        guestCategory,
                        gift,
                        stay,
                        cash,
                        invitationSent
                );

        InputStreamResource file = new InputStreamResource(actualData);

        String fileName = "guests.xlsx";

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\""
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(file);
    }

    @GetMapping("/summary")
    public ResponseEntity<GuestSummaryDto> getSummary() {

        return ResponseEntity.ok(
                guestService.getGuestSummary()
        );
    }

    @GetMapping("/category-summary")
    public ResponseEntity<List<GuestCategorySummaryDto>>
    getGuestCategorySummary() {

        return ResponseEntity.ok(
                guestService.getGuestCategorySummary()
        );
    }

}
