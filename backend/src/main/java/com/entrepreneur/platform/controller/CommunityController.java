package com.entrepreneur.platform.controller;

import com.entrepreneur.platform.common.Result;
import com.entrepreneur.platform.common.PageResult;
import com.entrepreneur.platform.entity.Post;
import com.entrepreneur.platform.service.PostService;
import com.entrepreneur.platform.service.UserFavoriteService;
import com.entrepreneur.platform.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 社区控制器
 */
@RestController
@RequestMapping("/community")
public class CommunityController {

    @Autowired
    private PostService postService;
    
    @Autowired
    private UserFavoriteService userFavoriteService;

    /**
     * 获取帖子列表
     */
    @GetMapping("/posts")
    public Result getPostList(@RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(required = false) String category,
                             @RequestParam(required = false) String type,
                             @RequestParam(required = false) String status,
                             @RequestParam(required = false) String keyword) {
        System.out.println("获取帖子列表请求: page=" + page + ", size=" + size + ", category=" + category + ", type=" + type + ", status=" + status + ", keyword=" + keyword);
        try {
            PageResult<Post> result = postService.getPostList(page, size, category, type, status, keyword);
            System.out.println("获取帖子列表成功: total=" + result.getTotal() + ", records=" + result.getRecords().size());
            return Result.ok(result);
        } catch (Exception e) {
            System.out.println("获取帖子列表失败:");
            e.printStackTrace();
            return Result.fail("获取帖子列表失败");
        }
    }

    /**
     * 获取帖子详情
     */
    @GetMapping("/posts/{id}")
    public Result getPostDetail(@PathVariable Long id) {
        System.out.println("获取帖子详情请求: id=" + id);
        try {
            postService.incrementViewCount(id);
            Post post = postService.getPostById(id);
            System.out.println("获取帖子详情成功: title=" + (post != null ? post.getTitle() : "null"));
            return Result.ok(post);
        } catch (Exception e) {
            System.out.println("获取帖子详情失败:");
            e.printStackTrace();
            return Result.fail("获取帖子详情失败");
        }
    }

    /**
     * 发布帖子
     */
    @PostMapping("/posts")
    public Result createPost(@RequestBody Post post) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) return Result.fail(401, "未登录");
        post.setUserId(userId);
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setStatus(1); // 直接发布，实际项目中可能需要审核
        postService.save(post);
        return Result.ok("发布成功");
    }

    /**
     * 检查当前用户是否是管理员
     */
    private boolean isAdmin() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("auth: " + auth);
            if (auth == null) {
                System.out.println("auth is null");
                return false;
            }
            System.out.println("authorities: " + auth.getAuthorities());
            // 检查用户角色是否包含admin或ADMIN
            boolean result = auth.getAuthorities().stream()
                    .anyMatch(authority -> {
                        String authorityStr = authority.getAuthority();
                        System.out.println("Authority: " + authorityStr);
                        return authorityStr.equals("ROLE_admin") || authorityStr.equals("admin") || authorityStr.equals("ADMIN");
                    });
            System.out.println("isAdmin result: " + result);
            return result;
        } catch (Exception e) {
            System.out.println("检查管理员权限失败:");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新帖子
     */
    @PutMapping("/posts/{id}")
    public Result updatePost(@PathVariable Long id, @RequestBody Post post) {
        System.out.println("更新帖子请求: id=" + id + ", post=" + post);
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            System.out.println("当前用户ID: " + userId);
            if (userId == null) {
                System.out.println("未登录");
                return Result.fail(401, "未登录");
            }
            Post existingPost = postService.getPostById(id);
            System.out.println("现有帖子: " + (existingPost != null ? existingPost.getTitle() : "null"));
            if (existingPost == null) {
                System.out.println("帖子不存在");
                return Result.fail("帖子不存在");
            }
            // 只有帖子作者或管理员可以修改帖子
            boolean isAdmin = isAdmin();
            System.out.println("是否管理员: " + isAdmin);
            System.out.println("帖子作者ID: " + existingPost.getUserId() + ", 当前用户ID: " + userId);
            // 检查权限
            if (!existingPost.getUserId().equals(userId) && !isAdmin) {
                System.out.println("无权修改");
                return Result.fail("无权修改");
            }
            post.setId(id);
            post.setUserId(existingPost.getUserId()); // 保持原作者ID
            postService.updateById(post);
            System.out.println("更新帖子成功");
            return Result.ok("更新成功");
        } catch (Exception e) {
            System.out.println("更新帖子失败:");
            e.printStackTrace();
            return Result.fail("更新帖子失败");
        }
    }

    /**
     * 删除帖子
     */
    @DeleteMapping("/posts/{id}")
    public Result deletePost(@PathVariable Long id) {
        System.out.println("删除帖子请求: id=" + id);
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            System.out.println("当前用户ID: " + userId);
            if (userId == null) {
                System.out.println("未登录");
                return Result.fail(401, "未登录");
            }
            Post post = postService.getPostById(id);
            System.out.println("帖子: " + (post != null ? post.getTitle() : "null"));
            if (post == null) {
                System.out.println("帖子不存在");
                return Result.fail("帖子不存在");
            }
            // 只有帖子作者或管理员可以删除帖子
            boolean isAdmin = isAdmin();
            System.out.println("是否管理员: " + isAdmin);
            System.out.println("帖子作者ID: " + post.getUserId() + ", 当前用户ID: " + userId);
            // 检查权限
            if (!post.getUserId().equals(userId) && !isAdmin) {
                System.out.println("无权删除");
                return Result.fail("无权删除");
            }
            postService.removeById(id);
            System.out.println("删除帖子成功");
            return Result.ok("删除成功");
        } catch (Exception e) {
            System.out.println("删除帖子失败:");
            e.printStackTrace();
            return Result.fail("删除帖子失败");
        }
    }

    /**
     * 设置最佳答案
     */
    @PutMapping("/posts/{id}/best-answer")
    public Result setBestAnswer(@PathVariable Long id, @RequestParam Long commentId) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) return Result.fail(401, "未登录");
        Post post = postService.getPostById(id);
        if (post == null) {
            return Result.fail("帖子不存在");
        }
        if (!post.getUserId().equals(userId)) {
            return Result.fail("只有作者可以设置最佳答案");
        }
        postService.setBestAnswer(id, commentId);
        return Result.ok("设置成功");
    }
    
    /**
     * 切换帖子收藏状态
     * @param id 帖子ID
     * @return 收藏状态
     */
    @PostMapping("/posts/{id}/favorite")
    public Result<Boolean> toggleFavorite(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) return Result.fail(401, "未登录");
        userFavoriteService.toggleFavorite(userId, "post", id);
        boolean isFavorited = userFavoriteService.isFavorited(userId, "post", id);
        return Result.ok(isFavorited);
    }
    
    /**
     * 检查帖子是否被收藏
     * @param id 帖子ID
     * @return 收藏状态
     */
    @GetMapping("/posts/{id}/favorite")
    public Result<Boolean> isFavorited(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) return Result.fail(401, "未登录");
        boolean isFavorited = userFavoriteService.isFavorited(userId, "post", id);
        return Result.ok(isFavorited);
    }

    /**
     * 获取热门帖子列表
     * @param limit 数量限制
     * @return 热门帖子列表
     */
    @GetMapping("/posts/hot")
    public Result<java.util.List<Post>> getHotPosts(@RequestParam(defaultValue = "5") int limit) {
        return Result.ok(postService.getHotPosts(limit));
    }
}