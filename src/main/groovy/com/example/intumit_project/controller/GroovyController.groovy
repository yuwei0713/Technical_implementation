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

    // 顯示首頁並列出所有公告
    @GetMapping("/index")
    String showIndex(Model model) {
        List<Announcement> announcements = announcementRepository.findAll();
        model.addAttribute("announcements", announcements);
        return "index";
    }

    // 新增公告
    @PostMapping("/insert")
    String insert(@ModelAttribute Announcement announcement,
                  @RequestParam("attachments") MultipartFile[] attachments,
                  Model model) {
        announcementRepository.save(announcement); // 先保存公告

        if (attachments != null && attachments.length > 0) {
            String uploadDir = "src/main/resources/static/uploads/";
            Path uploadPath = Paths.get(uploadDir);
            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                for (MultipartFile attachment : attachments) {
                    if (!attachment.isEmpty()) {
                        String fileName = attachment.getOriginalFilename();
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
        return "redirect:/index";
    }

    // 顯示編輯表單
    @GetMapping("/edit/{id}")
    String showEditForm(@PathVariable("id") Long id, Model model) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("無效的公告 ID: " + id));
        model.addAttribute("announcement", announcement);
        return "edit";
    }

    // 更新公告
    @PostMapping("/Update")
    String update(@ModelAttribute Announcement announcement,
                  @RequestParam("attachments") MultipartFile[] attachments,
                  Model model) {
        if (attachments != null && attachments.length > 0) {
            String uploadDir = "src/main/resources/static/uploads/";
            Path uploadPath = Paths.get(uploadDir);
            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                for (MultipartFile attachment : attachments) {
                    if (!attachment.isEmpty()) {
                        String fileName = attachment.getOriginalFilename();
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
        announcementRepository.save(announcement); // 更新公告
        return "redirect:/index";
    }

    // 刪除公告
    @PostMapping("/Delete")
    String delete(@RequestParam("id") Long id, Model model) {
        announcementRepository.deleteById(id); // 根據 ID 刪除公告及其附件（因 CASCADE）
        return "redirect:/index";
    }

    // 刪除單個附件
    @PostMapping("/deleteAttachment/{attachmentId}")
    String deleteAttachment(@PathVariable("attachmentId") Long attachmentId) {
        announcementService.deleteAttachment(attachmentId); // 呼叫 Service 刪除附件
        return "redirect:/edit/" + attachmentRepository.findById(attachmentId)
                .map(att -> att.getAnnouncement().getId())
                .orElse(0L); // 返回編輯頁面
    }
}