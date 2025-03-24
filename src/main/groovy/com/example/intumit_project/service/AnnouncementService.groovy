// src/main/groovy/com/example/intumit_project/service/AnnouncementService.groovy
package com.example.intumit_project.service

import com.example.intumit_project.model.Announcement
import com.example.intumit_project.model.Attachment
import com.example.intumit_project.repository.AnnouncementRepository
import com.example.intumit_project.repository.AttachmentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Service
class AnnouncementService {
    @Autowired
    AnnouncementRepository announcementRepository

    @Autowired
    AttachmentRepository attachmentRepository

    private static final String UPLOAD_DIR = "/var/uploads/"

    @Transactional
    void saveAnnouncement(Announcement announcement, MultipartFile[] uploadFiles) {
        announcementRepository.save(announcement)
        if (uploadFiles != null && uploadFiles.length > 0) {
            Path uploadPath = Paths.get(UPLOAD_DIR)
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath)
            }
            uploadFiles.each { file ->
                if (!file.empty) {
                    String fileName = System.currentTimeMillis() + "_" + file.originalFilename
                    Path filePath = uploadPath.resolve(fileName)
                    Files.copy(file.inputStream, filePath)

                    Attachment attachment = new Attachment(
                            fileName: fileName,
                            filePath: "/uploads/" + fileName,
                            announcement: announcement
                    )
                    attachmentRepository.save(attachment)
                    announcement.attachments << attachment
                }
            }
        }
    }

    @Transactional
    void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id)
    }

    @Transactional
    void deleteAttachment(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
        if (attachment) {
            Path filePath = Paths.get(UPLOAD_DIR + attachment.fileName)
            if (Files.exists(filePath)) {
                Files.delete(filePath)
            }
            attachmentRepository.deleteById(attachmentId)
        }
    }

    @Transactional(readOnly = true)
    List<Announcement> findAllAnnouncements() {
        announcementRepository.findAll()
    }

    @Transactional(readOnly = true)
    Announcement findAnnouncementById(Long id) {
        announcementRepository.findById(id)
    }
}