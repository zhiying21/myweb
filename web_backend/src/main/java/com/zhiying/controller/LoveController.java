package com.zhiying.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhiying.common.result.Result;
import com.zhiying.config.UploadProperties;
import com.zhiying.entity.LoveDiaryEntity;
import com.zhiying.entity.LovePhotoEntity;
import com.zhiying.entity.LoveReminderEntity;
import com.zhiying.service.LoveConfigService;
import com.zhiying.service.LoveDiaryService;
import com.zhiying.service.LovePhotoService;
import com.zhiying.service.LoveReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

/**
 * 恋爱日记控制器
 * 包含配置、日记、纪念日、照片的CRUD接口
 */
@Slf4j
@RestController
@RequestMapping("/love")
@RequiredArgsConstructor
public class LoveController {

    private final LoveConfigService loveConfigService;
    private final LoveDiaryService loveDiaryService;
    private final LoveReminderService loveReminderService;
    private final LovePhotoService lovePhotoService;
    private final UploadProperties uploadProperties;

    private static final DateTimeFormatter[] PARSERS = {
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"),
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("yyyy/MM/dd")
    };

    // ==================== 配置接口 ====================

    @GetMapping("/config")
    public Result<Map<String, Object>> getConfig() {
        return Result.ok(loveConfigService.getConfig());
    }

    @PostMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> saveConfig(@RequestBody Map<String, Object> body) {
        try {
            LocalDateTime startTime = parseStartTime(body.get("startTime"));
            String name1 = toStr(body.get("name1"), "TA");
            String name2 = toStr(body.get("name2"), "TA");
            String avatar1 = emptyToNull(toStr(body.get("avatar1"), null));
            String avatar2 = emptyToNull(toStr(body.get("avatar2"), null));
            loveConfigService.saveConfig(startTime, name1, name2, avatar1, avatar2);
            return Result.ok();
        } catch (Exception e) {
            log.error("恋爱配置保存失败", e);
            return Result.fail(500, e.getMessage() != null ? e.getMessage() : "保存失败");
        }
    }

    // ==================== 日记接口 ====================

    @GetMapping("/diaries")
    public Result<List<LoveDiaryEntity>> getDiaries() {
        try {
            return Result.ok(loveDiaryService.queryList());
        } catch (Exception e) {
            log.error("获取日记列表失败", e);
            return Result.fail(500, "获取日记列表失败");
        }
    }

