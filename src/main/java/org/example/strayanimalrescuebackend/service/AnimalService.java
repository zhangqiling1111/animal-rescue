package org.example.strayanimalrescuebackend.service;

import org.example.strayanimalrescuebackend.Util.AliOSSUtils;
import org.example.strayanimalrescuebackend.Util.JwtUtil;
import org.example.strayanimalrescuebackend.mapper.AnimalMapper;
import org.example.strayanimalrescuebackend.mapper.UserMapper;
import org.example.strayanimalrescuebackend.model.Animal;
import org.example.strayanimalrescuebackend.model.ResponseResult;
import org.example.strayanimalrescuebackend.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AnimalService {

    private static final Logger logger = LoggerFactory.getLogger(AnimalService.class);
    @Autowired
    private AnimalMapper animalMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 分页查询动物信息
     * @return responseResult
     */
    public ResponseResult getAnimals(int page,int limit){

        int offset = (page-1)*limit;
        List<Animal> animalList = animalMapper.getAnimals(offset,limit);
        ResponseResult responseResult = new ResponseResult();

        System.out.println(animalList);

        try {
            // 添加非空校验和空集合处理
            if (animalList != null && !animalList.isEmpty()) {
                // 成功响应：包含数据体和200状态码
                responseResult.setCode(HttpStatus.OK.value());
                responseResult.setMessage("成功返回动物信息列表");
                responseResult.setData(animalList);
                responseResult.setTotal((long) animalList.size());
            } else {
                // 优化说明：处理空结果情况，返回404状态码避免返回无意义的空数组
                responseResult.setCode(200);
                responseResult.setMessage("没有查询到动物信息");
                responseResult.setData(null);
                responseResult.setTotal(0L);
            }
        } catch (Exception e) {
            logger.error("获取动物信息列表异常",e);
            // 优化说明：异常时返回500状态码和空数组
            responseResult.setCode(500);
            responseResult.setMessage("服务器内部问题");
            responseResult.setData(null);
            responseResult.setTotal(0L);
        }
        return responseResult;
    }

    /**
     * 根据提供的关键字搜素流浪动物
     * @return responseResult
     */
    public ResponseResult searchAnimals(String breed, String area, String healthStatus, int isAdoptable, String shelterName) {
        ResponseResult responseResult = new ResponseResult();
        try {
            List<Animal> animalList = animalMapper.searchAnimals(breed,area,healthStatus,isAdoptable,shelterName);
            if (!animalList.isEmpty()){
                responseResult.setCode(HttpStatus.OK.value());
                responseResult.setMessage("搜索成功");
                responseResult.setData(animalList);
                responseResult.setTotal((long)animalList.size());
            }else {
                responseResult.setCode(HttpStatus.OK.value());
                responseResult.setMessage("无相关动物");
                responseResult.setData(null);
                responseResult.setTotal(0L);
            }
        }catch (Exception e){
            responseResult.setCode(500);
            responseResult.setMessage(e.getMessage());
            responseResult.setData(null);
            responseResult.setTotal(0L);
        }
        return responseResult;
    }

    /**
     * 根据动物Id查询动物Id
     * @param animalId
     * @return ResponseResult
     */
    public ResponseResult getAnimalInfo(int animalId) {
        ResponseResult responseResult = new ResponseResult();
        Animal animal;
        try {
            if (animalId > 0){
               animal = animalMapper.getAnimalInfo(animalId);
               if (animal == null) {
                   responseResult.setCode(HttpStatus.OK.value());
                   responseResult.setMessage("查询成功,无该动物");
                   responseResult.setData(null);
                   responseResult.setTotal(0L);
               } else {
                   responseResult.setCode(HttpStatus.OK.value());
                   responseResult.setMessage("查询成功");
                   responseResult.setData(animal);
                   responseResult.setTotal(1L);
               }
            } else {
                responseResult.setCode(HttpStatus.BAD_REQUEST.value());
                responseResult.setMessage("非法动物Id,无法查询");
                responseResult.setData(null);
                responseResult.setTotal(0L);
            }

        } catch (Exception e){
            responseResult.setCode(500);
            responseResult.setMessage(e.getMessage());
            responseResult.setData(null);
            responseResult.setTotal(0L);
        }
        return responseResult;
    }

    /**
     * 查询某个救助站的所有动物信息
     * @param shelterId
     * @return ResponseResult
     */
    public ResponseResult getAnimalsInShelter(int shelterId) {
        ResponseResult responseResult = new ResponseResult();
        try {
            if (shelterId > 0){
                List<Animal> animalList = animalMapper.getAnimalsInShelter(shelterId);
                if (!animalList.isEmpty()){
                    responseResult.setCode(HttpStatus.OK.value());
                    responseResult.setMessage("查询成功");
                    responseResult.setData(animalList);
                    responseResult.setTotal((long)animalList.size());
                } else {
                    responseResult.setCode(HttpStatus.OK.value());
                    responseResult.setMessage("该救助站没有动物");
                    responseResult.setData(animalList);
                    responseResult.setTotal(0L);
                }

            } else {
                responseResult.setCode(HttpStatus.BAD_REQUEST.value());
                responseResult.setMessage("非法Id,无法查询");
                responseResult.setData(null);
                responseResult.setTotal(0L);
            }        } catch (Exception e){
            responseResult.setCode(500);
            responseResult.setMessage(e.getMessage());
            responseResult.setData(null);
            responseResult.setTotal(0L);
        }
        return responseResult;
    }

    /**
     * 救助站添加新的流浪动物信息
     * @param animal
     * @return responseResult
     */
    public ResponseResult  addAnimal(Animal animal,int shelterId,String token) {
        ResponseResult responseResult = new ResponseResult();
        try {
            /*1.判断参数animal是否为空*/
            if (animal != null){
                /*2.核对救助站身份:shelterId和userId都在shelter表中*/
                // 解析 token 获取用户ID
                String username = JwtUtil.extractUsername(token);
                if (username == null) {
                    responseResult.setCode(HttpStatus.BAD_REQUEST.value());
                    responseResult.setMessage("无效token");
                    responseResult.setData(null);
                    responseResult.setTotal(0L);
                    return responseResult;
                }

                // 获取用户ID
                User user = userMapper.findByUsername(username);
                if (user == null) {
                    responseResult.setCode(HttpStatus.NOT_FOUND.value());
                    responseResult.setMessage("用户不存在");
                    responseResult.setData(null);
                    responseResult.setTotal(0L);
                    return responseResult;
                }

                int userId = user.getUserId();

                System.out.println("查询到的user："+user);
                System.out.println("查询到的userId："+userId);

                System.out.println("该救助站是否存在："+animalMapper.hasShelter(15,10));


                if (animalMapper.hasShelter(shelterId,userId)) {
                    /*3.添加*/
                    animal.setShelterId(shelterId);
                    animalMapper.addAnimal(animal);
                    int animalId =animal.getAnimalId();

                    System.out.println("动物Id:" + animalId);

                    Animal newAnimal = animalMapper.getAnimalInfo(animalId);

                    System.out.println("添加的动物信息：" + newAnimal);

                    responseResult.setCode(HttpStatus.OK.value());
                    responseResult.setData(newAnimal);
                    responseResult.setMessage("添加成功");
                    responseResult.setTotal(1L);
                } else {
                    responseResult.setCode(HttpStatus.NOT_FOUND.value());
                    responseResult.setData(null);
                    responseResult.setMessage("没有该救助站，请检查请求参数是否正确");
                    responseResult.setTotal(0L);
                }
            } else {
                responseResult.setCode(HttpStatus.BAD_REQUEST.value());
                responseResult.setData(null);
                responseResult.setMessage("动物信息为空无法添加");
                responseResult.setTotal(0L);
            }

        } catch (Exception e){
            responseResult.setCode(500);
            responseResult.setMessage(e.getMessage());
            responseResult.setData(null);
            responseResult.setTotal(0L);
        }

        return responseResult;
    }

    /**
     * 更新修改动物信息
     * @return responseResult
     */
    public ResponseResult updateAnimalInfo(Animal animal) {
        ResponseResult responseResult = new ResponseResult();

        System.out.println(animal);
        System.out.println(animalMapper.getAnimalInfo(animal.getAnimalId()));

        try {
            Animal hasAnimal = animalMapper.getAnimalInfo(animal.getAnimalId());
            if (hasAnimal == null || hasAnimal.getIsAdoptable() == -1) {

                System.out.println("没有该动物信息");

                responseResult.setCode(HttpStatus.BAD_REQUEST.value());
                responseResult.setMessage("流浪动物信息不存在");
                responseResult.setData(null);
                responseResult.setTotal(0L);
            } else {
                System.out.println("测试点1");
                if (animalMapper.updateAnimalInfo(animal) == 1) {
                    System.out.println("测试点2");

                    Animal newAnimal = animalMapper.getAnimalInfo(animal.getAnimalId());

                    System.out.println("修改后的动物信息："+newAnimal);

                    responseResult.setCode(HttpStatus.OK.value());
                    responseResult.setMessage("修改成功");
                    responseResult.setData(newAnimal);
                    responseResult.setTotal(1L);
                } else {
                    responseResult.setCode(HttpStatus.NOT_FOUND.value());
                    responseResult.setMessage("修改失败");
                    responseResult.setData(null);
                    responseResult.setTotal(1L);
                }
            }
        } catch (Exception e) {
            responseResult.setCode(500);
            responseResult.setMessage(e.getMessage());
            responseResult.setData(null);
            responseResult.setTotal(0L);
        }

        return responseResult;

    }

    public ResponseResult deleteAnimal(int animalId) {
        ResponseResult responseResult = new ResponseResult();
        try {
            if (animalMapper.getAnimalInfo(animalId) == null){
                System.out.println("没有该动物信息");

                responseResult.setCode(HttpStatus.BAD_REQUEST.value());
                responseResult.setMessage("流浪动物信息不存在");
                responseResult.setData(null);
                responseResult.setTotal(0L);
            } else {
                System.out.println("测试点1");
                if (animalMapper.deleteAnimal(animalId) == 1) {
                    System.out.println("测试点2");
                    responseResult.setCode(HttpStatus.OK.value());
                    responseResult.setMessage("删除成功");
                    responseResult.setData(null);
                    responseResult.setTotal(0L);
                } else {
                    responseResult.setCode(HttpStatus.NOT_FOUND.value());
                    responseResult.setMessage("删除失败");
                    responseResult.setData(null);
                    responseResult.setTotal(1L);
                }
            }
        } catch (Exception e) {
            responseResult.setCode(500);
            responseResult.setMessage(e.getMessage());
            responseResult.setData(null);
            responseResult.setTotal(0L);
        }

        return responseResult;
    }

    public ResponseResult uploadAnimalPhoto(MultipartFile file, int animalId) {
        ResponseResult responseResult = new ResponseResult();
        try {
            if (animalMapper.getAnimalInfo(animalId) == null) {
                responseResult.setCode(HttpStatus.BAD_REQUEST.value());
                responseResult.setMessage("动物不存在");
                responseResult.setData(null);
                responseResult.setTotal(0L);
                return responseResult;
            }
            if (!file.isEmpty()){
                System.out.println(file);
                System.out.println(file.getSize());
                System.out.println(file.getOriginalFilename());
                System.out.println(animalId);

                String photoUrl = AliOSSUtils.upload(file);
                System.out.println(photoUrl);

                int success = animalMapper.uploadAnimalPhoto(animalId,photoUrl);

                if (success > 0){
                    responseResult.setCode(HttpStatus.OK.value());
                    responseResult.setMessage("上传成功");
                    responseResult.setData(null);
                    responseResult.setTotal(0L);
                }else {
                    responseResult.setCode(HttpStatus.NOT_FOUND.value());
                    responseResult.setMessage("上传失败");
                    responseResult.setData(null);
                    responseResult.setTotal(0L);
                }
            } else {
                responseResult.setCode(HttpStatus.BAD_REQUEST.value());
                responseResult.setMessage("上传的图片内容为空");
                responseResult.setData(null);
                responseResult.setTotal(0L);
            }
        } catch (IOException e) {
            responseResult.setCode(500);
            responseResult.setMessage(e.getMessage());
            responseResult.setData(null);
            responseResult.setTotal(0L);
            throw new RuntimeException(e);
        }
        return responseResult;
    }

    public ResponseResult getAnimalTotalNum() {
        ResponseResult responseResult = new ResponseResult();
        try {
            int animalTotalNum = animalMapper.getAnimalTotalNum();

            Map<String, Object> result = new HashMap<>();
            result.put("animalTotalNum",animalTotalNum);
            responseResult.setCode(HttpStatus.OK.value());
            responseResult.setMessage("成功获取流浪动物的总数量");
            responseResult.setData(result);
            responseResult.setTotal(null);
        } catch (Exception e) {
            responseResult.setCode(500);
            responseResult.setMessage(e.getMessage());
            responseResult.setData(null);
            responseResult.setTotal(0L);
        }
        return responseResult;
    }
}
