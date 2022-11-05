package com.bitcamp.testproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.bitcamp.testproject.dao.MailDao;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmailService {
  @Autowired
  private JavaMailSender emailSender;

  public void sendSimpleMessage(MailDao mailDao) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("hunsik0302@gmail.com");
    message.setTo(mailDao.getAddress());
    message.setSubject(mailDao.getTitle());
    message.setText(mailDao.getContent());
    System.out.println(mailDao);
    emailSender.send(message);
  }
}