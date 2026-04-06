package com.entrepreneur.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneur.platform.entity.Investment;
import com.entrepreneur.platform.mapper.InvestmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 投资意向：提交、审核、列表
 */
@Service
@RequiredArgsConstructor
public class InvestmentService {

    private final InvestmentMapper investmentMapper;

    @Transactional(rollbackFor = Exception.class)
    public Investment submit(Investment investment) {
        investment.setStatus("pending");
        investmentMapper.insert(investment);
        return investment;
    }

    public Investment getById(Long id) {
        return investmentMapper.selectById(id);
    }

    public IPage<Investment> listByProject(Page<Investment> page, Long projectId) {
        return investmentMapper.selectPage(page,
                new LambdaQueryWrapper<Investment>().eq(Investment::getProjectId, projectId).orderByDesc(Investment::getCreateTime));
    }

    public IPage<Investment> listByInvestor(Page<Investment> page, Long investorId, String status) {
        LambdaQueryWrapper<Investment> q = new LambdaQueryWrapper<Investment>().eq(Investment::getInvestorId, investorId);
        if (StringUtils.hasText(status)) q.eq(Investment::getStatus, status);
        q.orderByDesc(Investment::getCreateTime);
        return investmentMapper.selectPage(page, q);
    }

    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id, boolean approved, String rejectReason) {
        Investment inv = investmentMapper.selectById(id);
        if (inv != null) {
            inv.setStatus(approved ? "approved" : "rejected");
            inv.setRejectReason(rejectReason);
            investmentMapper.updateById(inv);
        }
    }
}
