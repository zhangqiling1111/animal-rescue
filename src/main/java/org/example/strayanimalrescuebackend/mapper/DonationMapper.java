package org.example.strayanimalrescuebackend.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.example.strayanimalrescuebackend.model.DonationRecord;

import java.util.List;

@Mapper
public interface DonationMapper {
    void addDonationRecord(DonationRecord donationRecord);

    DonationRecord getDonationRecord(int donationId);

    List<DonationRecord> getPersonalRecords(int userId);

    List<DonationRecord> getRecordsInShelter(int shelterId);
}
