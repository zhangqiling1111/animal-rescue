package org.example.strayanimalrescuebackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.strayanimalrescuebackend.model.User;

import java.util.List;

@Mapper
public interface UserMapper {
    /**
     * 查询用户是否存在
     * @param username 用户名
     * @param email 邮箱
     * @return 存在返回 1，否则返回 0
     */
    int checkUserExists(@Param("username") String username, @Param("email") String email);

    /**
     * 注册用户
     * @param user 用户对象
     * @return 影响行数
     */
    int registerUser(User user);

    /*
    * 根据用户名查找用户信息
    * */
    User findByUsername(String username);

    /*
    * 根据邮箱查找用户信息
    * */
    public User findByEmail(String email);

    /**
     * 检查邮箱是否存在（排除当前用户）
     */
    int checkEmailExists(@Param("email") String email, @Param("username") String username);

    /**
     * 更新用户个人信息
     */
    int updateUserProfile(User user);

    // 修改密码
    int updatePassword(@Param("username") String username,
                       @Param("newPasswordHash") String newPasswordHash);

    /**
     * 查询所有用户信息
     * @return 用户列表
     */
    List<User> findAllUsers();

    /**
     * 根据用户 ID 查询用户信息
     */
    User getUserById(@Param("id") Long id);

    // 更新用户角色
    int updateUserRole(@Param("userId") int userId, @Param("role") String role);

    // 更新用户冻结状态
    void updateUserFrozenStatus(@Param("userId") Long userId, @Param("isFrozen") boolean isFrozen);

}
