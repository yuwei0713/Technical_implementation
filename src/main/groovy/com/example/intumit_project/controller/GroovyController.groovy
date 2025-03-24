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
                  @RequestParam(value = "uploadFiles", required = false) MultipartFile[] uploadFiles,
                  Model model) {
        // 先保存公告（不含附件）
        announcementRepository.save(announcement);

        // 處理檔案上傳
        if (uploadFiles != null && uploadFiles.length > 0) {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                for (MultipartFile file : uploadFiles) {
                    if (!file.isEmpty()) {
                        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(file.getInputStream(), filePath);

                        Attachment attachment = new Attachment();
                        attachment.setFileName(fileName);
                        attachment.setFilePath("/uploads/" + fileName);
                        attachment.setAnnouncement(announcement);
                        attachmentRepository.save(attachment);

                        if (announcement.getAttachments() == null) {
                            announcement.setAttachments(new ArrayList<>());
                        }
                        announcement.getAttachments().add(attachment);
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
    public String update(@ModelAttribute Announcement announcement,
                         @RequestParam(value = "uploadFiles", required = false) MultipartFile[] uploadFiles,
                         Model model) {
        announcementRepository.save(announcement);

        if (uploadFiles != null && uploadFiles.length > 0) {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                for (MultipartFile file : uploadFiles) {
                    if (!file.isEmpty()) {
                        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(file.getInputStream(), filePath);

                        Attachment attachment = new Attachment();
                        attachment.setFileName(fileName);
                        attachment.setFilePath("/uploads/" + fileName);
                        attachment.setAnnouncement(announcement);
                        attachmentRepository.save(attachment);

                        if (announcement.getAttachments() == null) {
                            announcement.setAttachments(new ArrayList<>());
                        }
                        announcement.getAttachments().add(attachment);
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