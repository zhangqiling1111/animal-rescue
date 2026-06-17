package org.example.strayanimalrescuebackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.strayanimalrescuebackend.model.Shelter;
import org.example.strayanimalrescuebackend.model.ShelterApplication;

import java.util.List;


@Mapper
public interface ShelterMapper {
    /**
     * 查询所有救助站信息
     * @return Shelter 列表
     */
    List<Shelter> findAllShelters();

    /**
     * 根据救助站 ID 查询救助站信息
     * @param shelterId 救助站 ID
     * @return Shelter
     */
    Shelter findShelterById(@Param("shelterId") int shelterId);

    /**
     * 检查用户是否已经提交申请
     * @param userId 用户ID
     * @return 申请记录数量
     */
    int checkApplicationExists(@Param("userId") int userId);

    /**
     * 根据用户 ID 查找救助站申请记录
     * @param userId 用户 ID
     * @return ShelterApplication 申请记录
     */
    ShelterApplication findShelterApplicationByUserId(@Param("userId") int userId);

    /**
     * 根据救助站名称和地址进行搜索
     * @param shelterName 救助站名称
     * @param address 救助站地址
     * @return 符合条件的救助站列表
     */
    List<Shelter> searchShelters(@Param("shelterName") String shelterName, @Param("address") String address);

    /**
     * 根据用户ID查找救助站信息
     * @param userId 用户ID
     * @return 救助站信息
     */
    Shelter findShelterByUserId(int userId);

    /**
     * 更新救助站信息
     * @param shelter 救助站信息对象
     * @return 更新成功的记录数
     */
    int updateShelter(Shelter shelter);

    // 插入新的救助站记录
    void insertShelter(Shelter shelter);
}
