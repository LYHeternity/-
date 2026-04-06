package com.entrepreneur.platform.controller;

import com.entrepreneur.platform.common.Result;
import com.entrepreneur.platform.entity.User;
import com.entrepreneur.platform.service.UserService;
import com.entrepreneur.platform.service.UserFavoriteService;
import com.entrepreneur.platform.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 个人中心：个人信息、修改资料、头像、修改密码
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserFavoriteService userFavoriteService;

    @GetMapping("/profile")
    public Result<User> profile() {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        return Result.ok(userService.getById(uid));
    }

    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody Map<String, String> body) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        userService.updateProfile(uid,
                body.get("realName"), body.get("email"), body.get("phone"), body.get("intro"),
                body.get("industry"), body.get("investmentAmount"), body.get("companyName"),
                body.get("contactPerson"), body.get("serviceType"), body.get("serviceIntro"));
        return Result.ok();
    }

    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        // 实际路径由 FileController 上传后返回，这里简化：直接调 FileController 逻辑或复用
        return Result.fail("请使用 /files/upload 上传后，将返回的 URL 通过 PUT /user/profile 的 avatar 字段更新");
    }

    @PutMapping("/password")
    public Result<String> changePassword(@RequestBody Map<String, String> body) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        String oldPwd = body.get("oldPassword");
        String newPwd = body.get("newPassword");
        if (oldPwd == null || newPwd == null) return Result.fail("参数不完整");
        userService.changePassword(uid, oldPwd, newPwd);
        return Result.ok();
    }

    @GetMapping("/favorites/projects")
    public Result<?> myFavoriteProjects(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        return Result.ok(userFavoriteService.myFavoriteProjects(uid, page, size));
    }

    @GetMapping("/favorites/resources")
    public Result<?> myFavoriteResources(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        return Result.ok(userFavoriteService.myFavoriteResources(uid, page, size));
    }

    @GetMapping("/favorites/articles")
    public Result<?> myFavoriteArticles(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        return Result.ok(userFavoriteService.myFavoriteArticles(uid, page, size));
    }

    @GetMapping("/favorites/posts")
    public Result<?> myFavoritePosts(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        return Result.ok(userFavoriteService.myFavoritePosts(uid, page, size));
    }
}
