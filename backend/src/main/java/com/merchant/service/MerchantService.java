package com.merchant.service;

import com.merchant.entity.Merchant;
import com.merchant.entity.MerchantStatus;
import com.merchant.repository.MerchantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MerchantService {
    @Autowired
    private MerchantRepository merchantRepository;

    public List<Merchant> getAllMerchants() {
        return merchantRepository.findAll();
    }

    public List<Merchant> getMerchantsByStatus(MerchantStatus status) {
        return merchantRepository.findByStatus(status);
    }

    @Transactional
    public Merchant updateMerchantStatus(Long id, MerchantStatus status, String rejectReason) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("商家不存在"));

        if (status == MerchantStatus.REJECTED && (rejectReason == null || rejectReason.trim().isEmpty())) {
            throw new IllegalArgumentException("拒绝时必须提供拒绝原因");
        }

        merchant.setStatus(status);
        if (status == MerchantStatus.REJECTED) {
            merchant.setRejectReason(rejectReason);
        } else {
            merchant.setRejectReason(null);
        }

        return merchantRepository.save(merchant);
    }

    public Map<MerchantStatus, Long> getMerchantStatusStatistics() {
        List<Merchant> merchants = merchantRepository.findAll();
        return merchants.stream()
                .collect(Collectors.groupingBy(Merchant::getStatus, Collectors.counting()));
    }
}
