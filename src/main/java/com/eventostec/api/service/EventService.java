package com.eventostec.api.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.eventostec.api.domain.event.Event;
import com.eventostec.api.domain.event.EventRequestDTO;
import com.eventostec.api.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class EventService {

  private static final Logger logger = Logger.getLogger(EventService.class.getName());

  @Value("${aws.bucket.name}")
  private String bucketName;

  @Autowired
  private AmazonS3 s3Client;

  @Autowired
  private EventRepository repository;

  public Event createEvent(EventRequestDTO data) {
    String imgUrl = "";

    if (data.image()!=null) {
      imgUrl = this.uploadImg(data.image());
    }

    Event newEvent = new Event();
    newEvent.setTitle(data.title());
    newEvent.setDescription(data.description());
    newEvent.setEventUrl(data.eventUrl());
    newEvent.setDate(new Date(data.date()));
    newEvent.setImgUrl(imgUrl);
    newEvent.setRemote(data.remote());

    repository.save(newEvent);

    return newEvent;
  }

  private String uploadImg(MultipartFile multipartFile) {
    String filename = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();
    File tempFile = null;

    try {
      tempFile = convertMultipartToFile(multipartFile);

      PutObjectRequest request = new PutObjectRequest(bucketName, filename, tempFile);
      s3Client.putObject(request);

      return s3Client.getUrl(bucketName, filename).toString();

    } catch (Exception e) {
      logger.severe("Erro ao subir o arquivo: " + e.getMessage());
      e.printStackTrace();
      return "";
    } finally {
      if (tempFile!=null && tempFile.exists()) {
        boolean deleted = tempFile.delete();
        if (!deleted) {
          logger.warning("Falha ao deletar o arquivo temporário: " + tempFile.getAbsolutePath());
        }
      }
    }
  }

  private File convertMultipartToFile(MultipartFile multipartFile) throws IOException {
    File tempFile = Files.createTempFile("upload_", multipartFile.getOriginalFilename()).toFile();
    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
      fos.write(multipartFile.getBytes());
    }
    return tempFile;
  }
}