    @GetMapping("/diaries/page")
    public Result<Page<LoveDiaryEntity>> getDiariesPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            return Result.ok(loveDiaryService.queryPageList(pageNum, pageSize));
        } catch (Exception e) {
            log.error("分页查询日记失败", e);
            return Result.fail(500, "分页查询日记失败");
        }
    }

    @GetMapping("/diaries/{id}")
    public Result<LoveDiaryEntity> getDiary(@PathVariable Long id) {
        try {
            return Result.ok(loveDiaryService.queryById(id));
        } catch (Exception e) {
            log.error("获取日记详情失败", e);
            return Result.fail(500, "获取日记详情失败");
        }
    }

    @PostMapping("/diaries")
    public Result<LoveDiaryEntity> createDiary(@RequestBody Map<String, Object> body) {
        try {
            LoveDiaryEntity diary = new LoveDiaryEntity();
            diary.setTitle(toStr(body.get("title"), ""));
            diary.setContent(toStr(body.get("content"), ""));
            diary.setIsPublic(toBoolean(body.get("isPublic"), false));
            
            // 处理日期
            Object dateObj = body.get("date");
            if (dateObj != null) {
                String dateStr = dateObj.toString();
                diary.setDate(parseDiaryDate(dateStr));
            } else {
                diary.setDate(LocalDateTime.now());
            }
            
            // 处理情感分数
            Object emotionScoreObj = body.get("emotionScore");
            if (emotionScoreObj != null) {
                try {
                    diary.setEmotionScore(Integer.parseInt(emotionScoreObj.toString()));
                } catch (NumberFormatException ignored) {}
            }
            
            return Result.ok(loveDiaryService.create(diary));
        } catch (Exception e) {
            log.error("创建日记失败", e);
            return Result.fail(500, "创建日记失败: " + e.getMessage());
        }
    }

    @PutMapping("/diaries/{id}")
    public Result<LoveDiaryEntity> updateDiary(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            LoveDiaryEntity diary = new LoveDiaryEntity();
            diary.setId(id);
            diary.setTitle(toStr(body.get("title"), ""));
            diary.setContent(toStr(body.get("content"), ""));
            diary.setIsPublic(toBoolean(body.get("isPublic"), false));
            
            // 处理日期
            Object dateObj = body.get("date");
            if (dateObj != null) {
                String dateStr = dateObj.toString();
                diary.setDate(parseDiaryDate(dateStr));
            }
            
            // 处理情感分数
            Object emotionScoreObj = body.get("emotionScore");
            if (emotionScoreObj != null) {
                try {
                    diary.setEmotionScore(Integer.parseInt(emotionScoreObj.toString()));
                } catch (NumberFormatException ignored) {}
            }
            
            return Result.ok(loveDiaryService.update(diary));
        } catch (Exception e) {
            log.error("更新日记失败", e);
            return Result.fail(500, "更新日记失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/diaries/{id}")
    public Result<Void> deleteDiary(@PathVariable Long id) {
        try {
            loveDiaryService.delete(id);
            return Result.ok();
        } catch (Exception e) {
            log.error("删除日记失败", e);
            return Result.fail(500, "删除日记失败");
        }
    }

    @PostMapping("/analyze-emotion")
    public Result<Map<String, Object>> analyzeEmotion(@RequestBody Map<String, String> body) {
        try {
            String content = body.get("content");
            int score = analyzeSimpleEmotion(content);
            String analysis = generateEmotionSuggestion(score);

            return Result.ok(Map.of(
                    "score", score,
                    "analysis", analysis
            ));
        } catch (Exception e) {
            log.error("情感分析失败", e);
            return Result.fail(500, "情感分析失败");
        }
    }

    // ==================== 纪念日接口 ====================

    @GetMapping("/reminders")
    public Result<List<LoveReminderEntity>> getReminders() {
        try {
            return Result.ok(loveReminderService.queryList());
        } catch (Exception e) {
            log.error("获取纪念日列表失败", e);
            return Result.fail(500, "获取纪念日列表失败");
        }
    }

    @GetMapping("/reminders/upcoming")
    public Result<List<LoveReminderEntity>> getUpcomingReminders() {
        try {
            return Result.ok(loveReminderService.queryUpcoming());
        } catch (Exception e) {
            log.error("获取即将到来的纪念日失败", e);
            return Result.fail(500, "获取即将到来的纪念日失败");
        }
    }

    @GetMapping("/reminders/page")
    public Result<Page<LoveReminderEntity>> getRemindersPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            return Result.ok(loveReminderService.queryPageList(pageNum, pageSize));
        } catch (Exception e) {
            log.error("分页查询纪念日失败", e);
            return Result.fail(500, "分页查询纪念日失败");
        }
    }

    @GetMapping("/reminders/{id}")
    public Result<LoveReminderEntity> getReminder(@PathVariable Long id) {
        try {
            return Result.ok(loveReminderService.queryById(id));
        } catch (Exception e) {
            log.error("获取纪念日详情失败", e);
            return Result.fail(500, "获取纪念日详情失败");
        }
    }

    @PostMapping("/reminders")
    public Result<LoveReminderEntity> createReminder(@RequestBody Map<String, Object> body) {
        try {
            LoveReminderEntity reminder = new LoveReminderEntity();
            reminder.setTitle(toStr(body.get("title"), ""));
            reminder.setDescription(toStr(body.get("description"), ""));
            reminder.setFrequency(toStr(body.get("frequency"), "once"));
            reminder.setIsEnabled(toBoolean(body.get("isEnabled"), true));
            
            // 处理日期
            Object dateObj = body.get("date");
            if (dateObj != null) {
                String dateStr = dateObj.toString();
                reminder.setDate(parseReminderDate(dateStr));
            } else {
                reminder.setDate(LocalDateTime.now());
            }
            
            return Result.ok(loveReminderService.create(reminder));
        } catch (Exception e) {
            log.error("创建纪念日失败", e);
            return Result.fail(500, "创建纪念日失败: " + e.getMessage());
        }
    }

    @PutMapping("/reminders/{id}")
    public Result<LoveReminderEntity> updateReminder(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            LoveReminderEntity reminder = new LoveReminderEntity();
            reminder.setId(id);
            reminder.setTitle(toStr(body.get("title"), ""));
            reminder.setDescription(toStr(body.get("description"), ""));
            reminder.setFrequency(toStr(body.get("frequency"), "once"));
            reminder.setIsEnabled(toBoolean(body.get("isEnabled"), true));
            
            // 处理日期
            Object dateObj = body.get("date");
            if (dateObj != null) {
                String dateStr = dateObj.toString();
                reminder.setDate(parseReminderDate(dateStr));
            }
            
            return Result.ok(loveReminderService.update(reminder));
        } catch (Exception e) {
            log.error("更新纪念日失败", e);
            return Result.fail(500, "更新纪念日失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/reminders/{id}")
    public Result<Void> deleteReminder(@PathVariable Long id) {
        try {
            loveReminderService.delete(id);
            return Result.ok();
        } catch (Exception e) {
            log.error("删除纪念日失败", e);
            return Result.fail(500, "删除纪念日失败");
        }
    }

    @PutMapping("/reminders/{id}/enabled")
    public Result<Void> setReminderEnabled(@PathVariable Long id, @RequestParam Boolean enabled) {
        try {
            loveReminderService.setReminderEnabled(id, enabled);
            return Result.ok();
        } catch (Exception e) {
            log.error("设置提醒状态失败", e);
            return Result.fail(500, "设置提醒状态失败");
        }
    }

    // ==================== 照片接口 ====================

    @GetMapping("/photos")
    public Result<List<LovePhotoEntity>> getPhotos() {
        try {
            return Result.ok(lovePhotoService.queryList());
        } catch (Exception e) {
            log.error("获取照片列表失败", e);
            return Result.fail(500, "获取照片列表失败");
        }
    }

    @GetMapping("/photos/featured")
    public Result<List<LovePhotoEntity>> getFeaturedPhotos() {
        try {
            return Result.ok(lovePhotoService.queryFeatured());
        } catch (Exception e) {
            log.error("获取精选照片失败", e);
            return Result.fail(500, "获取精选照片失败");
        }
    }

    @GetMapping("/photos/page")
    public Result<Page<LovePhotoEntity>> getPhotosPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "12") int pageSize) {
        try {
            return Result.ok(lovePhotoService.queryPageList(pageNum, pageSize));
        } catch (Exception e) {
            log.error("分页查询照片失败", e);
            return Result.fail(500, "分页查询照片失败");
        }
    }

    @GetMapping("/photos/{id}")
    public Result<LovePhotoEntity> getPhoto(@PathVariable Long id) {
        try {
            return Result.ok(lovePhotoService.queryById(id));
        } catch (Exception e) {
            log.error("获取照片详情失败", e);
            return Result.fail(500, "获取照片详情失败");
        }
    }

    @PostMapping("/photos")
    public Result<LovePhotoEntity> uploadPhoto(
            @RequestParam MultipartFile photo,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime takenDate) {
        try {
            return Result.ok(lovePhotoService.uploadPhoto(photo, description, takenDate));
        } catch (IllegalArgumentException e) {
            log.error("照片验证失败", e);
            return Result.fail(400, e.getMessage());
        } catch (Exception e) {
            log.error("上传照片失败", e);
            return Result.fail(500, "上传照片失败");
        }
    }

    @PostMapping("/photos/create")
    public Result<LovePhotoEntity> createPhoto(@RequestBody LovePhotoEntity photo) {
        try {
            return Result.ok(lovePhotoService.create(photo));
        } catch (Exception e) {
            log.error("创建照片记录失败", e);
            return Result.fail(500, "创建照片记录失败");
        }
    }

    @PutMapping("/photos/{id}")
    public Result<LovePhotoEntity> updatePhoto(@PathVariable Long id, @RequestBody LovePhotoEntity photo) {
        try {
            photo.setId(id);
            return Result.ok(lovePhotoService.update(photo));
        } catch (Exception e) {
            log.error("更新照片失败", e);
            return Result.fail(500, "更新照片失败");
        }
    }

    @DeleteMapping("/photos/{id}")
    public Result<Void> deletePhoto(@PathVariable Long id) {
        try {
            lovePhotoService.delete(id);
            return Result.ok();
        } catch (Exception e) {
            log.error("删除照片失败", e);
            return Result.fail(500, "删除照片失败");
        }
    }

    @PutMapping("/photos/{id}/metadata")
    public Result<Void> updatePhotoMetadata(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        try {
            LovePhotoEntity photo = new LovePhotoEntity();
            photo.setId(id);
            photo.setDescription(toStr(body.get("description"), null));
            
            // 处理拍摄日期如果存在
            Object takenDateObj = body.get("takenDate");
            if (takenDateObj != null) {
                String dateStr = takenDateObj.toString();
                photo.setTakenDate(parseReminderDate(dateStr));
            }
            
            lovePhotoService.update(photo);
            return Result.ok();
        } catch (Exception e) {
            log.error("更新照片元数据失败", e);
            return Result.fail(500, "更新照片元数据失败");
        }
    }

    @PutMapping("/photos/{id}/featured")
    public Result<Void> setPhotoFeatured(
            @PathVariable Long id,
            @RequestParam(required = false) Boolean featured) {
        try {
            Boolean isFeatured = featured != null ? featured : true;
            lovePhotoService.setFeatured(id, isFeatured);
            return Result.ok();
        } catch (Exception e) {
            log.error("设置精选状态失败", e);
            return Result.fail(500, "设置精选状态失败");
        }
    }

    /**
     * 获取照片文件（支持浏览器直接访问）
     */
    @GetMapping("/photos/file/{filename}")
    public ResponseEntity<byte[]> getPhotoFile(@PathVariable String filename) {
        try {
            String uploadBasePath = uploadProperties.getPath();
            Path filePath = Paths.get(uploadBasePath, "love-photos", filename);

            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileContent = Files.readAllBytes(filePath);
            String contentType = determineContentType(filename);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(fileContent);
        } catch (IOException e) {
            log.error("获取照片文件失败: {}", filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==================== 工具方法 ====================

    private String determineContentType(String filename) {
        if (filename == null) return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        String lower = filename.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lower.endsWith(".png")) {
            return "image/png";
        } else if (lower.endsWith(".gif")) {
            return "image/gif";
        } else if (lower.endsWith(".webp")) {
            return "image/webp";
        } else {
            return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
    }

    private LocalDateTime parseStartTime(Object val) {
        if (val == null) return LocalDateTime.now();
        String s = String.valueOf(val).replace("Z", "").trim();
        if (s.isBlank()) return LocalDateTime.now();
        for (DateTimeFormatter f : PARSERS) {
            try {
                return LocalDateTime.parse(s, f);
            } catch (DateTimeParseException ignored) {}
        }
        return LocalDateTime.now();
    }

    private String toStr(Object o, String def) {
        if (o == null) return def;
        String s = o.toString().trim();
        return s.isEmpty() ? def : s;
    }

    private String emptyToNull(String s) {
        return s != null && !s.isBlank() ? s : null;
    }

    /**
     * 解析日记日期（支持多种格式）
     */
    private LocalDateTime parseDiaryDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return LocalDateTime.now();
        }
        dateStr = dateStr.replace("Z", "").trim();
        for (DateTimeFormatter f : PARSERS) {
            try {
                return LocalDateTime.parse(dateStr, f);
            } catch (DateTimeParseException ignored) {}
        }
        // 如果只有日期部分，设置为当天的开始时间
        try {
            return LocalDateTime.parse(dateStr + "T00:00:00");
        } catch (Exception ignored) {}
        return LocalDateTime.now();
    }

    /**
     * 解析纪念日日期（支持多种格式）
     */
    private LocalDateTime parseReminderDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return LocalDateTime.now();
        }
        dateStr = dateStr.replace("Z", "").trim();
        for (DateTimeFormatter f : PARSERS) {
            try {
                return LocalDateTime.parse(dateStr, f);
            } catch (DateTimeParseException ignored) {}
        }
        // 如果只有日期部分，设置为当天的开始时间
        try {
            return LocalDateTime.parse(dateStr + "T00:00:00");
        } catch (Exception ignored) {}
        return LocalDateTime.now();
    }

    /**
     * 转换为布尔值
     */
    private Boolean toBoolean(Object o, Boolean def) {
        if (o == null) return def;
        if (o instanceof Boolean) return (Boolean) o;
        String s = o.toString().toLowerCase();
        if ("true".equals(s) || "1".equals(s) || "yes".equals(s)) return true;
        if ("false".equals(s) || "0".equals(s) || "no".equals(s)) return false;
        return def;
    }

    /**
     * 简单的情感分析（基于关键词）
     */
    private int analyzeSimpleEmotion(String content) {
        if (content == null || content.isEmpty()) return 3;

        String text = content.toLowerCase();
        int positiveCount = 0;
        int negativeCount = 0;

        String[] positiveWords = {"开心", "快乐", "幸福", "愉快", "美好", "温暖", "爱", "亲爱", "甜蜜", "欣喜", "高兴", "满足"};
        String[] negativeWords = {"难过", "伤心", "失望", "烦恼", "生气", "沮丧", "痛苦", "遗憾", "无奈", "冷淡", "争执"};

        for (String word : positiveWords) {
            if (text.contains(word)) positiveCount++;
        }

        for (String word : negativeWords) {
            if (text.contains(word)) negativeCount++;
        }

        if (positiveCount > negativeCount + 1) return 5;
        if (positiveCount > negativeCount) return 4;
        if (negativeCount > positiveCount + 1) return 1;
        if (negativeCount > positiveCount) return 2;
        return 3;
    }

    /**
     * 根据情感分数生成建议
     */
    private String generateEmotionSuggestion(int score) {
        switch (score) {
            case 5:
                return "💖 你的心里装满了幸福！继续珍惜彼此，一起走向更美好的未来。";
            case 4:
                return "😊 这段时光充满了温暖。多沟通，一起分享生活的小美好吧！";
            case 3:
                return "😐 平淡即是福。记得问问对方的想法，加深彼此的了解。";
            case 2:
                return "😔 这段时间有些不顺。试试一起去做些你们都喜欢的事，重燃热情。";
            case 1:
                return "💔 需要好好谈一次心。听听彼此的想法，找到问题的根源。";
            default:
                return "📝 记录下你们的故事，每一页都珍贵。";
        }
    }
}

