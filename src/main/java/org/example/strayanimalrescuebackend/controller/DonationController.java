package org.example.strayanimalrescuebackend.controller;

import org.example.strayanimalrescuebackend.model.DonationRecord;
import org.example.strayanimalrescuebackend.model.ResponseResult;
import org.example.strayanimalrescuebackend.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/donate")
public class DonationController {
    @Autowired
    private DonationService donationService;

    @PostMapping("/summitDonation")
    public ResponseResult summitDonation(
            @RequestHeader("token") String token,
            @RequestBody DonationRecord donationRecord
    ){
        return donationService.summitDonation(token,donationRecord);

    }


    @GetMapping("/getPersonalRecords")
    public ResponseResult getPersonalRecords(@RequestHeader("token") String token) {
        return donationService.getPersonalRecords(token);
    }


    @GetMapping("/getRecordsInShelter")
    public ResponseResult getRecordsInShelter(@RequestParam int shelterId) {
        return donationService.getRecordsInShelter(shelterId);
    }



}
