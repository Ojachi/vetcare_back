package com.vetcare_back.config;

import com.vetcare_back.entity.Breed;
import com.vetcare_back.entity.Species;
import com.vetcare_back.repository.BreedRepository;
import com.vetcare_back.repository.SpeciesRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpeciesDataInitializer {

    @Bean
    CommandLineRunner initSpeciesData(SpeciesRepository speciesRepository, BreedRepository breedRepository) {
        return args -> {
            if (speciesRepository.count() == 0) {
                // Perros
                Species dog = speciesRepository.save(Species.builder().name("Perro").active(true).build());
                breedRepository.save(Breed.builder().name("Criollo").species(dog).active(true).build());
                breedRepository.save(Breed.builder().name("Labrador Retriever").species(dog).active(true).build());
                breedRepository.save(Breed.builder().name("Golden Retriever").species(dog).active(true).build());
                breedRepository.save(Breed.builder().name("French Bulldog").species(dog).active(true).build());
                breedRepository.save(Breed.builder().name("Bulldog Inglés").species(dog).active(true).build());
                breedRepository.save(Breed.builder().name("Pug").species(dog).active(true).build());
                breedRepository.save(Breed.builder().name("Beagle").species(dog).active(true).build());
                breedRepository.save(Breed.builder().name("Pastor Alemán").species(dog).active(true).build());
                breedRepository.save(Breed.builder().name("Husky Siberiano").species(dog).active(true).build());
                breedRepository.save(Breed.builder().name("Pitbull").species(dog).active(true).build());
                breedRepository.save(Breed.builder().name("American Bully").species(dog).active(true).build());
                breedRepository.save(Breed.builder().name("Shih Tzu").species(dog).active(true).build());
                breedRepository.save(Breed.builder().name("Yorkshire Terrier").species(dog).active(true).build());
                breedRepository.save(Breed.builder().name("Schnauzer Miniatura").species(dog).active(true).build());
                breedRepository.save(Breed.builder().name("Border Collie").species(dog).active(true).build());

                // Gatos
                Species cat = speciesRepository.save(Species.builder().name("Gato").active(true).build());
                breedRepository.save(Breed.builder().name("Doméstico Común").species(cat).active(true).build());
                breedRepository.save(Breed.builder().name("Siamés").species(cat).active(true).build());
                breedRepository.save(Breed.builder().name("Persa").species(cat).active(true).build());
                breedRepository.save(Breed.builder().name("Maine Coon").species(cat).active(true).build());
                breedRepository.save(Breed.builder().name("Bengalí").species(cat).active(true).build());
                breedRepository.save(Breed.builder().name("British Shorthair").species(cat).active(true).build());
                breedRepository.save(Breed.builder().name("Angora Turco").species(cat).active(true).build());
                breedRepository.save(Breed.builder().name("Ragdoll").species(cat).active(true).build());
                breedRepository.save(Breed.builder().name("Sphynx").species(cat).active(true).build());

                // Conejos
                Species rabbit = speciesRepository.save(Species.builder().name("Conejo").active(true).build());
                breedRepository.save(Breed.builder().name("Enano").species(rabbit).active(true).build());
                breedRepository.save(Breed.builder().name("Cabeza de León").species(rabbit).active(true).build());
                breedRepository.save(Breed.builder().name("Belier").species(rabbit).active(true).build());
                breedRepository.save(Breed.builder().name("Rex").species(rabbit).active(true).build());
                breedRepository.save(Breed.builder().name("Holandés").species(rabbit).active(true).build());

                // Aves
                Species bird = speciesRepository.save(Species.builder().name("Ave").active(true).build());
                breedRepository.save(Breed.builder().name("Perico Australiano").species(bird).active(true).build());
                breedRepository.save(Breed.builder().name("Loro Amazónico").species(bird).active(true).build());
                breedRepository.save(Breed.builder().name("Canario").species(bird).active(true).build());
                breedRepository.save(Breed.builder().name("Cacatúa").species(bird).active(true).build());
                breedRepository.save(Breed.builder().name("Gallina").species(bird).active(true).build());
                breedRepository.save(Breed.builder().name("Ninfa (Cockatiel)").species(bird).active(true).build());
                breedRepository.save(Breed.builder().name("Agapornis").species(bird).active(true).build());
                breedRepository.save(Breed.builder().name("Guacamaya").species(bird).active(true).build());

                // Roedores
                Species rodent = speciesRepository.save(Species.builder().name("Roedor").active(true).build());
                breedRepository.save(Breed.builder().name("Cuy").species(rodent).active(true).build());
                breedRepository.save(Breed.builder().name("Hamster Sirio").species(rodent).active(true).build());
                breedRepository.save(Breed.builder().name("Hamster Enano").species(rodent).active(true).build());
                breedRepository.save(Breed.builder().name("Chinchilla").species(rodent).active(true).build());
                breedRepository.save(Breed.builder().name("Rata Doméstica").species(rodent).active(true).build());
                breedRepository.save(Breed.builder().name("Ratón Doméstico").species(rodent).active(true).build());

                // Reptiles
                Species reptile = speciesRepository.save(Species.builder().name("Reptil").active(true).build());
                breedRepository.save(Breed.builder().name("Iguana Verde").species(reptile).active(true).build());
                breedRepository.save(Breed.builder().name("Iguana Azul").species(reptile).active(true).build());
                breedRepository.save(Breed.builder().name("Tortuga de Orejas Rojas").species(reptile).active(true).build());
                breedRepository.save(Breed.builder().name("Tortuga Morrocoy").species(reptile).active(true).build());
                breedRepository.save(Breed.builder().name("Tortuga de Caja").species(reptile).active(true).build());
                breedRepository.save(Breed.builder().name("Gecko Leopardo").species(reptile).active(true).build());
                breedRepository.save(Breed.builder().name("Gecko Crestado").species(reptile).active(true).build());
                breedRepository.save(Breed.builder().name("Pitón Real").species(reptile).active(true).build());
                breedRepository.save(Breed.builder().name("Boa Imperator").species(reptile).active(true).build());
                breedRepository.save(Breed.builder().name("Serpiente del Maíz").species(reptile).active(true).build());
                breedRepository.save(Breed.builder().name("Dragón Barbudo").species(reptile).active(true).build());
                breedRepository.save(Breed.builder().name("Uromastyx").species(reptile).active(true).build());

                // Anfibios
                Species amphibian = speciesRepository.save(Species.builder().name("Anfibio").active(true).build());
                breedRepository.save(Breed.builder().name("Rana Arborícola Verde").species(amphibian).active(true).build());
                breedRepository.save(Breed.builder().name("Rana Ojos Rojos").species(amphibian).active(true).build());
                breedRepository.save(Breed.builder().name("Rana Pacman").species(amphibian).active(true).build());
                breedRepository.save(Breed.builder().name("Ajolote").species(amphibian).active(true).build());

                // Hurones
                Species ferret = speciesRepository.save(Species.builder().name("Hurón").active(true).build());
                breedRepository.save(Breed.builder().name("Hurón Doméstico").species(ferret).active(true).build());

                System.out.println("✅ Species and breeds initialized successfully");
            }
        };
    }
}
