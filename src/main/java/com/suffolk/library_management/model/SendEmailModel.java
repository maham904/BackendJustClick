package com.suffolk.library_management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendEmailModel {
    private String subject;
    private String userEmail;
    private String userName;
    private String verificationLink;
    private String logoData;
    private String registerMessage;
}
