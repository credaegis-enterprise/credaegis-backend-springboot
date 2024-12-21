package com.credaegis.backend.controller;

import com.credaegis.backend.constant.Constants;
import com.credaegis.backend.configuration.security.principal.CustomUser;
import com.credaegis.backend.entity.Notification;
import com.credaegis.backend.http.request.AccountInfoModificationRequest;
import com.credaegis.backend.http.request.PasswordChangeRequest;
import com.credaegis.backend.http.response.api.CustomApiResponse;
import com.credaegis.backend.http.response.custom.AccountInfoResponse;
import com.credaegis.backend.service.AccountService;
import dev.samstevens.totp.exceptions.QrGenerationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = Constants.ROUTEV1 + "/account")
@AllArgsConstructor
@Slf4j
public class AccountController {


    private final AccountService accountService;



    @GetMapping(path = "/notifications")
    public ResponseEntity<CustomApiResponse<List<Notification>>> getNotifications(@AuthenticationPrincipal CustomUser customUser) {
        List<Notification> notifications = accountService.getNotifications(customUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(
                new CustomApiResponse<>(notifications, "Notifications", true)
        );
    }

    @DeleteMapping(path = "/delete/notifications/{id}")
    public ResponseEntity<CustomApiResponse<Void>> deleteNotification(@AuthenticationPrincipal CustomUser customUser,
                                                                      @PathVariable String id) {
        accountService.deleteNotification(id, customUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(
                new CustomApiResponse<>(null, "Notification deleted", true)
        );
    }


    @DeleteMapping(path = "/delete/notifications/all")
    public ResponseEntity<CustomApiResponse<Void>> deleteAllNotifications(@AuthenticationPrincipal CustomUser customUser) {
        accountService.deleteAllNotifications(customUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(
                new CustomApiResponse<>(null, "All notifications deleted", true)
        );
    }



    @GetMapping(path = "/me")
    public ResponseEntity<CustomApiResponse<AccountInfoResponse>> getMe(@AuthenticationPrincipal CustomUser customUser) {
        AccountInfoResponse accountInfoResponse = accountService.getMe(customUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(
                new CustomApiResponse<>(accountInfoResponse, "User details", true)
        );
    }


    @DeleteMapping(path = "/remove/brand-logo")
    public ResponseEntity<CustomApiResponse<Void>> removeBrandLogo(@AuthenticationPrincipal CustomUser customUser) {
        accountService.removeBrandLogo(customUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(
                new CustomApiResponse<>(null, "Brand logo removed", true)
        );
    }


    @GetMapping(path = "/serve/brand-logo")
    public ResponseEntity<?> serveBrandLogo(@AuthenticationPrincipal CustomUser customUser) {
        InputStreamResource inputStreamResource = new InputStreamResource(accountService.serveBrandLogo(customUser.getId()));
        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Disposition", "inline; filename=brand-logo.jpg")
                .contentType(MediaType.IMAGE_JPEG)
                .body(inputStreamResource);
    }



    @PutMapping(path = "/change-password")
    public ResponseEntity<CustomApiResponse<Void>> changePassword(@RequestBody @Valid
                                                                  PasswordChangeRequest passwordChangeRequest,
                                                                  @AuthenticationPrincipal CustomUser customUser,
                                                                  HttpServletRequest request,
                                                                  HttpServletResponse response) {

        accountService.changePassword(passwordChangeRequest,
                customUser.getPassword(),
                customUser.getId(),
                request, response);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CustomApiResponse<>(null, "Password changed successfully", true)
        );
    }

    @PostMapping(path = "/mfa/register/{code}")
    public ResponseEntity<CustomApiResponse<Void>> registerMfa(@AuthenticationPrincipal CustomUser
                                                                       customUser, @PathVariable
                                                               String code) {

        Boolean success = accountService.registerMfa(code, customUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(
                new CustomApiResponse<>(null, "Mfa successfully registered", success)
        );
    }

    @PostMapping(path = "/mfa/generate-qr")
    public ResponseEntity<CustomApiResponse<String>> generateQr(@AuthenticationPrincipal CustomUser customUser)
            throws QrGenerationException {


        String imageUri = accountService.generateQrCodeMfa(customUser.getEmail(), customUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(
                new CustomApiResponse<>(imageUri, "QR code", true)
        );

    }

    @PutMapping(path = "/mfa/disable")
    public ResponseEntity<CustomApiResponse<Void>> disableMfa(@AuthenticationPrincipal CustomUser customUser) {
        accountService.disableMfa(customUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(
                new CustomApiResponse<>(null, "Mfa disabled", true)
        );
    }


    @PutMapping(path = "/update-info")
    public ResponseEntity<CustomApiResponse<Void>> updateInfo(@AuthenticationPrincipal CustomUser customUser,
                                                              @RequestBody @Valid AccountInfoModificationRequest
                                                                      accountInfoModificationRequest) {
        accountService.updateAccountInfo(accountInfoModificationRequest, customUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(
                new CustomApiResponse<>(null, "User info updated", true)
        );
    }


    @PostMapping(path ="/upload/brand-logo")
    public ResponseEntity<CustomApiResponse<Void>> uploadBrandLogo(@AuthenticationPrincipal CustomUser customUser,
                                                                   @RequestParam("logo") MultipartFile file) {

       Float value = (float) (file.getSize()/1000000);
       log.error("File size: "+value);
       if(value > 1) {
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                   new CustomApiResponse<>(null, "File size should be less than 2MB", false)
           );
       }

        accountService.uploadBrandLogo(customUser.getId(), file);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CustomApiResponse<>(null, "Brand logo uploaded", true)
        );
    }



}
