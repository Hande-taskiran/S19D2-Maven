package com.workintech.s18d4.controller;

import com.workintech.s18d4.dto.AccountResponse;
import com.workintech.s18d4.dto.CustomerResponse;
import com.workintech.s18d4.entity.Account;
import com.workintech.s18d4.entity.Customer;
import com.workintech.s18d4.service.AccountService;
import com.workintech.s18d4.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/account")
public class AccountController {
    private AccountService accountService;
    private CustomerService customerService;

    @GetMapping
    public List<Account> findAll(){
        return accountService.findAll();
    }
    @GetMapping("/{id}")
    public Account getById(@PathVariable long id){
        return accountService.find(id);
    }

    @PostMapping("/{customerId}")
    public AccountResponse save(@PathVariable("customerId") long customerId, @RequestBody Account account){
        Customer customer = customerService.find(customerId);
        if (customer != null){
            customer.getAccounts().add(account);
            account.setCustomer(customer);
            accountService.save(account);
        } else {
            throw new RuntimeException("no customer");
        }
        return new AccountResponse(account.getId(), account.getAccountName(), account.getMoneyAmount(), new CustomerResponse(customer.getId(), customer.getEmail(), customer.getSalary()));
    }

    @PutMapping("/{customerId}")
    public AccountResponse update(@RequestBody Account account, @PathVariable long customerId){
        Customer customer = customerService.find(customerId);
        Account updated = null;
        for(Account account1: customer.getAccounts()) {
            if(account.getId() == account1.getId()){
                updated = account1;
            }
        }
        if (updated == null){
            throw new RuntimeException("account("+account.getId()+") not found for this customer: "+customerId);
        }
        int indexOfUpdated = customer.getAccounts().indexOf(updated);
        customer.getAccounts().set(indexOfUpdated, account);
        account.setCustomer(customer);
        accountService.save(account);
        return new AccountResponse(account.getId(), account.getAccountName(), account.getMoneyAmount(), new CustomerResponse(customerId, customer.getEmail(), customer.getSalary()));

    }

    @DeleteMapping("/{id}")
    public AccountResponse remove(@PathVariable long id){
        Account account = accountService.find(id);
        if (account==null){
            throw new RuntimeException("NO ACCOUNT FOUND");
        }
        accountService.delete(id);
        return new AccountResponse(account.getId(), account.getAccountName(), account.getMoneyAmount(), new CustomerResponse(account.getCustomer().getId(), account.getCustomer().getEmail(), account.getCustomer().getSalary()));

    }
}
