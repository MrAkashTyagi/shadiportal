package com.bigsquare.ShadiPortal.serviceImpl;

import com.bigsquare.ShadiPortal.dto.FamilySummaryDto;
import com.bigsquare.ShadiPortal.entities.Family;
import com.bigsquare.ShadiPortal.entities.Guest;
import com.bigsquare.ShadiPortal.repositories.FamilyRepo;
import com.bigsquare.ShadiPortal.repositories.GuestRepo;
import com.bigsquare.ShadiPortal.services.FamilyService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FamilyServiceImpl implements FamilyService {

    @Autowired
    private FamilyRepo familyRepo;

    @Autowired
    private GuestRepo guestRepo;

    @Override
    public Family createFamily(Family family) {

        Optional<Family> existingFamily = this.familyRepo.findByFamilyName(family.getFamilyName().trim());
        if (existingFamily.isPresent()){
            System.out.println("Family with this name is present in the database !!");
            return existingFamily.get();
        }
        return this.familyRepo.save(family);
    }

    @Override
    public List<Family> getAllFamilies() {
        return this.familyRepo.findAll();
    }

    @Override
    public Family updateFamily(Integer id, Family family) {


        Family existingFamily = this.familyRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Family with given id is not pressent !!"));
        System.out.println("existing family is : " + existingFamily);

        Optional<Family> duplicateFamily = this.familyRepo.findByFamilyName(family.getFamilyName());
        if (duplicateFamily.isPresent() && !duplicateFamily.get().getId().equals(id)) {
            throw new IllegalArgumentException("Family with this name already exists !!");
        }

        existingFamily.setFamilyName(family.getFamilyName());

//        List<Family> familyList = this.familyRepo.findAll();
//        Family family1 = familyList.stream().filter(f -> f.getFamilyName() != existingFamily.getFamilyName()).findAny().get();
//        System.out.println(family1);

//        for (Family family1 : familyList){
//            System.out.println("inside loop");
//            System.out.println("Family list wala name :"+family1.getFamilyName());
//            System.out.println("Family wala name jo ui se aa rha h : "+family.getFamilyName());
//
//            if (family1.getId() != id && family1.getFamilyName() == family.getFamilyName()){
//                System.out.println("inside if of loop");
//                throw new IllegalArgumentException("Family with this name already exists !!");
//            }
//        }
        return this.familyRepo.save(existingFamily);

    }

    @Override
    public Family getByFamilityId(Integer id) {
        return this.familyRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Family with this id is not in the db"));
    }

    @Override
    public void deleteFamily(Integer id) {
//        this.familyRepo.deleteById(id);

        Family family = this.familyRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Family does not exists !!"));

        if (family.getGuestList() != null) {
            for (Guest guest : family.getGuestList()) {
                guest.setFamily(null);
            }
        }
        familyRepo.delete(family);
    }

    @Override
    public Page<Family> getPaginatedFamilyResult(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        String searchValue = search == null ? "" : search.trim();
        if (searchValue.isEmpty()) {

            return this.familyRepo.findAll(pageable);

        }
            // Agar search me kuch value hai, toh custom query chalayein
        return this.familyRepo.findBySearchQuery(
                searchValue,
                pageable);

    }

    @Override
    public FamilySummaryDto getFamilySummary() {

        Long totalFamilies = familyRepo.count();

        Long totalFamilyMembers =
                familyRepo.getTotalFamilyMembers();

        Integer largestFamilySize =
                familyRepo.getLargestFamilySize();

        return new FamilySummaryDto(
                totalFamilies,
                totalFamilyMembers,
                largestFamilySize
        );
    }
}


