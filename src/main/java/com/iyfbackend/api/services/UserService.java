package com.iyfbackend.api.services;

import com.iyfbackend.api.domain.User;
import com.iyfbackend.api.dto.UserDTO;
import com.iyfbackend.api.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepo userRepo;
    private final EmailService emailService;
    private final SmsService smsService;

    int length = 3;
    boolean useLetters = true;
    boolean useNumbers = true;

    public List<String> fetchAllVolunteers() {
        return userRepo.findAllDistinctRegisteredBy();
    }

    public String registerUser(UserDTO userDTO) {
        try {
            log.info("Registration starts for user :: {} {}", userDTO.getName(), userDTO.getContact());
            ModelMapper modelMapper = new ModelMapper();
            User user = modelMapper.map(userDTO, User.class);
            user.setRegisteredOn(LocalDateTime.now());
            userRepo.saveAndFlush(user);
            generateRegistrationCode(user);
            log.info("Registration done successfully for user :: {} {}", userDTO.getName(), userDTO.getContact());
            sendTextSMSToUser(user);
            sendEmailToUser(user);
            return "Registration done successfully";
        } catch (DataIntegrityViolationException e) {
            log.error(e.getMessage());
            return "Mobile number already exist :: " + e.getMessage();
        } catch (Exception e) {
            log.error(e.getMessage());
            return "Internal server error :: " + e.getMessage();
        }

    }

    public List<User> fetchAllUsers() {
        return userRepo.findAll();
    }

    private void generateRegistrationCode(User user) {

        user.setRegistrationCode(RandomStringUtils.random(length, useLetters, useNumbers) + user.getId());
        userRepo.saveAndFlush(user);

    }

    private void sendTextSMSToUser(User user) throws IOException {
        String message = "Congratulations. You've successfully registered for UMANG Fest. Your Ticket ID is " + user.getRegistrationCode() + ". Show this after reaching ISKCON GHAZIABAD to get your Buffet Coupon.";
        smsService.sendSMS(message, user.getContact().toString());
    }

    private void sendEmailToUser(User user) throws MessagingException {
        String subject = "UMANG Fest | Registration Done Successfully | ISKCON YOUTH FORUM";
        Context context = new Context();
        context.setVariable("user", user);
        emailService.sendMail(user.getEmail(), subject, "registration.html", context);
    }

    @Scheduled(cron = "0 0/20 * 1/1 * ?")
    public void schedulerToKeepSystemAlive() {
        log.info("Executing scheduler to keep system alive");
    }

}
