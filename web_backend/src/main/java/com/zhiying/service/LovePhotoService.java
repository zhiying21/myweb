package com.zhiying.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhiying.config.UploadProperties;
import com.zhiying.entity.LovePhotoEntity;
import com.zhiying.mapper.LovePhotoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 恋爱照片业务服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LovePhotoService {

    private final LovePhotoMapper lovePhotoMapper;
    private final UploadProperties uploadProperties;

    private static final String LOVE_PHOTOS_DIR = "love-photos";
    private static final String[] ALLOWED_TYPES = {"image/jpeg", "image/png", "image/gif", "image/webp"};
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * 分页查询照片
     */
    public Page<LovePhotoEntity> queryPageList(int pageNum, int pageSize) {
        Page<LovePhotoEntity> page = new Page<>(pageNum, pageSize);
        return lovePhotoMapper.selectPage(page,
                new LambdaQueryWrapper<LovePhotoEntity>()
                        .eq(LovePhotoEntity::getDeleted, 0)
                        .orderByDesc(LovePhotoEntity::getCreateTime));
    }

    /**
     * 获取所有照片列表
     */
    public List<LovePhotoEntity> queryList() {
        return lovePhotoMapper.selectList(
                new LambdaQueryWrapper<LovePhotoEntity>()
                        .eq(LovePhotoEntity::getDeleted, 0)
                        .orderByDesc(LovePhotoEntity::getTakenDate));
    }

    /**
     * 获取精选照片
     */
    public List<LovePhotoEntity> queryFeatured() {
        return lovePhotoMapper.selectList(
                new LambdaQueryWrapper<LovePhotoEntity>()
                        .eq(LovePhotoEntity::getDeleted, 0)
                        .eq(LovePhotoEntity::getIsFeatured, true)
                        .orderByDesc(LovePhotoEntity::getTakenDate)
                        .last("LIMIT 12"));
    }

    /**
     * 根据ID查询照片
     */
    public LovePhotoEntity queryById(Long id) {
        return lovePhotoMapper.selectById(id);
    }

    /**
     * 上传照片
     */
    public LovePhotoEntity uploadPhoto(MultipartFile file, String description, LocalDateTime takenDate) throws Exception {
        // 验证文件
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        String contentType = file.getContentType();
        if (!isAllowedType(contentType)) {
            throw new IllegalArgumentException("不支持的文件类型，仅支持图片格式");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过 10MB");
        }

        try {
            // 创建上传目录
            String uploadBasePath = uploadProperties.getPath();
            Path uploadPath = Paths.get(uploadBasePath, LOVE_PHOTOS_DIR);
            Files.createDirectories(uploadPath);

            // 生成随机文件名
            String ext = getFileExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID() + "." + ext;
            Path filePath = uploadPath.resolve(filename);

            // 保存文件
            Files.write(filePath, file.getBytes());
            log.info("照片上传成功: {}", filePath);

            // 创建照片记录
            LovePhotoEntity photo = new LovePhotoEntity();
            photo.setUrl("/api/love/photos/file/" + filename);
            photo.setDescription(description);
            photo.setTakenDate(takenDate != null ? takenDate : LocalDateTime.now());
            photo.setIsFeatured(false);

            lovePhotoMapper.insert(photo);
            return photo;
        } catch (Exception e) {
            log.error("照片上传失败", e);
            throw e;
        }
    }

    /**
     * 创建照片记录（仅数据库操作，用于前端直传）
     */
    public LovePhotoEntity create(LovePhotoEntity photo) {
        if (photo.getTakenDate() == null) {
            photo.setTakenDate(LocalDateTime.now());
        }
        if (photo.getIsFeatured() == null) {
            photo.setIsFeatured(false);
        }
        lovePhotoMapper.insert(photo);
        return photo;
    }

    /**
     * 更新照片信息
     */
    public LovePhotoEntity update(LovePhotoEntity photo) {
        lovePhotoMapper.updateById(photo);
        return photo;
    }

    /**
     * 删除照片（逻辑删除）
     */
    public void delete(Long id) throws Exception {
        LovePhotoEntity photo = lovePhotoMapper.selectById(id);
        if (photo != null) {
            // 删除文件
            if (photo.getUrl() != null) {
                try {
                    String filename = photo.getUrl().replace("/api/love/photos/file/", "");
                    String uploadBasePath = uploadProperties.getPath();
                    Path filepath = Paths.get(uploadBasePath, LOVE_PHOTOS_DIR, filename);
                    Files.deleteIfExists(filepath);
                } catch (Exception e) {
                    log.warn("文件删除失败，继续执行数据库删除", e);
                    // 文件删除失败继续执行，记录数据库删除
                }
            }
            // 逻辑删除
            photo.setDeleted(1);
            lovePhotoMapper.updateById(photo);
        }
    }

    /**
     * 设置精选状态
     */
    public void setFeatured(Long id, Boolean featured) {
        LovePhotoEntity photo = new LovePhotoEntity();
        photo.setId(id);
        photo.setIsFeatured(featured);
        lovePhotoMapper.updateById(photo);
    }

    /**
     * 检查是否为允许的文件类型
     */
    private boolean isAllowedType(String contentType) {
        if (contentType == null) return false;
        for (String type : ALLOWED_TYPES) {
            if (contentType.equals(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
