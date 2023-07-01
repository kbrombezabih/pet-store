package pet.store.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreData.PetStoreCustomer;
import pet.store.controller.model.PetStoreData.PetStoreEmployee;
import pet.store.service.PetStoreService;


@RestController
@RequestMapping("/pet_store")
@Slf4j
public class PetStoreController {

  @Autowired
  private PetStoreService petStoreService;

  @PostMapping("/pet_store")
  @ResponseStatus(code = HttpStatus.CREATED)
  public PetStoreData insertPetStore(@RequestBody  PetStoreData petStoreData) {
    log.info("/Creating pet store {}", petStoreData);
    return petStoreService.savePetStore(petStoreData);
  }

  @PutMapping("/pet_store/{petStoreId}")
  public PetStoreData updatePetStore(@PathVariable Long petStoreId,
      @RequestBody PetStoreData petStoreData) {

    petStoreData.setPetStoreId(petStoreId);
    log.info("/Updating store information {}.", petStoreData);
    return petStoreService.savePetStore(petStoreData);
  }
  
  @PostMapping("/{petStoreId}/employee")
  @ResponseStatus(code = HttpStatus.CREATED)
  public PetStoreEmployee addPetStoreEmployee(@PathVariable Long petStoreId, 
      @RequestBody PetStoreEmployee petStoreEmployee) {
    log.info("/Adding a new employee to pet store with ID={} {}", petStoreId, petStoreEmployee);
    return petStoreService.saveEmployee(petStoreId, petStoreEmployee);
  }
  
  @PostMapping("/{petStoreId}/customer")
  @ResponseStatus(code = HttpStatus.CREATED)
  public PetStoreCustomer addPetStoreCustomer(@PathVariable Long petStoreId, 
      @RequestBody PetStoreCustomer petStoreCustomer) {
    log.info("/Adding a new customer to pet store with ID={} {}", petStoreId, petStoreCustomer);
    return petStoreService.saveCustomer(petStoreId, petStoreCustomer);
  }
  
  @GetMapping("/pet_store")
  public List<PetStoreData> retrieveAllPetStores(){
    return petStoreService.retrieveAllPetStores();
  }
  
  
  @GetMapping("/{petStoreId}")
  public PetStoreData retrievePetStoreById(@PathVariable Long petStoreId) {
    return petStoreService.retrievePetStoreById(petStoreId);
  }
  
  
  @DeleteMapping("/{petStoreId}")
  
  public Map<String, String> deletePetStore(@PathVariable Long petStoreId){
    log.info("Deleting pet store {}", petStoreId);
    petStoreService.deletePetStoreById(petStoreId);
    return Map.of("message", "Pet store with ID = " + petStoreId + " was successfully deleted.");
    
  }

}
