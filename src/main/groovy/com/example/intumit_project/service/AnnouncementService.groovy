package com.example.intumit_project.service;

import com.example.intumit_project.model.Attachment;
import com.example.intumit_project.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AnnouncementService {

    @Autowired
    private AttachmentRepository attachmentRepository;

    // 刪除單個附件的邏輯
    public void deleteAttachment(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException("無效的附件 ID: " + attachmentId));

        // 刪除伺服器上的檔案
        Path filePath = Paths.get("src/main/resources/static" + attachment.getFilePath());
        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("刪除檔案失敗: " + e.getMessage());
        }

        // 從資料庫中刪除記錄
        attachmentRepository.deleteById(attachmentId);
    }
}