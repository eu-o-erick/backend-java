package com.eventostec.api.controller;

import com.eventostec.api.domain.event.Event;
import com.eventostec.api.domain.event.EventRequestDTO;
import com.eventostec.api.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/event")
public class EventController {

  @Autowired
  private EventService eventService;

  @PostMapping(consumes = "multipart/form-data")
  public ResponseEntity<Event> create(@RequestParam("title") String title,
                                      @RequestParam(value = "description", required = false) String description,
                                      @RequestParam("date") Long date,
                                      @RequestParam("city") String city,
                                      @RequestParam("uf") String uf,
                                      @RequestParam("remote") Boolean remote,
                                      @RequestParam("eventUrl") String eventUrl,
                                      @RequestParam(value = "image", required = false) MultipartFile image) {
    EventRequestDTO eventRequestDTO = new EventRequestDTO(title, description, date, city, uf, remote, eventUrl, image);
    Event newEvent = this.eventService.createEvent(eventRequestDTO);
    return ResponseEntity.ok(newEvent);
  }
  
}
