package org.example.strayanimalrescuebackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.strayanimalrescuebackend.model.RescueRecord;
import org.example.strayanimalrescuebackend.model.RescueReport;

import java.util.List;

@Mapper
public interface RescueMapper {
    List<RescueRecord> getRescueRecords(int recordId, int shelterId, int animalId);

    int addRescueReport(RescueReport rescueReport);

    RescueReport getRescueReport(int reportId);

    List<RescueReport> getReportByUserId(int userId);

    List<RescueReport> getReportByShelterId(int shelterId);

    void checkReport(int reportId, int shelterId,String status);

    void insertRescueRecord(RescueRecord rescueRecord);

    RescueRecord getRescueRecordById(int recordId);
}
