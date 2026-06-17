package org.example.strayanimalrescuebackend.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.strayanimalrescuebackend.model.AdoptionApplication;

import java.util.List;

@Mapper
public interface AdoptionMapper {
    // 插入领养申请
    void insertAdoptionApplication(AdoptionApplication adoptionApplication);

    // 查询该动物是否已被领养
    int existsAdoptedAnimal(@Param("animalId") int animalId);

    // 查询当前用户是否已提交过该动物的领养申请
    int existsUserApplication(@Param("userId") int userId, @Param("animalId") int animalId);

    // 删除领养申请
    int deleteAdoptionApplicationById(@Param("applicationId") int applicationId);

    // 根据用户 ID 和 动物 ID 查询领养申请信息
    AdoptionApplication findApplicationByUserAndAnimal(@Param("userId") int userId, @Param("animalId") int animalId);

    // 根据用户 ID 获取用户所有的领养申请信息
    List<AdoptionApplication> getAdoptionDetailsByUser(@Param("userId") int userId);

     //根据申请 ID 查询领养申请信息
    AdoptionApplication findAdoptionApplicationByApplicationId(int applicationId);

    //分页查询某个救助站的所有领养申请
    List<AdoptionApplication> getAdoptionApplicationsByShelterId(
            @Param("shelterId") int shelterId,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    int updateApplicationNotes(@Param("applicationId") int applicationId,
                               @Param("notes") String notes);

    int updateApplicationStatus(@Param("applicationId") int applicationId,
                                @Param("status") String status);

    // 根据用户ID和动物品种查找领养申请记录
    List<AdoptionApplication> findApplicationsByUserIdAndBreed(@Param("userId") int userId, @Param("breed") String breed);


}
