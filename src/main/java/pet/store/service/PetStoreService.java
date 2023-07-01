package pet.store.service;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreData.PetStoreCustomer;
import pet.store.controller.model.PetStoreData.PetStoreEmployee;
import pet.store.dao.CustomerDao;
import pet.store.dao.EmployeeDao;
import pet.store.dao.PetStoreDao;
import pet.store.entity.Customer;
import pet.store.entity.Employee;
import pet.store.entity.PetStore;

@Service
public class PetStoreService {

  @Autowired
  private PetStoreDao petStoreDao;
  
  @Autowired
  private EmployeeDao employeeDao;
  
  @Autowired
  private CustomerDao customerDao;
  @Transactional(readOnly = false)
  
  public PetStoreData savePetStore(PetStoreData petStoreData) {

    Long petStoreId = petStoreData.getPetStoreId();
    PetStore petStore = findOrCreatePetStore(petStoreId);
    copyPetStoreFields(petStore, petStoreData);
    return new PetStoreData(petStoreDao.save(petStore));
  }

  private void copyPetStoreFields(PetStore petStore, PetStoreData petStoreData) {
    
    petStore.setPetStoreId(petStoreData.getPetStoreId());
    petStore.setPetStoreName(petStoreData.getPetStoreName());
    petStore.setPetStoreAddress(petStoreData.getPetStoreAddress());
    petStore.setPetStoreCity(petStoreData.getPetStoreCity());
    petStore.setPetStoreState(petStoreData.getPetStoreState());
    petStore.setPetStoreZip(petStoreData.getPetStoreZip());
    petStore.setPetStorePhone(petStoreData.getPetStorePhone());
  }

  private PetStore findOrCreatePetStore(Long petStoreId) {

    //PetStore petStore;

    if (Objects.isNull(petStoreId)) {
      return new PetStore();
    } else {
      return findPetStoreById(petStoreId);
    }

  }

  private PetStore findPetStoreById(Long petStoreId) {

    return petStoreDao.findById(petStoreId).orElseThrow(() -> new NoSuchElementException(
        "Pet store with ID=" + petStoreId + " was not found."));
  }
  
  @Transactional(readOnly = false)
  public PetStoreEmployee saveEmployee(Long petStoreId, PetStoreEmployee petStoreEmployee) {
    
    PetStore petStore = findPetStoreById(petStoreId);
    Long employeeId = petStoreEmployee.getEmployeeId();
    Employee employee = findOrCreateEmployee(petStoreId, employeeId);
    
    copyEmployeeFields(employee, petStoreEmployee);
    employee.setPetStore(petStore);
    petStore.getEmployees().add(employee);
    Employee dbEmployee = employeeDao.save(employee);
    
    return new PetStoreEmployee(dbEmployee);
    
  }

  private void copyEmployeeFields(Employee employee, PetStoreEmployee petStoreEmployee) {
    employee.setEmployeeId(petStoreEmployee.getEmployeeId());
    employee.setEmployeeFirstName(petStoreEmployee.getEmployeeFirstName());
    employee.setEmployeeLastName(petStoreEmployee.getEmployeeLastName());
    employee.setEmployeePhone(petStoreEmployee.getEmployeePhone());
    employee.setEmployeeJobTitle(petStoreEmployee.getEmployeeJobTitle());
  }

  private Employee findOrCreateEmployee(Long petStoreId, Long employeeId) {
    
    Employee employee;
    
    if(Objects.isNull(employeeId)) {
      employee = new Employee();
    }
    else {
      employee = findEmployeeById(petStoreId, employeeId);
    }
    
    return employee;
  }

  private Employee findEmployeeById(Long petStoreId, Long employeeId) {
    Employee employee = employeeDao.findById(employeeId)
        .orElseThrow(() -> new NoSuchElementException("Employee with ID=" + employeeId + " was not found."));
    
    if(petStoreId.equals(employee.getPetStore().getPetStoreId())) {
      return employee;
    }
    else {
      throw new IllegalArgumentException("Employee with ID=" + employeeId + 
          " does not match the pet store id of " + petStoreId);
    }
  }
  
  @Transactional
  public List<PetStoreData> retrieveAllPetStores(){
    
    List<PetStore> petStores = petStoreDao.findAll();
    List<PetStoreData> result = new LinkedList<>();
    
    for(PetStore petStore : petStores) {
      PetStoreData psd = new PetStoreData(petStore);
      
      psd.getCustomers().clear();
      psd.getEmployees().clear();
      
      result.add(psd);
    }
    return result;
  }
  @Transactional(readOnly = true)
  public PetStoreData retrievePetStoreById(Long petStoreId){
    
    PetStoreData psd = new PetStoreData(findPetStoreById(petStoreId));
    
    return psd;
  }

  public PetStoreCustomer saveCustomer(Long petStoreId, PetStoreCustomer petStoreCustomer) {
    PetStore petStore = findPetStoreById(petStoreId);
    Long customerId = petStoreCustomer.getCustomerId();
    Customer customer = findOrCreateCustomer(petStoreId, customerId);
    
    copyCustomerFields(customer, petStoreCustomer, petStore);
    
    customer.getPetStores().add(petStore);
    Customer psc = customerDao.save(customer);
    psc.getPetStores().add(petStore);
    
    return new PetStoreCustomer(psc);
    
  }

  private void copyCustomerFields(Customer customer, PetStoreCustomer petStoreCustomer,
    PetStore petStore) {
    
    customer.setCustomerId(petStoreCustomer.getCustomerId());
    customer.setCustomerFirstName(petStoreCustomer.getCustomerFirstName());
    customer.setCustomerLastName(petStoreCustomer.getCustomerLastName());
    customer.setCustomerEmail(petStoreCustomer.getCustomerEmail()); 
    petStore.getCustomers().add(customer);

  }

  private Customer findOrCreateCustomer(Long petStoreId, Long customerId) {
    Customer customer;
    
    if(Objects.isNull(customerId)) {
      customer = new Customer();
    }
    else {
      customer = findCustomerById(petStoreId, customerId);
    }
    
    return customer;
  }
  
  private Customer findCustomerById(Long petStoreId, Long customerId) {
    Customer customer = customerDao.findById(customerId)
        .orElseThrow(() -> new NoSuchElementException("Employee with ID=" + customerId + " was not found."));
    
    if(petStoreId.equals(((PetStoreData) customer.getPetStores()).getPetStoreId())) {
      return customer;
    }
    else {
      throw new IllegalArgumentException("Employee with ID=" + customerId + 
          " does not match the pet store id of " + petStoreId);
    }
  }

  public void deletePetStoreById(Long petStoreId) {
    PetStore petStore = findPetStoreById(petStoreId);
    petStoreDao.delete(petStore);
  }

}
