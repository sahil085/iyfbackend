package com.iyfbackend.api.services;

import com.iyfbackend.api.domain.User;
import com.iyfbackend.api.domain.UserAttendance;
import com.iyfbackend.api.dto.UserDTO;
import com.iyfbackend.api.repository.UserAttendanceRepo;
import com.iyfbackend.api.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepo userRepo;
    private final EmailService emailService;
    private final SmsService smsService;
    private final UserAttendanceRepo userAttendanceRepo;

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
            log.info("Sending Email and SMS to user :: {} - {}", userDTO.getName(), userDTO.getContact());
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

    public String updateUser(UserDTO userDTO) {
        try {
            log.info("Update Registration details starts for user :: {} {}", userDTO.getName(), userDTO.getContact());
            User user = userRepo.findById(userDTO.getId()).orElse(null);
            boolean isContactChanged = false;
            boolean isEmailChanged = false;
            if (Objects.nonNull(user)) {
                ModelMapper modelMapper = new ModelMapper();
                LocalDateTime registeredOn = user.getRegisteredOn();
                String registrationCode = user.getRegistrationCode();
                if (!user.getContact().equals(userDTO.getContact())) {
                    isContactChanged = true;
                }
                if (!user.getEmail().equals(userDTO.getEmail())) {
                    isEmailChanged = true;
                }
                user = modelMapper.map(userDTO, User.class);
                user.setRegistrationCode(registrationCode);
                user.setRegisteredOn(registeredOn);
                userRepo.saveAndFlush(user);
                log.info("Registration details updated successfully for user :: {} {}", userDTO.getName(), userDTO.getContact());
                if (isContactChanged) {
                    log.info("Sending SMS to user :: {} - {}", userDTO.getName(), userDTO.getContact());
                    sendTextSMSToUser(user);
                }
                if (isEmailChanged) {
                    log.info("Sending Email to user :: {} - {}", userDTO.getName(), userDTO.getContact());
                    sendEmailToUser(user);
                }
                return "Registration details updated successfully";
            } else {
                return "User not found with Id " + userDTO.getId();
            }
        } catch (DataIntegrityViolationException e) {
            log.error(e.getMessage());
            return "Mobile number already exist :: " + e.getMessage();
        } catch (Exception e) {
            log.error(e.getMessage());
            return "Internal server error :: " + e.getMessage();
        }

    }

    public List<UserDTO> fetchAllUsers() {
        List<User> userList = userRepo.findAll();
        List<UserAttendance> userAttendanceList = userAttendanceRepo.findAll();
        List<UserDTO> userDTOList = new ArrayList<>();
        ModelMapper mapper = new ModelMapper();
        userList.forEach(user -> {
            UserDTO userDTO = mapper.map(user, UserDTO.class);
            if (!user.getWithBhagavadGita()) {
                userDTO.setMoneyLeftToBePaid(100 - user.getMoneyPaid());
            } else {
                userDTO.setMoneyLeftToBePaid(0);
            }
            userAttendanceList
                    .stream()
                    .filter(userAttendance -> userAttendance.getUser().getId().equals(user.getId()))
                    .findFirst()
                    .map(UserAttendance::getIsPresent)
                    .ifPresent(userDTO::setIsPresent);
            userDTOList.add(userDTO);
        });

        return userDTOList;
    }

    public String sendSmsToAllUsers(String sms) {
        try {
            List<Long> contactList = userRepo.findAllDistinctContacts();
            smsService.sendSMS(sms, StringUtils.join(contactList, ","));
            return "Sms sent to all registered users";
        } catch (Exception e) {
            log.error("Error in sending sms to all users :: {}", e.getMessage());
            return "Error in sending sms to all users :: " + e.getMessage();
        }
    }

    public UserDTO fetchById(Long userId) {
        ModelMapper modelMapper = new ModelMapper();
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found with id : " + userId);
        } else {
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            if (!user.getWithBhagavadGita()) {
                userDTO.setMoneyLeftToBePaid(100 - user.getMoneyPaid());
            } else {
                userDTO.setMoneyLeftToBePaid(0);
            }
            UserAttendance userAttendance = userAttendanceRepo.findByUser_Id(userId);
            if (userAttendance != null) {
                userDTO.setIsPresent(userAttendance.getIsPresent());
            }
            return userDTO;
        }
    }

    public String markAttendanceOfUser(Long userId, Boolean isPresent) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found with id : " + userId);
        } else {
            UserAttendance userAttendance = userAttendanceRepo.findByUser_Id(userId);
            if (userAttendance == null) {
                userAttendance = new UserAttendance();
                userAttendance.setUser(user);
            }
            userAttendance.setIsPresent(isPresent);
            userAttendanceRepo.saveAndFlush(userAttendance);
            String attendance = isPresent ? "Present" : "Absent";
            return user.getName() + " marked as : " + attendance;
        }
    }

    private void generateRegistrationCode(User user) {

        user.setRegistrationCode(RandomStringUtils.random(length, useLetters, useNumbers) + user.getId());
        userRepo.saveAndFlush(user);

    }

    private void sendTextSMSToUser(User user) throws IOException {
        String message = "Congratulations. You've successfully registered for UMANG Fest. Your Ticket ID is " + user.getRegistrationCode() + ". Show this after reaching ISKCON GHAZIABAD to get your Buffet Coupon. Kindly Join festival Whatsapp group http://tiny.cc/umang";
        smsService.sendSMS(message, user.getContact().toString());
    }

    private void sendEmailToUser(User user) throws MessagingException {
        String subject = "UMANG Fest | Registration Done Successfully | ISKCON YOUTH FORUM";
        Context context = new Context();
        context.setVariable("user", user);
        emailService.sendMailWithAttachment(user.getEmail(), subject, "registration.html", "Umang_Brochure.pdf", context);
    }

}
