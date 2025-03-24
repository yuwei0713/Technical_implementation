package com.example.intumit_project.controller;

import com.example.intumit_project.model.Announcement;
import com.example.intumit_project.model.Attachment;
import com.example.intumit_project.repository.AnnouncementRepository;
import com.example.intumit_project.repository.AttachmentRepository;
import com.example.intumit_project.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class AnnouncementController {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private AnnouncementService announcementService;

    // 上傳目錄設為外部路徑
    private static final String UPLOAD_DIR = "/var/uploads/";

    @GetMapping("/index")
    String showIndex(Model model) {
        List<Announcement> announcements = announcementRepository.findAll();
        model.addAttribute("announcements", announcements);
        return "index";
    }

    @PostMapping("/insert")
    String insert(@ModelAttribute Announcement announcement,
                  @RequestParam("attachments") MultipartFile[] attachments,
                  Model model) {
        announcementRepository.save(announcement);

        if (attachments != null && attachments.length > 0) {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                for (MultipartFile attachment : attachments) {
                    if (!attachment.isEmpty()) {
                        String fileName = System.currentTimeMillis() + "_" + attachment.getOriginalFilename(); // 避免檔案名衝突
                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(attachment.getInputStream(), filePath);
                        Attachment attach = new Attachment();
                        attach.setFileName(fileName);
                        attach.setFilePath("/uploads/" + fileName); // URL 路徑仍以 /uploads 開頭
                        attach.setAnnouncement(announcement);
                        attachmentRepository.save(attach);
                        announcement.getAttachments().add(attach);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("error", "檔案上傳失敗");
                return "errorPage";
            }
        }
        return "redirect:/index";
    }

    @GetMapping("/edit/{id}")
    String showEditForm(@PathVariable("id") Long id, Model model) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("無效的公告 ID: " + id));
        model.addAttribute("announcement", announcement);
        return "edit";
    }

    @PostMapping("/Update")
    String update(@ModelAttribute Announcement announcement,
                  @RequestParam("attachments") MultipartFile[] attachments,
                  Model model) {
        if (attachments != null && attachments.length > 0) {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                for (MultipartFile attachment : attachments) {
                    if (!attachment.isEmpty()) {
                        String fileName = System.currentTimeMillis() + "_" + attachment.getOriginalFilename();
                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(attachment.getInputStream(), filePath);
                        Attachment attach = new Attachment();
                        attach.setFileName(fileName);
                        attach.setFilePath("/uploads/" + fileName);
                        attach.setAnnouncement(announcement);
                        attachmentRepository.save(attach);
                        announcement.getAttachments().add(attach);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("error", "檔案上傳失敗");
                return "errorPage";
            }
        }
        announcementRepository.save(announcement);
        return "redirect:/index";
    }

    @PostMapping("/Delete")
    String delete(@RequestParam("id") Long id, Model model) {
        announcementRepository.deleteById(id);
        return "redirect:/index";
    }

    @PostMapping("/deleteAttachment/{attachmentId}")
    String deleteAttachment(@PathVariable("attachmentId") Long attachmentId) {
        announcementService.deleteAttachment(attachmentId);
        return "redirect:/edit/" + attachmentRepository.findById(attachmentId)
                .map(att -> att.getAnnouncement().getId())
                .orElse(0L);
    }
}