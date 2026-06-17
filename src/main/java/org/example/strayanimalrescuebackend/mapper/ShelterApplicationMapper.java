package org.example.strayanimalrescuebackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.strayanimalrescuebackend.model.ShelterApplication;

import java.util.List;

@Mapper
public interface ShelterApplicationMapper {
    // 查询所有救助站申请
    List<ShelterApplication> getAllApplications();

    // 根据申请ID获取单个申请
    ShelterApplication getApplicationById(int applicationId);

    // 更新申请状态
    void updateApplicationStatus(int applicationId, String status);

    // 根据申请ID删除申请
    void deleteApplication(@Param("applicationId") int applicationId);

    /**
     * 插入救助站申请
     * @param application ShelterApplication 对象
     * @return 插入成功的行数
     */
    int insertShelterApplication(ShelterApplication application);
}
