package com.bigsquare.ShadiPortal.controllers;

import com.bigsquare.ShadiPortal.entities.Family;
import com.bigsquare.ShadiPortal.services.FamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/family")
public class FamilyController {

    @Autowired
    private FamilyService familyService;

    //    create
    @PostMapping
    public Family createFamily(@RequestBody Family family) {
        return this.familyService.createFamily(family);
    }


    //    getAll
    @GetMapping
    public List<Family> getAllFamilies() {
        return this.familyService.getAllFamilies();
    }

//    get by id

    @GetMapping("/{id}")
    public Family getById(@PathVariable Integer id) {
        return this.familyService.getByFamilityId(id);
    }

//    updated

    @PutMapping("/{id}")
    public Family updateFamily(@PathVariable Integer id, @RequestBody Family family) {

        return this.familyService.updateFamily(id, family);

    }

    //    delete
    @DeleteMapping("/{id}")
    public void deleteFamily(@PathVariable Integer id) {
        this.familyService.deleteFamily(id);
    }


}
