// src/main/groovy/com/example/intumit_project/controller/AnnouncementController.groovy
package com.example.intumit_project.controller

import com.example.intumit_project.model.Announcement
import com.example.intumit_project.service.AnnouncementService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Controller
class AnnouncementController {
    @Autowired
    AnnouncementService announcementService

    @GetMapping("/index")
    String showIndex(Model model) {
        def announcements = announcementService.findAllAnnouncements()
        model.addAttribute("announcements", announcements)
        "index"
    }

    @PostMapping("/insert")
    String insert(@ModelAttribute Announcement announcement,
                  @RequestParam(value = "uploadFiles", required = false) MultipartFile[] uploadFiles,
                  Model model) {
        try {
            announcementService.saveAnnouncement(announcement, uploadFiles)
            "redirect:/index"
        } catch (Exception e) {
            model.addAttribute("error", "檔案上傳失敗")
            "errorPage"
        }
    }

    @GetMapping("/edit/{id}")
    String showEditForm(@PathVariable("id") Long id, Model model) {
        def announcement = announcementService.findAnnouncementById(id)
        if (!announcement) {
            throw new IllegalArgumentException("無效的公告 ID: ${id}")
        }
        model.addAttribute("announcement", announcement)
        "edit"
    }

    @PostMapping("/Update")
    String update(@ModelAttribute Announcement announcement,
                  @RequestParam(value = "uploadFiles", required = false) MultipartFile[] uploadFiles,
                  Model model) {
        try {
            announcementService.saveAnnouncement(announcement, uploadFiles)
            "redirect:/index"
        } catch (Exception e) {
            model.addAttribute("error", "檔案上傳失敗")
            "errorPage"
        }
    }

    @PostMapping("/Delete")
    String delete(@RequestParam("id") Long id) {
        announcementService.deleteAnnouncement(id)
        "redirect:/index"
    }

    @PostMapping("/deleteAttachment/{attachmentId}")
    String deleteAttachment(@PathVariable("attachmentId") Long attachmentId) {
        announcementService.deleteAttachment(attachmentId)
        def attachment = announcementService.attachmentRepository.findById(attachmentId)
        if (attachment) {
            "redirect:/edit/${attachment.announcement.id}"
        } else {
            "redirect:/index"
        }
    }
}