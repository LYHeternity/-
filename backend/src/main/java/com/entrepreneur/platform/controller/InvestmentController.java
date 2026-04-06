package com.entrepreneur.platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneur.platform.common.Result;
import com.entrepreneur.platform.entity.Investment;
import com.entrepreneur.platform.service.InvestmentService;
import com.entrepreneur.platform.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 投资意向：提交、创业者查看/审核、投资人查看我的意向
 */
@RestController
@RequestMapping("/investment")
@RequiredArgsConstructor
public class InvestmentController {

    private final InvestmentService investmentService;

    @PostMapping
    public Result<Investment> submit(@RequestBody Investment investment) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        investment.setInvestorId(uid);
        return Result.ok(investmentService.submit(investment));
    }

    @GetMapping("/project/{projectId}")
    public Result<?> listByProject(@PathVariable Long projectId,
                                   @RequestParam(defaultValue = "1") long current,
                                   @RequestParam(defaultValue = "10") long size) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        return Result.ok(investmentService.listByProject(new Page<>(current, size), projectId));
    }

    @GetMapping("/my")
    public Result<?> myList(@RequestParam(defaultValue = "1") long current,
                            @RequestParam(defaultValue = "10") long size,
                            @RequestParam(required = false) String status) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        return Result.ok(investmentService.listByInvestor(new Page<>(current, size), uid, status));
    }

    @GetMapping("/{id}")
    public Result<Investment> detail(@PathVariable Long id) {
        Investment inv = investmentService.getById(id);
        if (inv == null) return Result.fail(404, "不存在");
        return Result.ok(inv);
    }

    @PostMapping("/{id}/audit")
    public Result<String> audit(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long uid = SecurityUtil.getCurrentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        Boolean approved = (Boolean) body.get("approved");
        String rejectReason = (String) body.get("rejectReason");
        investmentService.approve(id, Boolean.TRUE.equals(approved), rejectReason);
        return Result.ok();
    }
}
