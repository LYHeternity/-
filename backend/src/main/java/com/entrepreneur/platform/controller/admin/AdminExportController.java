package com.entrepreneur.platform.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.entrepreneur.platform.entity.SystemLog;
import com.entrepreneur.platform.entity.User;
import com.entrepreneur.platform.mapper.SystemLogMapper;
import com.entrepreneur.platform.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 管理员-导出 Excel（用户列表、操作日志）
 */
@RestController
@RequestMapping("/admin/export")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminExportController {

    private final UserMapper userMapper;
    private final SystemLogMapper systemLogMapper;
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/users")
    public void exportUsers(HttpServletResponse response) throws IOException {
        List<User> list = userMapper.selectList(new LambdaQueryWrapper<User>().eq(User::getDeleted, 0));
        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("用户列表");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("用户名");
        header.createCell(2).setCellValue("真实姓名");
        header.createCell(3).setCellValue("角色");
        header.createCell(4).setCellValue("手机");
        header.createCell(5).setCellValue("邮箱");
        header.createCell(6).setCellValue("状态");
        header.createCell(7).setCellValue("注册时间");
        int r = 1;
        for (User u : list) {
            Row row = sheet.createRow(r++);
            row.createCell(0).setCellValue(u.getId() != null ? u.getId() : 0);
            row.createCell(1).setCellValue(u.getUsername() != null ? u.getUsername() : "");
            row.createCell(2).setCellValue(u.getRealName() != null ? u.getRealName() : "");
            row.createCell(3).setCellValue(u.getRole() != null ? u.getRole() : "");
            row.createCell(4).setCellValue(u.getPhone() != null ? u.getPhone() : "");
            row.createCell(5).setCellValue(u.getEmail() != null ? u.getEmail() : "");
            row.createCell(6).setCellValue(u.getStatus() != null ? u.getStatus().toString() : "");
            row.createCell(7).setCellValue(u.getCreateTime() != null ? u.getCreateTime().format(DF) : "");
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("用户列表.xlsx", StandardCharsets.UTF_8.toString()));
        wb.write(response.getOutputStream());
        wb.close();
    }

    @GetMapping("/logs")
    public void exportLogs(HttpServletResponse response) throws IOException {
        List<SystemLog> list = systemLogMapper.selectList(new LambdaQueryWrapper<SystemLog>().orderByDesc(SystemLog::getTimestamp).last("LIMIT 5000"));
        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("操作日志");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("时间");
        header.createCell(2).setCellValue("级别");
        header.createCell(3).setCellValue("模块");
        header.createCell(4).setCellValue("操作");
        header.createCell(5).setCellValue("详情");
        header.createCell(6).setCellValue("用户ID");
        header.createCell(7).setCellValue("IP");
        int r = 1;
        for (SystemLog l : list) {
            Row row = sheet.createRow(r++);
            row.createCell(0).setCellValue(l.getId() != null ? l.getId() : 0);
            row.createCell(1).setCellValue(l.getTimestamp() != null ? l.getTimestamp().format(DF) : "");
            row.createCell(2).setCellValue(l.getLevel() != null ? l.getLevel() : "");
            row.createCell(3).setCellValue(l.getModule() != null ? l.getModule() : "");
            row.createCell(4).setCellValue(l.getAction() != null ? l.getAction() : "");
            row.createCell(5).setCellValue(l.getMessage() != null ? l.getMessage() : "");
            row.createCell(6).setCellValue(l.getUserId() != null ? l.getUserId().toString() : "");
            row.createCell(7).setCellValue(l.getIp() != null ? l.getIp() : "");
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("操作日志.xlsx", StandardCharsets.UTF_8.toString()));
        wb.write(response.getOutputStream());
        wb.close();
    }
}
