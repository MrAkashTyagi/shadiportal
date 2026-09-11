package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.dto.FamilyRequest;
import com.bigsquare.ShadiPortal.dto.FamilySummaryDto;
import com.bigsquare.ShadiPortal.entities.Family;
import com.bigsquare.ShadiPortal.services.FamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/family")
@CrossOrigin("*")
public class FamilyController {

    @Autowired
    private FamilyService familyService;

    //    create
//    @PostMapping
//    public Family createFamily(@RequestBody Family family) {
//        return this.familyService.createFamily(family);
//    }

    @PostMapping
    public Family createFamily(
            @RequestBody FamilyRequest request
    ) {
        return familyService.createFamily(request);
    }

    //    getAll
    @GetMapping
    public Page<Family> getFamilies(

            @RequestParam Integer userId,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size,

            @RequestParam(defaultValue = "") String search
    ) {

        return familyService.getPaginatedFamilyResult(
                userId,
                page,
                size,
                search
        );
    }

    //    get by id
    @GetMapping("/{id}")
    public Family getById(@PathVariable Integer id) {
        return this.familyService.getByFamilityId(id);
    }

    //    updated
//    @PutMapping("/{id}")
//    public Family updateFamily(@PathVariable Integer id, @RequestBody Family family) {
//
//        return this.familyService.updateFamily(id, family);
//
//    }

    @PutMapping("/{id}")
    public Family updateFamily(
            @PathVariable Integer id,
            @RequestBody FamilyRequest request
    ) {
        return familyService.updateFamily(id, request);
    }

    //    delete
    @DeleteMapping("/{id}")
    public void deleteFamily(@PathVariable Integer id) {
        this.familyService.deleteFamily(id);
    }

    // get all for dropdown
    @GetMapping("/getAll")
    public List<Family> getAllFamilies() {
        return familyService.getAllFamilies();
    }

    @GetMapping("/summary")
    public ResponseEntity<FamilySummaryDto> getFamilySummary() {

        return ResponseEntity.ok(
                familyService.getFamilySummary()
        );
    }

}
