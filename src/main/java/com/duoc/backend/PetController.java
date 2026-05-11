package com.duoc.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class PetController {

    @Autowired
    private PetRepository petRepository;

    private static final String MESSAGE = "message";

    @PostMapping("/pets")
    public ResponseEntity<?> createPet(@RequestBody Pet pet) {
        try {
            petRepository.save(pet);
            return ResponseEntity.status(HttpStatus.CREATED).body(pet);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put(MESSAGE, "Error al registrar mascota: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/pets")
    public ResponseEntity<Iterable<Pet>> getAllPets() {
        try {
            Iterable<Pet> pets = petRepository.findAll();
            return ResponseEntity.ok(pets);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/pets/available")
    public ResponseEntity<List<Pet>> getAvailablePets() {
        try {
            List<Pet> pets = petRepository.findByStatus("available");
            return ResponseEntity.ok(pets);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/pets/{id}")
    public ResponseEntity<Pet> getPetById(@PathVariable Integer id) {
        try {
            Optional<Pet> pet = petRepository.findById(id);
            if (pet.isPresent()) {
                return ResponseEntity.ok(pet.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/pets/{id}")
    public ResponseEntity<?> updatePet(
            @PathVariable Integer id,
            @RequestBody Pet petDetails) {

        try {

            Optional<Pet> pet = petRepository.findById(id);

            if (pet.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Pet existingPet = pet.get();

            updatePetFields(existingPet, petDetails);

            petRepository.save(existingPet);

            return ResponseEntity.ok(existingPet);

        } catch (Exception e) {

            Map<String, String> error = new HashMap<>();
            error.put(MESSAGE,
                    "Error al actualizar mascota: " + e.getMessage());

            return ResponseEntity.badRequest().body(error);
        }
    }

    private void updatePetFields(Pet existingPet, Pet petDetails) {

        if (petDetails.getName() != null) {
            existingPet.setName(petDetails.getName());
        }

        if (petDetails.getSpecies() != null) {
            existingPet.setSpecies(petDetails.getSpecies());
        }

        if (petDetails.getBreed() != null) {
            existingPet.setBreed(petDetails.getBreed());
        }

        if (petDetails.getAge() != null) {
            existingPet.setAge(petDetails.getAge());
        }

        if (petDetails.getGender() != null) {
            existingPet.setGender(petDetails.getGender());
        }

        if (petDetails.getLocation() != null) {
            existingPet.setLocation(petDetails.getLocation());
        }

        if (petDetails.getPhotos() != null) {
            existingPet.setPhotos(petDetails.getPhotos());
        }

        if (petDetails.getStatus() != null) {
            existingPet.setStatus(petDetails.getStatus());
        }
    }

    @DeleteMapping("/pets/{id}")
    public ResponseEntity<?> deletePet(@PathVariable Integer id) {
        try {
            Optional<Pet> pet = petRepository.findById(id);
            if (pet.isPresent()) {
                petRepository.deleteById(id);
                Map<String, String> response = new HashMap<>();
                response.put(MESSAGE, "Mascota eliminada exitosamente");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put(MESSAGE, "Error al eliminar mascota: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/pets/search")
    public ResponseEntity<List<Pet>> searchPets(
            @RequestParam(required = false) String species,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer age,
            @RequestParam(defaultValue = "available") String status) {

        try {

            List<Pet> pets = findPetsByFilters(
                    species,
                    gender,
                    location,
                    age,
                    status
            );

            return ResponseEntity.ok(pets);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private List<Pet> findPetsByFilters(
        String species,
        String gender,
        String location,
        Integer age,
        String status) {

        boolean hasSpecies = species != null;
        boolean hasGender = gender != null;
        boolean hasLocation = location != null;
        boolean hasAge = age != null;

        if (hasSpecies && hasGender && hasLocation && hasAge) {
            return petRepository
                    .findBySpeciesAndGenderAndLocationAndAgeAndStatus(
                            species, gender, location, age, status);
        }

        if (hasSpecies && hasGender && hasLocation) {
            return petRepository
                    .findBySpeciesAndGenderAndLocationAndStatus(
                            species, gender, location, status);
        }

        if (hasSpecies && hasGender && hasAge) {
            return petRepository
                    .findBySpeciesAndGenderAndAgeAndStatus(
                            species, gender, age, status);
        }

        if (hasSpecies && hasLocation && hasAge) {
            return petRepository
                    .findBySpeciesAndLocationAndAgeAndStatus(
                            species, location, age, status);
        }

        if (hasGender && hasLocation && hasAge) {
            return petRepository
                    .findByGenderAndLocationAndAgeAndStatus(
                            gender, location, age, status);
        }

        if (hasSpecies && hasGender) {
            return petRepository
                    .findBySpeciesAndGenderAndStatus(
                            species, gender, status);
        }

        if (hasSpecies && hasLocation) {
            return petRepository
                    .findBySpeciesAndLocationAndStatus(
                            species, location, status);
        }

        if (hasSpecies && hasAge) {
            return petRepository
                    .findBySpeciesAndAgeAndStatus(
                            species, age, status);
        }

        if (hasGender && hasLocation) {
            return petRepository
                    .findByGenderAndLocationAndStatus(
                            gender, location, status);
        }

        if (hasGender && hasAge) {
            return petRepository
                    .findByGenderAndAgeAndStatus(
                            gender, age, status);
        }

        if (hasLocation && hasAge) {
            return petRepository
                    .findByLocationAndAgeAndStatus(
                            location, age, status);
        }

        if (hasSpecies) {
            return petRepository.findBySpeciesAndStatus(
                    species, status);
        }

        if (hasGender) {
            return petRepository.findByGenderAndStatus(
                    gender, status);
        }

        if (hasLocation) {
            return petRepository.findByLocationAndStatus(
                    location, status);
        }

        if (hasAge) {
            return petRepository.findByAgeAndStatus(
                    age, status);
        }

        return petRepository.findByStatus(status);
    }
}
