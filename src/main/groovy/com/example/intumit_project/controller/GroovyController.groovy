package com.example.intumit_project.controller;

import com.example.intumit_project.model.Announcement;
import com.example.intumit_project.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

@Controller
public class AnnouncementController {

    @Autowired
    private AnnouncementRepository announcementRepository;

    // 顯示首頁並列出所有公告
    @GetMapping("/index")
    String showIndex(Model model) {
//        List<Announcement> announcements = announcementRepository.findAll(); // 查詢所有公告

        List<Announcement> announcements = new ArrayList<>();

        // 第一筆測試資料
        Announcement announcement1 = new Announcement();
        announcement1.setId(1L);
        announcement1.setTitle("測試公告 1");
        announcement1.setPublisher("Alice");
        announcement1.setPublishDate(LocalDate.of(2025, 3, 20));
        announcement1.setDeadline(LocalDate.of(2025, 3, 27));
        announcement1.setContent("<p>這是第一則測試公告內容，使用 <strong>粗體</strong> 格式。</p>");
        announcement1.setAttachmentPath("/uploads/test1.pdf");
        announcements.add(announcement1);

        // 第二筆測試資料
        Announcement announcement2 = new Announcement();
        announcement2.setId(2L);
        announcement2.setTitle("測試公告 2");
        announcement2.setPublisher("Bob");
        announcement2.setPublishDate(LocalDate.of(2025, 3, 21));
        announcement2.setDeadline(LocalDate.of(2025, 4, 1));
        announcement2.setContent("<p>這是第二則測試公告，包含一個列表：<ul><li>項目 1</li><li>項目 2</li></ul></p>");
        announcement2.setAttachmentPath(null); // 無附件
        announcements.add(announcement2);

        model.addAttribute("announcements", announcements);
        model.addAttribute("name", "John Doe");
        model.addAttribute("message", "Hello, welcome to the Groovy Spring Boot application!");
        return "index"; // 返回 index.html
    }

    // 新增公告
    @PostMapping("/insert")
    String insert(@ModelAttribute Announcement announcement,
                  @RequestParam("attachment") MultipartFile attachment,
                  Model model) {
        if (!attachment.isEmpty()) {
            try {
                String uploadDir = "src/main/resources/static/uploads/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String fileName = attachment.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(attachment.getInputStream(), filePath);
                announcement.setAttachmentPath("/uploads/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("error", "檔案上傳失敗");
                return "errorPage";
            }
        }
        announcementRepository.save(announcement); // 新增（id 為 null）
        return "redirect:/index";
    }

    // 顯示編輯表單
    @GetMapping("/edit/{id}")
    String showEditForm(@PathVariable("id") Long id, Model model) {
//        Announcement announcement = announcementRepository.findById(id)
//                .orElseThrow({ -> new IllegalArgumentException("無效的公告 ID: " + id) })
        Announcement announcement = new Announcement()
        announcement.setId(1L)
        announcement.setTitle("測試公告 1")
        announcement.setPublisher("Alice")
        announcement.setPublishDate(LocalDate.of(2025, 3, 20))
        announcement.setDeadline(LocalDate.of(2025, 3, 27))
        announcement.setContent("<p>這是第一則測試公告內容，使用 <strong>粗體</strong> 格式。</p>")

        model.addAttribute("announcement", announcement)
        return "edit"
    }

    // 更新公告
    @PostMapping("/Update")
    String update(@ModelAttribute Announcement announcement,
                  @RequestParam("attachment") MultipartFile attachment,
                  Model model) {
        if (!attachment.isEmpty()) {
            try {
                String uploadDir = "src/main/resources/static/uploads/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String fileName = attachment.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(attachment.getInputStream(), filePath);
                announcement.setAttachmentPath("/uploads/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("error", "檔案上傳失敗");
                return "errorPage";
            }
        }
        announcementRepository.save(announcement); // 更新（id 不為 null）
        return "redirect:/index";
    }

    // 刪除公告
    @PostMapping("/Delete")
    String delete(@PathVariable("id") Long id, Model model) {
        announcementRepository.deleteById(id); // 根據 ID 刪除
        return "redirect:/index";
    }
}