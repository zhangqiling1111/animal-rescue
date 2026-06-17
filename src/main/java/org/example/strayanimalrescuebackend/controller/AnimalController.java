package org.example.strayanimalrescuebackend.controller;


import org.example.strayanimalrescuebackend.model.Animal;
import org.example.strayanimalrescuebackend.model.ResponseResult;
import org.example.strayanimalrescuebackend.model.Shelter;
import org.example.strayanimalrescuebackend.service.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/animals")
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    /**
     * 1.获取动物信息列表
     * @return List<animal>
     */
    @GetMapping("/getAnimals")
    public ResponseResult getAnimals(@RequestParam int page,
                                     @RequestParam int limit){
        return animalService.getAnimals(page, limit);
    }

    /**
     * 2.根据关键字搜素流浪动物
     * @return result
     */
    @GetMapping("/search")
    public ResponseResult searchAnimals(
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String healthStatus,  // 改为非必需
            @RequestParam(required = false,defaultValue = "-1") int isAdoptable,  // 建议改为Integer包装类型
            @RequestParam(required = false) String shelterName
    ){
        return animalService.searchAnimals(breed,area,healthStatus,isAdoptable,shelterName);
    }

    /**
     * 3.根据动物Id查询动物信息
     * @param animalId
     * @return ResponseResult
     */
    @GetMapping("/getAnimalInfo")
    public ResponseResult getAnimalInfo(@RequestParam int animalId){
        return animalService.getAnimalInfo(animalId);
    }

    /**
     * 4.根据救助站Id查询救助站里的所有动物信息
     * @param shelterId
     * @return ResponseResult
     */
    @GetMapping("/getAnimalsInShelter")
    public ResponseResult getAnimalsInShelter(@RequestParam int shelterId){
        return animalService.getAnimalsInShelter(shelterId);
    }

    /**
     * 添加新的动物信息
     * @param animal
     * @return
     */
    @PostMapping("/addAnimal")
    public ResponseResult addNewAnimal(
            @RequestHeader("token") String token,
            @RequestBody Animal animal,
            @RequestParam int shelterId
    ){
        return animalService.addAnimal(animal, shelterId,token);
    }

    /**
     * 上传动物照片
     * @param file
     * @param animalId
     * @return
     */
    @PostMapping(value = "/uploadAnimalPhoto")
    public ResponseResult uploadAnimalPhoto(
            @RequestParam("animalPhoto") MultipartFile file,
            @RequestParam int animalId
    ){
        return animalService.uploadAnimalPhoto(file,animalId);
    }

    /**
     * 修改动物信息
     * @param animal
     * @return Animal
     */
    @PutMapping("/updateAnimalInfo")
    public ResponseResult updateAnimalInfo(@RequestBody Animal animal){

        if (animal.getIsAdoptable() == null) {
            animal.setIsAdoptable(-1);
        }
        System.out.println(animal);
        return animalService.updateAnimalInfo(animal);
    }

    @GetMapping("/deleteAnimal" )
    public ResponseResult deleteAnimal(@RequestParam int animalId){
        return animalService.deleteAnimal(animalId);
    }


    @GetMapping("/getAnimalTotalNum" )
    public ResponseResult getAnimalTotalNum(){
        return animalService.getAnimalTotalNum();
    }






}
