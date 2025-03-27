// src/main/groovy/com/example/intumit_project/controller/AnnouncementController.groovy
package com.example.intumit_project.controller

import com.example.intumit_project.model.Announcement
import com.example.intumit_project.service.AnnouncementService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.transaction.annotation.Transactional

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
            model.addAttribute("error", e)
            "errorPage"
        }
    }

    @GetMapping("/edit/{id}")
    @Transactional(readOnly = true)
    String showEditForm(@PathVariable("id") Long id, Model model) {
        def announcement = announcementService.findAnnouncementById(id)
        println "Announcement: ${announcement}"
        if (!announcement) {
            throw new IllegalArgumentException("無效的公告 ID: ${id}")
        }
        println "Attachments: ${announcement.attachments}"
        model.addAttribute("announcement", announcement)
        "edit"
    }

    @PostMapping("/Update")
    String update(@ModelAttribute Announcement announcement,
                  @RequestParam(value = "uploadFiles", required = false) MultipartFile[] uploadFiles,
                  @RequestParam(value = "deleteAttachments", required = false) List<Long> deleteAttachments,
                  Model model) {
        try {
            announcementService.saveAnnouncement(announcement, uploadFiles, deleteAttachments)
            "redirect:/index"
        } catch (Exception e) {
            model.addAttribute("error", "檔案上傳失敗: ${e.message}")
            "errorPage"
        }
    }

    @PostMapping("/Delete")
    String delete(@RequestParam("id") Long id) {
        announcementService.deleteAnnouncement(id)
        "redirect:/index"
    }
}