package com.example.Bankregistration.Service.Impl;

import com.example.Bankregistration.Entity.Admin;
import com.example.Bankregistration.Entity.ApiPartner;
import com.example.Bankregistration.Exception.InvalidCredentialsException;
import com.example.Bankregistration.Exception.UserNotFoundException;
import com.example.Bankregistration.JWT.JwtGenerator;
import com.example.Bankregistration.Model.Request.AdminRequest;
import com.example.Bankregistration.RegUtils.Utils;
import com.example.Bankregistration.Repository.AdminRepository;
import com.example.Bankregistration.Service.AdminService;
import com.example.Bankregistration.Service.ApiService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired
    private ApiService apiService;

    @Override
    public boolean authorizeRequest(String secret_key) {
        if (secret_key.matches(Utils.SECRET_CODE)) {
            log.info("Request authorized.");
            return true;
        } else {
            log.info("Can't authorize request.");
            return false;
        }
    }

    @Override
    public HashMap<Integer,String> onboardAdmin(AdminRequest adminRequest) {
        log.info("Request received to onboard : " + adminRequest);
        Admin admin = adminRepository.findById(adminRequest.getId()).orElse(null);
        HashMap<Integer,String> map = new HashMap<>();
        if (!(admin==null)) {
            map.put(1,"Admin already present with this given id.");
            log.info("Admin already present with this given id.");
            return map;
        } else {
            map.clear();
            Admin admin2 = adminRepository.findByName(adminRequest.getUser_name()).orElse(null);
            if (admin2==null) {
                Admin newAdmin = new Admin();
                newAdmin.setUserId(adminRequest.getId());
                newAdmin.setName(adminRequest.getUser_name());
                newAdmin.setPassword(adminRequest.getPassword());

                adminRepository.save(newAdmin);
                map.put(0,"Admin onboarded successfully.");
                log.info("Admin onboarded successfully.");
                return map;
            } else {
                map.clear();
                log.info("Username already exists.");
                map.put(-1,"Username already exists.");
                return map;
            }
        }
    }

    @Override
    public boolean validateRequest(AdminRequest adminRequest) {
        Admin admin = adminRepository.findById(adminRequest.getId()).orElse(null);
        if(admin==null){
            throw new UserNotFoundException("Admin doesn't exist.");
        }else{
            if(!(admin.getUserId().matches(adminRequest.getId()))
                    || !(admin.getPassword().matches(adminRequest.getPassword()))
                        || !(admin.getName().matches(adminRequest.getUser_name()))){
                throw new InvalidCredentialsException("Invalid credentials");
            }else{
                return true;
            }
        }
    }

    @Override
    public Optional<Admin> findAdminById(String id) {
        return adminRepository.findById(id);
    }

    @Override
    public List<String> getAllAdmins() {
        return adminRepository.getAllAdmins();
    }

    @Override
    public Object generateToken(AdminRequest adminRequest) {
        return jwtGenerator.generateToken(adminRequest);
    }

    @Override
    public String getTokenFromAuthorization(HttpServletRequest httpServletRequest) {
        return jwtGenerator.getTokenFromAuthorization(httpServletRequest);
    }

    @Override
    public boolean isTokenExpired(String auth) {
        return jwtGenerator.isTokenExpired(auth);
    }

    @Override
    public Claims getDataFromToken(String auth) {
        return jwtGenerator.getDataFromToken(auth);
    }

    @Override
    public boolean validateToken(String token) {
        return jwtGenerator.validateToken(token);
    }

    @Override
    public List<String> getAllApiUsersOfAnAdmin(String id) {
        List<String> apiList = apiService.getAllApiUsersOfAdmin(id);
        return apiList;
    }

    @Override
    public ApiPartner findApiUserByName(String name) {
        ApiPartner apiPartner = apiService.findApiUserByName(name);
        return apiPartner;
    }

    @Override
    public void removeAdmin(Admin admin) {
        adminRepository.delete(admin);
    }

    @Override
    public void changeDetailsOfApiUser(ApiPartner apiPartner) {
        apiService.changeDetailsForDeletedAdmin(apiPartner);
    }
}
