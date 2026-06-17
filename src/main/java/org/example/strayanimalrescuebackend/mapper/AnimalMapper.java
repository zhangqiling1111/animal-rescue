package org.example.strayanimalrescuebackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.strayanimalrescuebackend.model.Animal;

import java.util.List;

@Mapper
public interface AnimalMapper {
    /**
     * @return List<Animal></Animal>
     */
    List<Animal> getAnimals(int offset, int limit);

    List<Animal> searchAnimals(String breed, String area, String healthStatus, int isAdoptable, String shelterName);

    Animal getAnimalInfo(int animalId);

    List<Animal> getAnimalsInShelter(int shelterId);

    boolean hasShelter(int shelterId, int userId);

    int addAnimal(Animal animal);

    int updateAnimalInfo(Animal animal);

    int deleteAnimal(int animalId);

    int uploadAnimalPhoto(int animalId, String photoUrl);

    int getAnimalTotalNum();
}
